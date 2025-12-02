// Scanner de Tarimas - Lógica principal

const API = '/api/tarimas';
let estadoActual = {
    tipo: null,
    codigo: null,
    preTarima: null,
    tarima: null,
    produccionActiva: null,
    estadoProductoSeleccionado: null
};

// Event listeners
document.addEventListener('DOMContentLoaded', () => {
    cargarEstadosProducto();
    cargarProduccionActiva();

    const inputEscaneado = document.getElementById('codigoEscaneado');
    const inputSlot = document.getElementById('codigoSlot');

    inputEscaneado.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            procesarEscaneo(inputEscaneado.value.trim());
            inputEscaneado.value = '';
        }
    });

    inputSlot.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            guardarTarimaEnSlot(inputSlot.value.trim());
        }
    });
});

/**
 * Carga los estados de producto disponibles
 */
async function cargarEstadosProducto() {
    try {
        const response = await fetch(`${API}/estados-producto`);
        const estados = await response.json();

        const select = document.getElementById('estadoProductoSelect');
        select.innerHTML = '';

        // Opción por defecto
        const defaultOption = document.createElement('option');
        defaultOption.value = 'TERMINADO';
        defaultOption.textContent = 'TERMINADO (Default)';
        defaultOption.selected = true;
        select.appendChild(defaultOption);

        // Agregar otros estados
        estados.forEach(estado => {
            if (estado.codigo !== 'TERMINADO') {
                const option = document.createElement('option');
                option.value = estado.codigo;
                option.textContent = estado.codigo;
                select.appendChild(option);
            }
        });

        estadoActual.estadoProductoSeleccionado = 'TERMINADO';
        select.addEventListener('change', (e) => {
            estadoActual.estadoProductoSeleccionado = e.target.value;
        });

    } catch (error) {
        console.error('Error cargando estados:', error);
        document.getElementById('estadoProductoSelect').innerHTML =
            '<option value="TERMINADO">TERMINADO (Error cargando)</option>';
        estadoActual.estadoProductoSeleccionado = 'TERMINADO';
    }
}

/**
 * Carga la producción activa
 */
async function cargarProduccionActiva() {
    try {
        const response = await fetch(`${API}/produccion-activa`);
        const data = await response.json();

        if (data.exitoso && data.produccionAlm) {
            estadoActual.produccionActiva = data.produccionAlm;
            mostrarProduccionActiva(data.produccionAlm);
        }
    } catch (error) {
        console.error('Error cargando producción activa:', error);
        // Ocultar la sección si hay error
        document.getElementById('produccionActivaSection').style.display = 'none';
    }
}

/**
 * Muestra la información de la producción activa
 */
function mostrarProduccionActiva(produccionAlm) {
    document.getElementById('productoActivo').textContent = produccionAlm.nombreProducto;
    document.getElementById('codigoProduccionAlm').textContent = produccionAlm.codigo;
    document.getElementById('numeroTarimas').textContent = 'T001 - ' + produccionAlm.numeroTariminasActual;
    document.getElementById('horaInicio').textContent = produccionAlm.horaInicio || 'Aún no iniciada';
    document.getElementById('produccionActivaSection').style.display = 'block';
}

/**
 * Confirma que la producción activa es la correcta
 */
function confirmarProduccionActiva() {
    if (estadoActual.produccionActiva) {
        mostrarSuccess(`✓ Producción confirmada: ${estadoActual.produccionActiva.codigo}`);
        document.getElementById('btnConfirmarProduccion').disabled = true;
        document.getElementById('codigoEscaneado').focus();
    }
}

/**
 * Procesa el código escaneado según su tipo
 */
async function procesarEscaneo(codigo) {
    limpiarMensajes();

    if (!codigo) {
        mostrarError('Código vacío');
        return;
    }

    const tipo = detectarTipoCodigo(codigo);
    estadoActual.tipo = tipo;
    estadoActual.codigo = codigo;

    mostrarLoading('Procesando...');

    try {
        switch (tipo) {
            case 'CAJA':
                await procesarCaja(codigo);
                break;
            case 'PRETARIMA':
                await procesarPreTarima(codigo);
                break;
            case 'MAQUINA':
                procesarMaquina(codigo);
                break;
            default:
                mostrarError('Código no reconocido');
        }
    } catch (error) {
        mostrarError('Error: ' + error.message);
    } finally {
        ocultarLoading();
    }
}

/**
 * Detecta el tipo de código escaneado
 */
function detectarTipoCodigo(codigo) {
    // Caja: 0XXXXXX251125 (comienza con 0 y tiene 13 dígitos)
    if (codigo.startsWith('0') && /^\d{13}$/.test(codigo)) {
        return 'CAJA';
    }

    // Pre-tarima: T001-PBG1-251125
    if (codigo.startsWith('T') && codigo.includes('-')) {
        const partes = codigo.split('-');
        if (partes.length === 3 && partes[0].match(/^T\d{3}$/)) {
            return 'PRETARIMA';
        }
    }

    // Máquina: M-XXXX
    if (codigo.startsWith('M-')) {
        return 'MAQUINA';
    }

    return 'DESCONOCIDO';
}

/**
 * Procesa escaneo de caja
 * Crea pre-tarima y genera PDF
 */
async function procesarCaja(codigoEscaneado) {
    const response = await fetch(`${API}/crear-pretarima`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            codigoEscaneado: codigoEscaneado,
            estadoProducto: estadoActual.estadoProductoSeleccionado || 'TERMINADO'
        })
    });

    const data = await response.json();

    if (data.exitoso) {
        const preTarima = data.preTarima;
        estadoActual.preTarima = preTarima;

        mostrarSuccess(`✓ Pre-Tarima creada: ${preTarima.codigo}`);
        mostrarEstado('CAJA', preTarima.codigo, preTarima.nombreProducto, 'Creada');

        // TODO: Generar e imprimir PDF
        // generarPDFPreTarima(preTarima);

        // Limpiar después de 2 segundos
        setTimeout(() => {
            document.getElementById('codigoEscaneado').focus();
        }, 2000);
    } else {
        mostrarError(data.error || 'Error al crear pre-tarima');
    }
}

/**
 * Procesa escaneo de pre-tarima
 * Abre modal bloqueante para guardar en slot
 */
async function procesarPreTarima(codigo) {
    const response = await fetch(`${API}/crear-tarima-desde-pretarima`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            codigoPreTarima: codigo
        })
    });

    const data = await response.json();

    if (data.exitoso) {
        const tarima = data.tarima;
        estadoActual.tarima = tarima;

        mostrarInfo(`Tarima creada: ${tarima.codigo}`);
        mostrarEstado('PRETARIMA', codigo, tarima.nombreProducto, 'Esperando slot');

        // Abrir modal bloqueante
        abrirModalGuardarSlot(tarima);
    } else {
        mostrarError(data.error || 'Error al crear tarima');
        document.getElementById('codigoEscaneado').focus();
    }
}

/**
 * Procesa escaneo de máquina
 * TODO: Implementar lógica de máquinas
 */
function procesarMaquina(codigo) {
    mostrarWarning('Máquinas aún no implementadas - ' + codigo);
    document.getElementById('codigoEscaneado').focus();
}

/**
 * Abre modal bloqueante para guardar tarima en slot
 */
function abrirModalGuardarSlot(tarima) {
    // Llenar info de tarima
    document.getElementById('preTarimaCode').textContent = tarima.codigo;
    document.getElementById('preTarimaProducto').textContent = tarima.nombreProducto;
    document.getElementById('preTarimaCajas').textContent = tarima.cantidadCajas;

    // Mostrar modal y configurar botones
    document.getElementById('modalSlot').classList.add('show');
    document.getElementById('btnCancelar').style.display = 'block';
    document.getElementById('btnNuevaEscaneada').style.display = 'none';
    document.getElementById('resultadoModal').style.display = 'none';

    // Limpiar campo y enfocar
    limpiarMensajesModal();
    document.getElementById('codigoSlot').value = '';
    document.getElementById('codigoSlot').focus();
}

/**
 * Guarda la tarima en el espacio del slot escaneado
 */
async function guardarTarimaEnSlot(codigoQr) {
    limpiarMensajesModal();

    if (!codigoQr) {
        mostrarErrorModal('Código QR vacío');
        return;
    }

    mostrarLoadingModal();

    try {
        // Obtener info del slot
        const infoResponse = await fetch(`${API}/info-slot/${encodeURIComponent(codigoQr)}`);
        const infoData = await infoResponse.json();

        if (!infoData.exitoso) {
            mostrarErrorModal(infoData.error || 'Slot no encontrado');
            ocultarLoadingModal();
            return;
        }

        if (infoData.espaciosDisponibles === 0) {
            mostrarWarningModal('⚠️ ' + infoData.sugerencia);
            ocultarLoadingModal();
            return;
        }

        // Guardar tarima en espacio
        const guardarResponse = await fetch(`${API}/guardar-en-espacio`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                tarimaId: estadoActual.tarima.id,
                espacioId: infoData.posicionDisponible
            })
        });

        const guardarData = await guardarResponse.json();
        ocultarLoadingModal();

        if (guardarData.exitoso) {
            // Mostrar resultado
            document.getElementById('ubicacionInfo').textContent = guardarData.tarima.ubicacion;
            document.getElementById('resultadoModal').style.display = 'block';
            document.getElementById('btnCancelar').style.display = 'none';
            document.getElementById('btnNuevaEscaneada').style.display = 'block';
            document.getElementById('codigoSlot').style.display = 'none';

            mostrarSuccess('✓ Tarima guardada exitosamente');
        } else {
            mostrarErrorModal(guardarData.error || 'Error al guardar');
        }
    } catch (error) {
        ocultarLoadingModal();
        mostrarErrorModal('Error de conexión: ' + error.message);
    }
}

/**
 * Cancela el guardado y limpia el modal
 */
function cancelarGuardado() {
    if (confirm('¿Cancelar sin guardar la tarima?')) {
        limpiarModalYVolver();
    }
}

/**
 * Limpia el modal y retorna a pantalla principal
 */
function limpiarModalYVolver() {
    document.getElementById('modalSlot').classList.remove('show');
    document.getElementById('estadoSection').style.display = 'none';
    limpiarMensajes();
    limpiarMensajesModal();
    estadoActual = { tipo: null, codigo: null, preTarima: null, tarima: null };
    document.getElementById('codigoEscaneado').value = '';
    document.getElementById('codigoEscaneado').focus();
}

/**
 * Muestra el estado actual del escaneo
 */
function mostrarEstado(tipo, codigo, producto, estado) {
    document.getElementById('tipoEscaneado').textContent = tipo;
    document.getElementById('codigoInfo').textContent = codigo;
    document.getElementById('productoInfo').textContent = producto;
    document.getElementById('estadoInfo').textContent = estado;
    document.getElementById('estadoSection').style.display = 'block';
}

/**
 * Funciones de mensajes
 */
function mostrarLoading(texto) {
    document.getElementById('loadingText').textContent = texto;
    document.getElementById('loading').style.display = 'block';
}

function ocultarLoading() {
    document.getElementById('loading').style.display = 'none';
}

function mostrarLoadingModal() {
    document.getElementById('loadingModal').style.display = 'block';
    document.getElementById('codigoSlot').style.display = 'none';
}

function ocultarLoadingModal() {
    document.getElementById('loadingModal').style.display = 'none';
    document.getElementById('codigoSlot').style.display = 'block';
}

function mostrarError(mensaje) {
    const elem = document.getElementById('errorMsg');
    elem.textContent = '❌ ' + mensaje;
    elem.classList.add('show');
}

function mostrarErrorModal(mensaje) {
    const elem = document.getElementById('errorModalMsg');
    elem.textContent = '❌ ' + mensaje;
    elem.classList.add('show');
}

function mostrarWarning(mensaje) {
    const elem = document.getElementById('warningMsg');
    elem.textContent = '⚠️ ' + mensaje;
    elem.classList.add('show');
}

function mostrarWarningModal(mensaje) {
    const elem = document.getElementById('warningModalMsg');
    elem.textContent = mensaje;
    elem.classList.add('show');
}

function mostrarSuccess(mensaje) {
    const elem = document.getElementById('successMsg');
    elem.textContent = '✓ ' + mensaje;
    elem.classList.add('show');
}

function mostrarInfo(mensaje) {
    const elem = document.getElementById('infoMsg');
    elem.textContent = 'ℹ️ ' + mensaje;
    elem.classList.add('show');
}

function limpiarMensajes() {
    document.getElementById('errorMsg').classList.remove('show');
    document.getElementById('warningMsg').classList.remove('show');
    document.getElementById('successMsg').classList.remove('show');
    document.getElementById('infoMsg').classList.remove('show');
}

function limpiarMensajesModal() {
    document.getElementById('errorModalMsg').classList.remove('show');
    document.getElementById('warningModalMsg').classList.remove('show');
}
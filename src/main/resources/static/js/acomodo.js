// Scanner de Tarimas - Lógica principal para acomodo.html

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
    console.log('Inicializando acomodo.js');
    cargarEstadosProducto();
    cargarProduccionActiva();
    inicializarAccordion();

    const inputEscaneado = document.getElementById('codigoEscaneado');
    const inputSlot = document.getElementById('codigoSlot');

    if (inputEscaneado) {
        inputEscaneado.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                procesarEscaneo(inputEscaneado.value.trim());
                inputEscaneado.value = '';
            }
        });
    }

    if (inputSlot) {
        inputSlot.addEventListener('keypress', (e) => {
            if (e.key === 'Enter') {
                guardarTarimaEnSlot(inputSlot.value.trim());
            }
        });
    }
});

/**
 * Carga los estados de producto disponibles
 */
async function cargarEstadosProducto() {
    try {
        console.log('Cargando estados de producto...');
        const response = await fetch(`${API}/estados-producto`);

        console.log('Status respuesta:', response.status);
        console.log('Content-Type:', response.headers.get('content-type'));

        if (!response.ok) {
            console.error('Error en respuesta:', response.status);
            const text = await response.text();
            console.error('Respuesta del servidor:', text);
            mostrarErrorCargaEstados('Error ' + response.status);
            return;
        }

        const text = await response.text();
        console.log('Texto recibido:', text);

        const estados = JSON.parse(text);
        console.log('Estados recibidos:', estados);

        const select = document.getElementById('estadoProductoSelect');
        if (!select) {
            console.warn('Select de estados no encontrado en DOM');
            return;
        }

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
            console.log('Estado seleccionado:', e.target.value);
        });

        console.log('Estados cargados exitosamente');

    } catch (error) {
        console.error('Error cargando estados:', error);
        mostrarErrorCargaEstados('Error de conexión');
    }
}

/**
 * Muestra error en carga de estados
 */
function mostrarErrorCargaEstados(mensaje) {
    const select = document.getElementById('estadoProductoSelect');
    if (select) {
        select.innerHTML = '<option value="TERMINADO">TERMINADO (Error: ' + mensaje + ')</option>';
        select.value = 'TERMINADO';
    }
    estadoActual.estadoProductoSeleccionado = 'TERMINADO';
}

/**
 * Carga la producción activa
 */
async function cargarProduccionActiva() {
    try {
        console.log('Cargando producción activa...');
        const response = await fetch(`${API}/produccion-activa`);

        console.log('Status respuesta:', response.status);
        console.log('URL final:', response.url);
        console.log('Content-Type:', response.headers.get('content-type'));

        // Si fue redirigido a login, la URL cambiará
        if (response.url.includes('/login')) {
            console.error('REDIRIGIDO A LOGIN - Usuario no autenticado o sin permiso');
            console.error('URL:', response.url);
            mostrarErrorCargaEstados('No autenticado - Vuelve a iniciar sesión');
            document.getElementById('produccionActivaSection').style.display = 'none';
            return;
        }

        if (!response.ok) {
            console.error('Error en respuesta:', response.status);
            const text = await response.text();
            console.error('Respuesta del servidor:', text.substring(0, 200));
            document.getElementById('produccionActivaSection').style.display = 'none';
            return;
        }

        const text = await response.text();
        console.log('Texto recibido:', text);

        const data = JSON.parse(text);
        console.log('Respuesta producción activa:', data);

        if (data.exitoso && data.produccionAlm) {
            estadoActual.produccionActiva = data.produccionAlm;
            // Mostrar confirmación solo si es la primera vez (sin pretarimas aún)
            if (data.produccionAlm.tienePretarimas === false) {
                mostrarProduccionActivaPendingConfirm(data.produccionAlm);
            } else {
                // Ya hay pre-tarimas, solo mostrar info sin botón de confirmación
                mostrarProduccionActivaConfirmada(data.produccionAlm);
            }
        } else {
            console.warn('No hay producción activa');
            document.getElementById('produccionActivaSection').style.display = 'none';
        }
    } catch (error) {
        console.error('Error cargando producción activa:', error);
        document.getElementById('produccionActivaSection').style.display = 'none';
    }
}

/**
 * Toggle para expandir/contraer la producción activa
 */
function toggleProduccionInfo() {
    const info = document.getElementById('produccionInfo');
    const btn = document.getElementById('btnToggle');

    if (info.style.display === 'none') {
        info.style.display = 'block';
        btn.textContent = '▲ Ocultar Producción';
    } else {
        info.style.display = 'none';
        btn.textContent = '▼ Ver Producción Activa';
    }
}

/**
 * Muestra la información de la producción activa PENDIENTE DE CONFIRMACIÓN
 */
function mostrarProduccionActivaPendingConfirm(produccionAlm) {
    const seccion = document.getElementById('produccionActivaSection');
    if (!seccion) return;

    document.getElementById('productoActivo').textContent = produccionAlm.nombreProducto || '-';
    document.getElementById('codigoProduccionAlm').textContent = produccionAlm.codigo || '-';

    const btn = document.getElementById('btnConfirmarProduccion');
    if (btn) {
        btn.style.display = 'block';
        btn.disabled = false;
    }

    seccion.style.display = 'block';

    // Mostrar expandido por defecto si hay que confirmar
    const produccionInfo = document.getElementById('produccionInfo');
    const btnToggle = document.getElementById('btnToggle');
    if (produccionInfo && btnToggle) {
        produccionInfo.style.display = 'block';
        btnToggle.textContent = '▲ Ocultar Producción';
    }

    console.log('Producción activa pendiente de confirmación:', produccionAlm.codigo);
}

/**
 * Muestra la información de la producción activa YA CONFIRMADA
 */
function mostrarProduccionActivaConfirmada(produccionAlm) {
    const seccion = document.getElementById('produccionActivaSection');
    if (!seccion) return;

    document.getElementById('productoActivo').textContent = produccionAlm.nombreProducto || '-';
    document.getElementById('codigoProduccionAlm').textContent = produccionAlm.codigo || '-';

    const btn = document.getElementById('btnConfirmarProduccion');
    if (btn) {
        btn.style.display = 'none';
    }

    seccion.style.display = 'block';

    // Mantener contraído por defecto si ya está confirmada
    const produccionInfo = document.getElementById('produccionInfo');
    const btnToggle = document.getElementById('btnToggle');
    if (produccionInfo && btnToggle) {
        produccionInfo.style.display = 'none';
        btnToggle.textContent = '▼ Ver Producción Activa';
    }

    console.log('Producción activa confirmada:', produccionAlm.codigo);
}

/**
 * Confirma que la producción activa es la correcta
 */
function confirmarProduccionActiva() {
    if (estadoActual.produccionActiva) {
        mostrarSuccess(`✓ Producción confirmada: ${estadoActual.produccionActiva.codigo}`);
        const btn = document.getElementById('btnConfirmarProduccion');
        if (btn) btn.style.display = 'none';

        const input = document.getElementById('codigoEscaneado');
        if (input) input.focus();
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

    console.log('Procesando código tipo:', tipo, 'código:', codigo);
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
        console.error('Error procesando:', error);
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
    const estadoProducto = estadoActual.estadoProductoSeleccionado || 'TERMINADO';
    console.log('Creando pre-tarima con estado:', estadoProducto);

    const response = await fetch(`${API}/crear-pretarima`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            codigoEscaneado: codigoEscaneado,
            estadoProducto: estadoProducto
        })
    });

    const data = await response.json();
    console.log('Respuesta pre-tarima:', data);

    if (data.exitoso) {
        const preTarima = data.preTarima;
        estadoActual.preTarima = preTarima;

        mostrarSuccess(`✓ Pre-Tarima creada: ${preTarima.codigo}`);
        mostrarEstado('CAJA', preTarima.codigo, preTarima.nombreProducto, 'Creada');

        // Limpiar después de 2 segundos
        setTimeout(() => {
            const input = document.getElementById('codigoEscaneado');
            if (input) input.focus();
        }, 2000);
    } else {
        mostrarError(data.error || 'Error al crear pre-tarima');
    }
}

/**
 * Procesa escaneo de pre-tarima
 * NO crea la tarima aún, solo abre modal para escanear slot
 */
async function procesarPreTarima(codigo) {
    // Obtener info de la pre-tarima sin crear tarima aún
    try {
        const response = await fetch(`${API}/obtener-pretarima/${codigo}`, {
            method: 'GET'
        });

        const data = await response.json();

        if (data.exitoso) {
            const preTarima = data.preTarima;
            estadoActual.preTarima = preTarima;

            mostrarInfo(`Pre-Tarima escaneada: ${preTarima.codigo}`);
            mostrarEstado('PRETARIMA', codigo, preTarima.nombreProducto, 'Esperando slot');

            // Abrir modal bloqueante SIN crear tarima aún
            abrirModalGuardarSlot(preTarima);
        } else {
            mostrarError(data.error || 'Pre-tarima no encontrada');
            const input = document.getElementById('codigoEscaneado');
            if (input) input.focus();
        }
    } catch (error) {
        console.error('Error:', error);
        mostrarError('Error al procesar pre-tarima');
    }
}

/**
 * Procesa escaneo de máquina
 * Obtiene info de la máquina y abre modal para confirmar consumo de insumo
 */
async function procesarMaquina(codigo) {
    console.log('Procesando máquina:', codigo);
    mostrarLoading('Buscando máquina...');

    try {
        const response = await fetch(`/api/insumos/maquinas/info/${encodeURIComponent(codigo)}`);
        const data = await response.json();

        ocultarLoading();

        if (data.exitoso) {
            const maquina = data.maquina;
            estadoActual.maquinaEscaneada = maquina;

            mostrarInfo(`Máquina escaneada: ${maquina.nombre}`);
            abrirModalConsumoInsumo(maquina);
        } else {
            mostrarError(data.error || 'Máquina no encontrada');
            const input = document.getElementById('codigoEscaneado');
            if (input) input.focus();
        }
    } catch (error) {
        ocultarLoading();
        console.error('Error:', error);
        mostrarError('Error al procesar máquina: ' + error.message);
    }
}

/**
 * Abre modal bloqueante para guardar tarima en slot
 */
function abrirModalGuardarSlot(preTarima) {
    document.getElementById('preTarimaCode').textContent = preTarima.codigo;
    document.getElementById('preTarimaProducto').textContent = preTarima.nombreProducto;
    document.getElementById('preTarimaCajas').textContent = preTarima.cantidadCajas;

    document.getElementById('modalSlot').classList.add('show');
    document.getElementById('btnCancelar').style.display = 'block';
    document.getElementById('btnNuevaEscaneada').style.display = 'none';
    document.getElementById('resultadoModal').style.display = 'none';

    limpiarMensajesModal();
    const inputSlot = document.getElementById('codigoSlot');
    if (inputSlot) {
        inputSlot.value = '';
        inputSlot.style.display = 'block';  // Asegurar que sea visible
        inputSlot.focus();
    }
}

/**
 * Completa el código QR si no tiene prefijo
 */
function completarCodigoQr(codigoQr) {
    const PREFIJO = '+C1';
    if (!codigoQr.startsWith(PREFIJO)) {
        console.log('Código QR sin prefijo, completando:', codigoQr);
        return PREFIJO + codigoQr;
    }
    return codigoQr;
}

/**
 * Guarda la tarima en el espacio PEPS automático del slot escaneado
 * AHORA sí crea la tarima después de validar y asignar el espacio
 */
async function guardarTarimaEnSlot(codigoQr) {
    limpiarMensajesModal();

    if (!codigoQr) {
        mostrarErrorModal('Código QR vacío');
        return;
    }

    // Completar código QR si no tiene prefijo
    codigoQr = completarCodigoQr(codigoQr);
    console.log('Procesando slot PEPS:', codigoQr);

    mostrarLoadingModal();

    try {
        // PASO 1: Validar que haya espacio disponible
        const infoResponse = await fetch(`${API}/info-slot/${encodeURIComponent(codigoQr)}`);
        const infoData = await infoResponse.json();

        console.log('Respuesta slot:', infoData);

        if (!infoData.exitoso) {
            mostrarErrorModal('⚠️ ' + infoData.error);
            ocultarLoadingModal();
            return;
        }

        // PASO 2: AHORA crear la tarima desde la pre-tarima
        const crearTarimaResponse = await fetch(`${API}/crear-tarima-desde-pretarima`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                codigoPreTarima: estadoActual.preTarima.codigo
            })
        });

        const tarimaData = await crearTarimaResponse.json();

        if (!tarimaData.exitoso) {
            mostrarErrorModal(tarimaData.error || 'Error al crear tarima');
            ocultarLoadingModal();
            return;
        }

        estadoActual.tarima = tarimaData.tarima;

        // PASO 3: Guardar la tarima en el espacio asignado
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
            document.getElementById('ubicacionInfo').textContent = guardarData.tarima.ubicacion;
            document.getElementById('resultadoModal').style.display = 'block';
            document.getElementById('btnCancelar').style.display = 'none';
            document.getElementById('btnNuevaEscaneada').style.display = 'block';
            const inputSlot = document.getElementById('codigoSlot');
            if (inputSlot) inputSlot.style.display = 'none';

            // Mostrar espacios restantes
            if (infoData.espaciosRestantes > 0) {
                mostrarSuccess(`✓ Tarima almacenada. Quedan ${infoData.espaciosRestantes} espacios`);
            } else {
                mostrarSuccess('✓ Tarima almacenada. Este nivel está COMPLETO');
            }
        } else {
            mostrarErrorModal(guardarData.error || 'Error al guardar');
        }
    } catch (error) {
        ocultarLoadingModal();
        mostrarErrorModal('Error de conexión: ' + error.message);
    }
}

/**
 * Cancela el guardado y limpia el modal SIN guardar la tarima
 */
function cancelarGuardado() {
    if (confirm('¿Cancelar sin guardar la tarima? Solo se mantendrá la pre-tarima.')) {
        limpiarModalYVolver();
    }
}

/**
 * Limpia el modal y retorna a pantalla principal
 */
function limpiarModalYVolver() {
    document.getElementById('modalSlot').classList.remove('show');
    const estadoSection = document.getElementById('estadoSection');
    if (estadoSection) estadoSection.style.display = 'none';

    limpiarMensajes();
    limpiarMensajesModal();
    estadoActual = {
        tipo: null,
        codigo: null,
        preTarima: null,
        tarima: null,
        produccionActiva: estadoActual.produccionActiva,
        estadoProductoSeleccionado: estadoActual.estadoProductoSeleccionado
    };

    const input = document.getElementById('codigoEscaneado');
    if (input) {
        input.value = '';
        input.focus();
    }
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
    const elem = document.getElementById('loading');
    if (elem) {
        const textElem = document.getElementById('loadingText');
        if (textElem) textElem.textContent = texto;
        elem.style.display = 'block';
    }
}

function ocultarLoading() {
    const elem = document.getElementById('loading');
    if (elem) elem.style.display = 'none';
}

function mostrarLoadingModal() {
    const elem = document.getElementById('loadingModal');
    const inputSlot = document.getElementById('codigoSlot');
    if (elem) elem.style.display = 'block';
    if (inputSlot) inputSlot.style.display = 'none';
}

function ocultarLoadingModal() {
    const elem = document.getElementById('loadingModal');
    const inputSlot = document.getElementById('codigoSlot');
    if (elem) elem.style.display = 'none';
    if (inputSlot) inputSlot.style.display = 'block';
}

function mostrarError(mensaje) {
    const elem = document.getElementById('errorMsg');
    if (elem) {
        elem.textContent = '❌ ' + mensaje;
        elem.classList.add('show');
    }
}

function mostrarErrorModal(mensaje) {
    const elem = document.getElementById('errorModalMsg');
    if (elem) {
        elem.textContent = '❌ ' + mensaje;
        elem.classList.add('show');
    }
}

function mostrarWarning(mensaje) {
    const elem = document.getElementById('warningMsg');
    if (elem) {
        elem.textContent = '⚠️ ' + mensaje;
        elem.classList.add('show');
    }
}

function mostrarWarningModal(mensaje) {
    const elem = document.getElementById('warningModalMsg');
    if (elem) {
        elem.textContent = mensaje;
        elem.classList.add('show');
    }
}

function mostrarSuccess(mensaje) {
    const elem = document.getElementById('successMsg');
    if (elem) {
        elem.textContent = '✓ ' + mensaje;
        elem.classList.add('show');
    }
}

function mostrarInfo(mensaje) {
    const elem = document.getElementById('infoMsg');
    if (elem) {
        elem.textContent = 'ℹ️ ' + mensaje;
        elem.classList.add('show');
    }
}

function limpiarMensajes() {
    const elementos = ['errorMsg', 'warningMsg', 'successMsg', 'infoMsg'];
    elementos.forEach(id => {
        const elem = document.getElementById(id);
        if (elem) elem.classList.remove('show');
    });
}

function limpiarMensajesModal() {
    const elementos = ['errorModalMsg', 'warningModalMsg'];
    elementos.forEach(id => {
        const elem = document.getElementById(id);
        if (elem) elem.classList.remove('show');
    });
}

/**
 * Abre modal para confirmar consumo de insumo
 */
function abrirModalConsumoInsumo(maquina) {
    document.getElementById('maquinaEscaneada').textContent = maquina.codigo;
    document.getElementById('consumoMaquinaNombre').textContent = maquina.nombre;
    document.getElementById('consumoInsumoNombre').textContent = maquina.insumo.codigo + ' - ' + maquina.insumo.descripcion;

    document.getElementById('modalConsumoInsumo').classList.add('show');

    // Limpiar mensajes
    limpiarErrorConsumo();
    console.log('Modal abierto para máquina:', maquina.codigo);
}

/**
 * Cancela el registro de consumo
 */
function cancelarConsumoInsumo() {
    document.getElementById('modalConsumoInsumo').classList.remove('show');
    estadoActual.maquinaEscaneada = null;

    const input = document.getElementById('codigoEscaneado');
    if (input) {
        input.value = '';
        input.focus();
    }
}

/**
 * Confirma y registra el consumo de insumo
 */
async function confirmarConsumoInsumo() {
    if (!estadoActual.maquinaEscaneada) {
        mostrarErrorConsumo('Error: Máquina no válida');
        return;
    }

    const maquina = estadoActual.maquinaEscaneada;
    console.log('Registrando consumo para:', maquina.codigo);

    try {
        const response = await fetch('/api/insumos/registrar-consumo', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                maquinaId: maquina.id,
                insumoId: maquina.insumo.id,
                produccionAlmId: estadoActual.produccionActiva?.id || null
            })
        });

        const data = await response.json();

        if (data.exitoso) {
            document.getElementById('modalConsumoInsumo').classList.remove('show');
            mostrarSuccess(`✓ Consumo registrado: ${maquina.insumo.codigo}`);

            // Limpiar input
            const input = document.getElementById('codigoEscaneado');
            if (input) {
                input.value = '';
                setTimeout(() => input.focus(), 500);
            }
        } else {
            mostrarErrorConsumo(data.error || 'Error al registrar consumo');
        }
    } catch (error) {
        console.error('Error:', error);
        mostrarErrorConsumo('Error de conexión: ' + error.message);
    }
}

/**
 * Muestra error en modal de consumo
 */
function mostrarErrorConsumo(mensaje) {
    const elem = document.getElementById('errorConsumoMsg');
    if (elem) {
        elem.textContent = '❌ ' + mensaje;
        elem.classList.add('show');
    }
}

/**
 * Limpia errores del modal de consumo
 */
function limpiarErrorConsumo() {
    const elem = document.getElementById('errorConsumoMsg');
    if (elem) elem.classList.remove('show');
}

/**
 * Inicializa la funcionalidad de accordion para la caja de información
 */
function inicializarAccordion() {
    const infoBox = document.querySelector('.info-box');
    const infoBoxH3 = document.querySelector('.info-box h3');
    const infoBoxUl = document.getElementById('infoBoxContent');

    if (!infoBox || !infoBoxH3 || !infoBoxUl) {
        console.warn('Elementos del accordion no encontrados');
        return;
    }

    // Estado del accordion
    let isExpanded = false;

    // Estilo del título
    infoBoxH3.style.cursor = 'pointer';
    infoBoxH3.style.userSelect = 'none';
    infoBoxH3.style.transition = 'all 0.3s ease';

    // Click en el título para expandir/contraer
    infoBoxH3.addEventListener('click', (e) => {
        e.stopPropagation();
        isExpanded = !isExpanded;

        if (isExpanded) {
            infoBoxUl.style.display = 'block';
            infoBoxH3.style.opacity = '1';
        } else {
            infoBoxUl.style.display = 'none';
            infoBoxH3.style.opacity = '0.7';
        }
    });

    // Click fuera para contraer
    document.addEventListener('click', (e) => {
        if (!infoBox.contains(e.target) && isExpanded) {
            isExpanded = false;
            infoBoxUl.style.display = 'none';
            infoBoxH3.style.opacity = '0.7';
        }
    });

    // Estado inicial
    infoBoxH3.style.opacity = '0.7';
    console.log('Accordion inicializado');
}
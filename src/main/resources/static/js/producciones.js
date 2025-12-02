/**
 * Gestión de Producciones - Script Principal
 */

const API_PRODUCCIONES = '/api/producciones';
const API_PRODUCCION_ALM = '/api/produccion-alm';
const API_PRODUCTOS = '/api/productos/activos';

let todasProducciones = [];
let produccionesAlmActual = [];
let produccionActualId = null;
let produccionAlmParaEliminar = null;
let tipoEliminacion = null; // 'produccion' o 'alm'

// Elementos del DOM
const modalDetalles = document.getElementById('modalDetalles');
const modalAlm = document.getElementById('modalAlm');
const modalConfirmar = document.getElementById('modalConfirmar');
const btnNuevaProduccion = document.getElementById('btnNuevaProduccion');
const btnNuevaProduccionEmpty = document.getElementById('btnNuevaProduccionEmpty');
const btnAgregarAlm = document.getElementById('btnAgregarAlm');
const btnCerrarDetalles = document.getElementById('btnCerrarDetalles');
const btnCerrarDetallesBtn = document.getElementById('btnCerrarDetallesBtn');
const btnCerrarAlm = document.getElementById('btnCerrarAlm');
const btnCancelarAlm = document.getElementById('btnCancelarAlm');
const btnCancelarConfirm = document.getElementById('btnCancelarConfirm');
const btnConfirmarDelete = document.getElementById('btnConfirmarDelete');
const formularioAlm = document.getElementById('formularioAlm');
const produccionesTableBody = document.getElementById('produccionesTableBody');
const produccionesTable = document.getElementById('produccionesTable');
const emptyState = document.getElementById('emptyState');
const detallesTableBody = document.getElementById('detallesTableBody');
const notification = document.getElementById('notification');

// Event Listeners
document.addEventListener('DOMContentLoaded', () => {
    cargarProducciones();
    cargarProductos();
});

btnNuevaProduccion.addEventListener('click', () => crearProduccion());
btnNuevaProduccionEmpty.addEventListener('click', () => crearProduccion());
btnAgregarAlm.addEventListener('click', () => abrirModalNuevoAlm());
btnCerrarDetalles.addEventListener('click', () => cerrarModalDetalles());
btnCerrarDetallesBtn.addEventListener('click', () => cerrarModalDetalles());
btnCerrarAlm.addEventListener('click', () => cerrarModalAlm());
btnCancelarAlm.addEventListener('click', () => cerrarModalAlm());
formularioAlm.addEventListener('submit', (e) => guardarAlm(e));

modalDetalles.addEventListener('click', (e) => {
    if (e.target === modalDetalles) cerrarModalDetalles();
});

modalAlm.addEventListener('click', (e) => {
    if (e.target === modalAlm) cerrarModalAlm();
});

modalConfirmar.addEventListener('click', (e) => {
    if (e.target === modalConfirmar) cerrarModalConfirm();
});

/**
 * Cargar todas las producciones
 */
async function cargarProducciones() {
    try {
        const response = await fetch(API_PRODUCCIONES + '/activas');
        const data = await response.json();

        if (data.success) {
            todasProducciones = data.data;
            mostrarProducciones(todasProducciones);
        } else {
            mostrarNotificacion('Error al cargar producciones', 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        mostrarNotificacion('Error al conectar con el servidor', 'error');
    }

    // Cargar inactivas siempre, aunque no haya activas
    cargarProduccionesInactivas();
}

/**
 * Cargar productos para el select
 */
async function cargarProductos() {
    try {
        const response = await fetch(API_PRODUCTOS);
        const data = await response.json();

        if (data.success) {
            const select = document.getElementById('almProducto');
            select.innerHTML = '<option value="">Seleccionar producto...</option>';
            data.data.forEach(producto => {
                const option = document.createElement('option');
                option.value = producto.id;
                option.textContent = producto.nombre + ' (' + producto.codigo + ')';
                select.appendChild(option);
            });
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

/**
 * Mostrar producciones en la tabla
 */
function mostrarProducciones(producciones) {
    if (producciones.length === 0) {
        produccionesTable.classList.add('hidden');
        emptyState.classList.remove('hidden');
        return;
    }

    produccionesTable.classList.remove('hidden');
    emptyState.classList.add('hidden');

    produccionesTableBody.innerHTML = producciones.map(produccion => {
        const fecha = new Date(produccion.fecha).toLocaleDateString('es-ES');
        return `
            <tr>
                <td>${produccion.id}</td>
                <td>${fecha}</td>
                <td>
                    <span class="badge-activo">Activa</span>
                </td>
                <td><span id="countAlm-${produccion.id}">-</span></td>
                <td>
                    <div class="btn-actions">
                        <button class="btn-action btn-view" onclick="verDetalles(${produccion.id})">👁️ Ver</button>
                        <button id="btn-accion-${produccion.id}" class="btn-action btn-terminate" onclick="eliminarOTerminarProduccion(${produccion.id})">⏹️ Terminar</button>
                    </div>
                </td>
            </tr>
        `;
    }).join('');

    // Cargar contar de almacenes para cada producción
    producciones.forEach(produccion => {
        cargarConteoAlm(produccion.id);
    });
}

/**
 * Cargar producciones inactivas
 */
async function cargarProduccionesInactivas() {
    try {
        const response = await fetch(API_PRODUCCIONES);
        const data = await response.json();

        if (data.success) {
            // Filtrar inactivas, ordenar descendente y tomar últimas 10
            const inactivas = data.data
                .filter(p => !p.activo)
                .sort((a, b) => b.id - a.id)
                .slice(0, 10);
            mostrarProduccionesInactivas(inactivas);
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

/**
 * Mostrar producciones inactivas
 */
function mostrarProduccionesInactivas(inactivas) {
    const container = document.getElementById('inactivasTableBody');
    if (!container) return;

    if (inactivas.length === 0) {
        container.innerHTML = '<tr><td colspan="3" class="text-center">No hay producciones inactivas</td></tr>';
        return;
    }

    container.innerHTML = inactivas.map(produccion => {
        const fecha = new Date(produccion.fecha).toLocaleDateString('es-ES');
        return `
            <tr>
                <td>${produccion.id}</td>
                <td>${fecha}</td>
                <td>
                    <button class="btn-action btn-view" onclick="verDetalles(${produccion.id})">👁️ Ver</button>
                </td>
            </tr>
        `;
    }).join('');
}
async function cargarConteoAlm(produccionId) {
    try {
        const response = await fetch(`${API_PRODUCCION_ALM}/produccion/${produccionId}`);
        const data = await response.json();
        if (data.success) {
            const count = data.count || 0;
            const element = document.getElementById(`countAlm-${produccionId}`);
            const boton = document.getElementById(`btn-accion-${produccionId}`);

            if (element) {
                element.textContent = count + ' asignado(s)';
            }

            // Cambiar texto del botón
            if (boton) {
                if (count > 0) {
                    boton.textContent = '⏹️ Terminar';
                } else {
                    boton.textContent = '🗑️ Eliminar';
                }
            }
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

/**
 * Crear nueva producción
 */
function crearProduccion() {
    // Verificar si hay alguna producción activa
    const tieneActiva = todasProducciones.some(p => p.activo);
    if (tieneActiva) {
        mostrarNotificacion('Solo puede haber una producción ACTIVA a la vez. Termine la actual primero.', 'error');
        return;
    }

    // Mostrar modal para ingresar fecha
    const fecha = prompt('Ingrese la fecha de la producción (YYYY-MM-DD):', new Date().toISOString().split('T')[0]);

    if (fecha) {
        if (confirm('¿Está seguro de que desea crear una nueva producción con fecha ' + fecha + '?')) {
            crearProduccionConfirmado(fecha);
        }
    }
}

async function crearProduccionConfirmado(fecha) {
    try {
        const produccion = {
            fecha: fecha + 'T00:00:00'
        };

        const response = await fetch(API_PRODUCCIONES, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(produccion)
        });

        const data = await response.json();

        if (data.success) {
            mostrarNotificacion(data.message, 'success');
            await cargarProducciones();
        }
    } catch (error) {
        mostrarNotificacion('Error al crear producción', 'error');
    }
}

/**
 * Eliminar o terminar producción según tenga ProduccionAlm
 */
async function eliminarOTerminarProduccion(produccionId) {
    try {
        // Verificar si tiene ProduccionAlm
        const response = await fetch(`${API_PRODUCCION_ALM}/produccion/${produccionId}`);
        const data = await response.json();

        if (data.success && data.count > 0) {
            // Tiene datos, terminar
            terminarProduccion(produccionId);
        } else {
            // Está vacía, eliminar
            if (confirm('¿Está seguro de que desea eliminar esta producción vacía?')) {
                const deleteResponse = await fetch(`${API_PRODUCCIONES}/${produccionId}`, {
                    method: 'DELETE',
                    headers: { 'Content-Type': 'application/json' }
                });

                const deleteData = await deleteResponse.json();

                if (deleteData.success) {
                    mostrarNotificacion(deleteData.message, 'success');
                    await cargarProducciones();
                } else {
                    mostrarNotificacion(deleteData.message || 'Error al eliminar', 'error');
                }
            }
        }
    } catch (error) {
        mostrarNotificacion('Error', 'error');
    }
}
async function terminarProduccion(produccionId) {
    try {
        // Verificar que todas las ProduccionAlm estén terminadas
        const checkResponse = await fetch(`${API_PRODUCCION_ALM}/produccion/${produccionId}`);
        const checkData = await checkResponse.json();

        if (checkData.success) {
            const tieneActivas = checkData.data.some(alm => alm.estatus === 'ACTIVA');
            if (tieneActivas) {
                mostrarNotificacion('Debe terminar todos los almacenes antes de terminar la producción', 'error');
                return;
            }
        }

        if (!confirm('¿Está seguro de que desea terminar esta producción?')) {
            return;
        }

        const produccion = { activo: false };
        const response = await fetch(`${API_PRODUCCIONES}/${produccionId}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(produccion)
        });

        const data = await response.json();

        if (data.success) {
            mostrarNotificacion(data.message, 'success');
            await cargarProducciones();
            cerrarModalDetalles();
        } else {
            mostrarNotificacion(data.message || 'Error al terminar', 'error');
        }
    } catch (error) {
        mostrarNotificacion('Error al terminar producción', 'error');
    }
}
async function verDetalles(produccionId) {
    produccionActualId = produccionId;
    document.getElementById('detallesProduccionId').textContent = produccionId;

    try {
        const response = await fetch(`${API_PRODUCCION_ALM}/produccion/${produccionId}`);
        const data = await response.json();

        if (data.success) {
            produccionesAlmActual = data.data;
            mostrarDetalles(produccionesAlmActual);
            modalDetalles.classList.remove('hidden');
        }
    } catch (error) {
        mostrarNotificacion('Error al cargar detalles', 'error');
    }
}

/**
 * Mostrar detalles en tabla
 */
function mostrarDetalles(detalles) {
    detallesTableBody.innerHTML = detalles.map(alm => {
        const horaInicio = alm.horaInicio ? new Date(alm.horaInicio).toLocaleString('es-ES') : '-';
        const horaFin = alm.horaFin ? new Date(alm.horaFin).toLocaleString('es-ES') : '-';

        let botonesAccion = '';
        if (alm.estatus === 'ACTIVA') {
            botonesAccion = `<button class="btn-action btn-terminate" onclick="abrirModalTerminarAlm(${alm.id})">⏹️ Terminar</button>`;
        }

        return `
            <tr>
                <td>${alm.id}</td>
                <td>${alm.codigo}</td>
                <td>${alm.producto.nombre}</td>
                <td>${horaInicio}</td>
                <td>${horaFin}</td>
                <td>${alm.cajasIniciales}</td>
                <td>${alm.cajasFinales}</td>
                <td>${alm.estatus}</td>
                <td>
                    <div class="btn-actions">
                        ${botonesAccion}
                    </div>
                </td>
            </tr>
        `;
    }).join('');
}

/**
 * Abrir modal para nuevo almacén
 */
function abrirModalNuevoAlm() {
    if (!produccionActualId) {
        mostrarNotificacion('Selecciona una producción primero', 'error');
        return;
    }

    // Verificar si hay una activa
    const tieneActiva = produccionesAlmActual.some(alm => alm.estatus === 'ACTIVA');
    if (tieneActiva) {
        mostrarNotificacion('Solo puede haber una almacén ACTIVA a la vez', 'error');
        return;
    }

    limpiarFormularioAlm();
    document.getElementById('almTitle').textContent = 'Nuevo Almacén';
    document.getElementById('almProduccionId').value = produccionActualId;
    document.getElementById('almId').value = '';

    // Mostrar campos de crear
    document.getElementById('almProducto').parentElement.style.display = 'flex';
    document.getElementById('almCajasIniciales').parentElement.style.display = 'flex';

    // Ocultar formTerminar
    document.getElementById('formTerminar').style.display = 'none';

    modalAlm.classList.remove('hidden');
}

/**
 * Abrir modal para terminar almacén
 */
function abrirModalTerminarAlm(almId) {
    // Buscar el almacén en la lista actual
    const alm = produccionesAlmActual.find(a => a.id === almId);
    if (!alm) return;

    document.getElementById('almTitle').textContent = 'Terminar Almacén';
    document.getElementById('almId').value = almId;
    document.getElementById('almProduccionId').value = produccionActualId;

    // Ocultar campos de crear
    document.getElementById('almProducto').parentElement.style.display = 'none';
    document.getElementById('almCajasIniciales').parentElement.style.display = 'none';

    // Mostrar formTerminar
    document.getElementById('formTerminar').style.display = 'block';

    // Llenar valores
    document.getElementById('almHoraFin').value = alm.horaFin ? alm.horaFin.slice(0, 16) : '';
    document.getElementById('almCajasFinales').value = alm.cajasFinales;

    modalAlm.classList.remove('hidden');
}
async function editarAlm(almId) {
    try {
        const response = await fetch(`${API_PRODUCCION_ALM}/${almId}`);
        const data = await response.json();

        if (data.success) {
            const alm = data.data;
            llenarFormularioAlm(alm);
            document.getElementById('almTitle').textContent = 'Editar Almacén';
            document.getElementById('almId').value = almId;
            modalAlm.classList.remove('hidden');
        }
    } catch (error) {
        mostrarNotificacion('Error al cargar almacén', 'error');
    }
}

/**
 * Llenar formulario de almacén
 */
function llenarFormularioAlm(alm) {
    document.getElementById('almCodigo').value = alm.codigo;
    document.getElementById('almProducto').value = alm.producto.id;
    document.getElementById('almHoraInicio').value = alm.horaInicio.slice(0, 16);
    document.getElementById('almHoraFin').value = alm.horaFin ? alm.horaFin.slice(0, 16) : '';
    document.getElementById('almCajasIniciales').value = alm.cajasIniciales;
    document.getElementById('almCajasFinales').value = alm.cajasFinales;
    document.getElementById('almEstatus').value = alm.estatus;
}

/**
 * Limpiar formulario de almacén
 */
function limpiarFormularioAlm() {
    formularioAlm.reset();
    document.getElementById('almId').value = '';
    document.querySelectorAll('.error-message').forEach(el => {
        el.classList.remove('show');
        el.textContent = '';
    });
}

/**
 * Cerrar modal de detalles
 */
function cerrarModalDetalles() {
    modalDetalles.classList.add('hidden');
    produccionActualId = null;
}

/**
 * Cerrar modal de almacén
 */
function cerrarModalAlm() {
    modalAlm.classList.add('hidden');
    limpiarFormularioAlm();
    // Resetear visibilidad de campos
    document.getElementById('almProducto').parentElement.style.display = 'flex';
    document.getElementById('almCajasIniciales').parentElement.style.display = 'flex';
    document.getElementById('formTerminar').style.display = 'none';
}

/**
 * Guardar almacén
 */
async function guardarAlm(e) {
    e.preventDefault();

    const almId = document.getElementById('almId').value;
    const produccionId = document.getElementById('almProduccionId').value;
    const modalTitle = document.getElementById('almTitle').textContent;

    let alm;

    if (modalTitle === 'Terminar Almacén') {
        // Solo actualizar hora fin, cajas finales y estatus
        alm = {
            horaFin: document.getElementById('almHoraFin').value ? document.getElementById('almHoraFin').value + ':00' : null,
            cajasFinales: parseInt(document.getElementById('almCajasFinales').value) || 0,
            estatus: 'TERMINADA'
        };
    } else {
        // Obtener producto ID
        const productoId = parseInt(document.getElementById('almProducto').value);

        // Generar hora actual automáticamente
        const ahora = new Date();
        const horaActualISO = ahora.toISOString().slice(0, 19);

        alm = {
            codigo: "",
            producto: { id: productoId },
            produccion: { id: parseInt(produccionId) },
            horaInicio: horaActualISO,
            horaFin: null,
            cajasIniciales: parseInt(document.getElementById('almCajasIniciales').value) || 0,
            cajasFinales: 0,
            estatus: 'ACTIVA'
        };

        // Validar
        if (!productoId) {
            mostrarError('errorAlmProducto', 'El producto es requerido');
            return;
        }
    }

    try {
        let response;

        if (almId) {
            response = await fetch(`${API_PRODUCCION_ALM}/${almId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(alm)
            });
        } else {
            response = await fetch(API_PRODUCCION_ALM, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(alm)
            });
        }

        const data = await response.json();

        if (data.success) {
            mostrarNotificacion(data.message, 'success');
            cerrarModalAlm();
            await verDetalles(produccionId);
            await cargarConteoAlm(produccionId);
        } else {
            mostrarNotificacion(data.message || 'Error al guardar', 'error');
        }
    } catch (error) {
        mostrarNotificacion('Error al guardar almacén', 'error');
    }
}

/**
 * Validar formulario
 */
function validarFormularioAlm(alm) {
    let esValido = true;

    document.querySelectorAll('.error-message').forEach(el => {
        el.classList.remove('show');
        el.textContent = '';
    });

    if (!alm.codigo) {
        mostrarError('errorAlmCodigo', 'El código es requerido');
        esValido = false;
    }

    if (!alm.producto.id) {
        mostrarError('errorAlmProducto', 'El producto es requerido');
        esValido = false;
    }

    if (!alm.horaInicio) {
        mostrarError('errorAlmHoraInicio', 'La hora de inicio es requerida');
        esValido = false;
    }

    return esValido;
}

/**
 * Mostrar error
 */
function mostrarError(elementoId, mensaje) {
    const elemento = document.getElementById(elementoId);
    if (elemento) {
        elemento.textContent = mensaje;
        elemento.classList.add('show');
    }
}

/**
 * Mostrar notificación
 */
function mostrarNotificacion(mensaje, tipo = 'info') {
    notification.textContent = mensaje;
    notification.className = `notification ${tipo}`;

    setTimeout(() => {
        notification.classList.add('hidden');
    }, 3500);
}
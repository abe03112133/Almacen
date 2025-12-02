/**
 * Gestión de Productos - Script Principal
 */

const API_URL = '/api/productos';
let productoParaEliminar = null;
let todoProductos = [];

// Elementos del DOM
const modalProducto = document.getElementById('modalProducto');
const formularioProducto = document.getElementById('formularioProducto');
const productId = document.getElementById('productId');
const btnNuevoProducto = document.getElementById('btnNuevoProducto');
const btnNuevoProductoEmpty = document.getElementById('btnNuevoProductoEmpty');
const btnCerrarModal = document.getElementById('btnCerrarModal');
const btnCancelar = document.getElementById('btnCancelar');
const productosTableBody = document.getElementById('productosTableBody');
const productosTable = document.getElementById('productosTable');
const emptyState = document.getElementById('emptyState');
const searchInput = document.getElementById('searchInput');
const btnBuscar = document.getElementById('btnBuscar');
const notification = document.getElementById('notification');
const modalConfirmar = document.getElementById('modalConfirmar');
const btnCancelarConfirm = document.getElementById('btnCancelarConfirm');
const btnConfirmarDelete = document.getElementById('btnConfirmarDelete');

// Event Listeners
document.addEventListener('DOMContentLoaded', () => {
    cargarProductos();
});

btnNuevoProducto.addEventListener('click', () => abrirModalNuevo());
btnNuevoProductoEmpty.addEventListener('click', () => abrirModalNuevo());
btnCerrarModal.addEventListener('click', () => cerrarModal());
btnCancelar.addEventListener('click', () => cerrarModal());
btnCancelarConfirm.addEventListener('click', () => cerrarModalConfirm());
btnConfirmarDelete.addEventListener('click', () => confirmarEliminar());
formularioProducto.addEventListener('submit', (e) => guardarProducto(e));
btnBuscar.addEventListener('click', () => buscarProductos());
searchInput.addEventListener('keypress', (e) => {
    if (e.key === 'Enter') buscarProductos();
});
document.getElementById('btnLimpiar').addEventListener('click', () => limpiarBusqueda());

// Cerrar modal al hacer click fuera de él
modalProducto.addEventListener('click', (e) => {
    if (e.target === modalProducto) cerrarModal();
});

modalConfirmar.addEventListener('click', (e) => {
    if (e.target === modalConfirmar) cerrarModalConfirm();
});

/**
 * Cargar todos los productos
 */
async function cargarProductos() {
    try {
        const response = await fetch(API_URL);
        const data = await response.json();

        if (data.success) {
            todoProductos = data.data;
            mostrarProductos(todoProductos);
        } else {
            mostrarNotificacion('Error al cargar productos', 'error');
        }
    } catch (error) {
        console.error('Error:', error);
        mostrarNotificacion('Error al conectar con el servidor', 'error');
    }
}

/**
 * Mostrar productos en la tabla
 */
function mostrarProductos(productos) {
    if (productos.length === 0) {
        productosTable.classList.add('hidden');
        emptyState.classList.remove('hidden');
        return;
    }

    productosTable.classList.remove('hidden');
    emptyState.classList.add('hidden');

    productosTableBody.innerHTML = productos.map(producto => `
        <tr>
            <td>${producto.id}</td>
            <td><strong>${producto.codigo}</strong></td>
            <td>${producto.nombre}</td>
            <td>${producto.abreviacion || '-'}</td>
            <td>${producto.piezasPorCaja}</td>
            <td>
                ${producto.colorDisplay ? `<div style="width: 24px; height: 24px; background-color: ${producto.colorDisplay}; border-radius: 4px; border: 1px solid #ddd;"></div>` : '-'}
            </td>
            <td>
                <span class="badge-${producto.activo ? 'activo' : 'inactivo'}">
                    ${producto.activo ? 'Activo' : 'Inactivo'}
                </span>
            </td>
            <td>
                <div class="btn-actions">
                    <button class="btn-action btn-edit" onclick="editarProducto(${producto.id})">✏️ Editar</button>
                </div>
            </td>
        </tr>
    `).join('');
}

/**
 * Buscar productos
 */
function buscarProductos() {
    const termino = searchInput.value.toLowerCase().trim();

    if (termino === '') {
        mostrarProductos(todoProductos);
        return;
    }

    const productosFilteados = todoProductos.filter(producto =>
        producto.codigo.toLowerCase().includes(termino) ||
        producto.nombre.toLowerCase().includes(termino)
    );

    mostrarProductos(productosFilteados);
}

/**
 * Limpiar búsqueda
 */
function limpiarBusqueda() {
    searchInput.value = '';
    mostrarProductos(todoProductos);
}

/**
 * Abrir modal para nuevo producto
 */
function abrirModalNuevo() {
    limpiarFormulario();
    document.getElementById('modalTitle').textContent = 'Nuevo Producto';
    productId.value = '';
    modalProducto.classList.remove('hidden');
}

/**
 * Editar producto
 */
async function editarProducto(id) {
    try {
        const response = await fetch(`${API_URL}/${id}`);
        const data = await response.json();

        if (data.success) {
            const producto = data.data;
            llenarFormulario(producto);
            document.getElementById('modalTitle').textContent = 'Editar Producto';
            productId.value = id;
            modalProducto.classList.remove('hidden');
        }
    } catch (error) {
        console.error('Error:', error);
        mostrarNotificacion('Error al cargar el producto', 'error');
    }
}

/**
 * Llenar formulario con datos del producto
 */
function llenarFormulario(producto) {
    document.getElementById('codigo').value = producto.codigo;
    document.getElementById('nombre').value = producto.nombre;
    document.getElementById('abreviacion').value = producto.abreviacion || '';
    document.getElementById('piezasPorCaja').value = producto.piezasPorCaja;
    document.getElementById('colorDisplay').value = producto.colorDisplay || '#1a1a1a';
    document.getElementById('activo').value = producto.activo;
}

/**
 * Limpiar formulario
 */
function limpiarFormulario() {
    formularioProducto.reset();
    document.querySelectorAll('.error-message').forEach(el => {
        el.classList.remove('show');
        el.textContent = '';
    });
    document.getElementById('colorDisplay').value = '#1a1a1a';
    document.getElementById('activo').value = 'true';
}

/**
 * Cerrar modal
 */
function cerrarModal() {
    modalProducto.classList.add('hidden');
    limpiarFormulario();
}

/**
 * Guardar producto
 */
async function guardarProducto(e) {
    e.preventDefault();

    const id = productId.value;
    const producto = {
        codigo: document.getElementById('codigo').value.trim(),
        nombre: document.getElementById('nombre').value.trim(),
        abreviacion: document.getElementById('abreviacion').value.trim(),
        piezasPorCaja: parseInt(document.getElementById('piezasPorCaja').value),
        colorDisplay: document.getElementById('colorDisplay').value,
        activo: document.getElementById('activo').value === 'true'
    };

    // Validar datos
    if (!validarFormulario(producto)) {
        return;
    }

    try {
        let response;
        const options = {
            headers: { 'Content-Type': 'application/json' }
        };

        if (id) {
            // Actualizar
            response = await fetch(`${API_URL}/${id}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(producto)
            });
        } else {
            // Crear
            response = await fetch(API_URL, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(producto)
            });
        }

        const data = await response.json();

        if (data.success) {
            mostrarNotificacion(data.message, 'success');
            cerrarModal();
            await cargarProductos();
        } else {
            mostrarNotificacion(data.message || 'Error al guardar', 'error');
        }
    } catch (error) {
        mostrarNotificacion('Error al guardar el producto', 'error');
    }
}

/**
 * Validar formulario
 */
function validarFormulario(producto) {
    let esValido = true;

    // Limpiar errores previos
    document.querySelectorAll('.error-message').forEach(el => {
        el.classList.remove('show');
        el.textContent = '';
    });

    if (!producto.codigo) {
        mostrarError('errorCodigo', 'El código es requerido');
        esValido = false;
    }

    if (!producto.nombre) {
        mostrarError('errorNombre', 'El nombre es requerido');
        esValido = false;
    }

    if (producto.piezasPorCaja === null || producto.piezasPorCaja < 0) {
        mostrarError('errorPiezas', 'Piezas por caja debe ser un número válido');
        esValido = false;
    }

    return esValido;
}

/**
 * Mostrar error en campo
 */
function mostrarError(elementoId, mensaje) {
    const elemento = document.getElementById(elementoId);
    if (elemento) {
        elemento.textContent = mensaje;
        elemento.classList.add('show');
    }
}

/**
 * Abrir modal de confirmación para eliminar
 */
function abrirModalEliminar(id) {
    productoParaEliminar = id;
    modalConfirmar.classList.remove('hidden');
}

/**
 * Cerrar modal de confirmación
 */
function cerrarModalConfirm() {
    modalConfirmar.classList.add('hidden');
    productoParaEliminar = null;
}

/**
 * Confirmar eliminación
 */
async function confirmarEliminar() {
    if (!productoParaEliminar) return;

    try {
        const response = await fetch(`${API_URL}/${productoParaEliminar}`, {
            method: 'DELETE',
            headers: { 'Content-Type': 'application/json' }
        });

        const data = await response.json();

        if (data.success) {
            mostrarNotificacion(data.message, 'success');
            cerrarModalConfirm();
            await cargarProductos();
        } else {
            mostrarNotificacion(data.message || 'Error al eliminar', 'error');
        }
    } catch (error) {
        mostrarNotificacion('Error al eliminar el producto', 'error');
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
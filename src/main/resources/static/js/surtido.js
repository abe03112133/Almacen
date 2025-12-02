const API_BASE = '/api/ordenes-surtido';

let estadoActual = {
    surtidoId: null
};

// ===================================================================
// INIT
// ===================================================================

document.addEventListener('DOMContentLoaded', () => {
    console.log('✓ Módulo Surtido cargado');
    cargarSurtidos();
});

// ===================================================================
// CARGAR SURTIDOS
// ===================================================================

function cargarSurtidos() {
    fetch(API_BASE, {
        credentials: 'include'
    })

        .then(res => res.json())
        .then(data => {
            renderizarSurtidos(data.surtidosActivos || [], 'gridActivos', 'emptyActivos');
            /////////renderizarSurtidos(data.surtidosTerminados || [], 'gridTerminados', 'emptyTerminados');
        })
        .catch(error => {
            console.error('Error cargando surtidos:', error);
            mostrarError('Error al cargar surtidos');
        });
}

function renderizarSurtidos(surtidos, gridId, emptyId) {
    const grid = document.getElementById(gridId);
    const empty = document.getElementById(emptyId);

    grid.innerHTML = '';

    if (surtidos.length === 0) {
        empty.classList.remove('hidden');
        return;
    }

    empty.classList.add('hidden');

    surtidos.forEach(surtido => {
        const card = document.createElement('div');
        card.className = `surtido-card ${surtido.estado === 'ACTIVO' ? 'active' : 'completed'}`;
        card.innerHTML = `
            <div class="card-header">
                <h3>Surtido #${surtido.id}</h3>
                <span class="badge badge-${surtido.estado.toLowerCase()}">${surtido.estado}</span>
            </div>
            <div class="card-body">
                <div class="info-row">
                    <span class="label">Fecha:</span>
                    <span class="value">${formatDate(surtido.fecha)}</span>
                </div>
                <!-- Supervisor ID disponible pero NO visible -->
                        <input type="hidden" class="supervisor-id" value="${surtido.supervisorId}">
                <div class="info-row">
                    <span class="label">Pedidos:</span>
                    <span class="value">${surtido.totalProductos || 0}</span>
                </div>
            </div>
            <div class="card-actions">
                <button class="btn-primary btn-sm" onclick="abrirDetalleSurtido(${surtido.id})">
                    👁️ Ver Detalle
                </button>
                <button class="btn-secondary btn-sm" onclick="descargarResumen(${surtido.id})">
                    📄 Resumen
                </button>
                ${surtido.estado === 'ACTIVO' ? `
                    <button class="btn-success btn-sm" onclick="cerrarSurtidoConfirm(${surtido.id})">
                        ✓ Cerrar
                    </button>
                ` : `
                `}
            </div>
        `;
        grid.appendChild(card);
    });
}

// ===================================================================
// CREAR SURTIDO
// ===================================================================

function abrirModalCrearSurtido() {
    console.log('Abriendo modal crear surtido');
    limpiarMensajeModal('errorSurtidoMsg');

    const ahora = new Date();
    ahora.setMinutes(ahora.getMinutes() - ahora.getTimezoneOffset());
    document.getElementById('fecha').value = ahora.toISOString().slice(0, 16);
    document.getElementById('descripcion').value = '';

    abrirModal('modalSurtido');
}

function crearSurtido() {
    const fecha = document.getElementById('fecha').value;
    const descripcion = document.getElementById('descripcion').value;

    if (!fecha) {
        mostrarErrorModal('La fecha es requerida', 'errorSurtidoMsg');
        return;
    }

    const params = new URLSearchParams({
        fecha: fecha,
        descripcion: descripcion
        // Sin supervisorId
    });

    fetch(`${API_BASE}/crear?${params}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
    })
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                mostrarSuccess('Surtido creado exitosamente');
                cerrarModal('modalSurtido');
                setTimeout(() => cargarSurtidos(), 500);
            } else {
                mostrarErrorModal(data.error || 'Error al crear surtido', 'errorSurtidoMsg');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            mostrarErrorModal('Error al crear surtido', 'errorSurtidoMsg');
        });
}

// ===================================================================
// DETALLE SURTIDO
// ===================================================================

function abrirDetalleSurtido(surtidoId) {
    console.log('Abriendo detalle surtido:', surtidoId);
    estadoActual.surtidoId = surtidoId;
    limpiarMensajes();

    fetch(`${API_BASE}/${surtidoId}/resumen`)
        .then(res => res.json())
        .then(data => {
            if (!data.success) {
                mostrarError(data.error || 'Error al cargar detalle');
                return;
            }

            const resumen = data.data;

            // Totales
            document.getElementById('detalleId').textContent = surtidoId;
            document.getElementById('totalSolicitado').textContent = resumen.totalSolicitado ?? 0;
            document.getElementById('totalAsignado').textContent = resumen.totalAsignado ?? 0;
            document.getElementById('totalSurtido').textContent = resumen.totalSurtido ?? 0;

            // ✅ Render productos
            renderizarProductos(resumen.productos || []);

            abrirModal('modalDetalle');
        })
        .catch(error => {
            console.error('Error:', error);
            mostrarError('Error al cargar detalle');
        });
}

function renderizarProductos(productos) {
    const tbody = document.getElementById('bodyProductos');
    tbody.innerHTML = '';

    if (!productos.length) {
        tbody.innerHTML = `
            '<tr><td colspan="6" style="text-align: center; padding: 20px; color: #999;">No hay productos agregados</td></tr>';
        `;
        return;
    }

    productos.forEach(p => {
        const tr = document.createElement('tr');

        // resaltar complementarios
        if (p.esComplementario) {
            tr.style.backgroundColor = '#fff5e5';
        }

        tr.innerHTML = `
            <td>${p.productoNombre}</td>
            <td>${p.destinoNombre}</td>
            <td>${p.cantidadSolicitada}</td>
            <td>${p.cantidadAsignada ?? 0}</td>
            <td>${p.cantidadSurtida ?? 0}</td>
            <td>
                <span class="badge badge-${p.estado.toLowerCase()}">${p.estado}</span>
            </td>
            <td>
                <button class="btn-sm btn-info" onclick="generarPicklist(${p.id})" title="Generar Picklist">
                    📋 Generar
                </button>
                <button class="btn-sm btn-warning" onclick="editarSurtidoProducto(${p.id})" title="Editar">
                    ✏️ Editar
            </button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

// Generar picklist (PEPS)
async function generarPicklist(surtidoProductoId) {
    try {
        const res = await fetch(`${API_BASE}/producto/${surtidoProductoId}/generar`, {
            method: 'POST',
            credentials: 'include',
            headers: { 'Content-Type': 'application/json' }
        });

        const data = await res.json();

        if (data.success) {
            mostrarSuccess(`Picklist generado: ${data.detalles} tarimas`);
            setTimeout(() => abrirDetalleSurtido(estadoActual.surtidoId), 500);
        } else {
            mostrarError(data.error || 'Error generando picklist');
        }
    } catch (error) {
        console.error('Error:', error);
        mostrarError('Error generando picklist');
    }
}

// Editar SurtidoProducto
async function editarSurtidoProducto(surtidoProductoId) {
    const cantidad = prompt('Ingresa nueva cantidad solicitada:');
    if (!cantidad) return;

    try {
        const res = await fetch(`${API_BASE}/producto/${surtidoProductoId}`, {
            method: 'PUT',
            credentials: 'include',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                cantidad: parseInt(cantidad)
            })
        });

        const data = await res.json();

        if (data.success) {
            mostrarSuccess('Producto editado exitosamente');
            abrirDetalleSurtido(estadoActual.surtidoId);
        } else {
            mostrarError(data.error || 'Error editando producto');
        }
    } catch (error) {
        console.error('Error:', error);
        mostrarError('Error editando producto');
    }
}

// Imprimir todos los picklists generados
// Imprimir todos los picklists generados
async function imprimirPDF() {
    if (!estadoActual.surtidoId) {
        mostrarError('Surtido no seleccionado');
        return;
    }

    try {
        const tbody = document.getElementById('bodyProductos');
        const filas = tbody.querySelectorAll('tr');

        if (filas.length === 0) {
            mostrarError('No hay productos para imprimir');
            return;
        }

        let productosGenerados = 0;

        filas.forEach(fila => {
            const btnGenerar = fila.querySelector('button[onclick^="generarPicklist"]');

            // Extraer el ID del SurtidoProducto del onclick
            // onclick="generarPicklist(${p.id})" -> extrae p.id
            const onclickText = btnGenerar.getAttribute('onclick');
            const surtidoProductoId = onclickText.match(/\d+/)[0];

            setTimeout(() => {
                window.location.href = `${API_BASE}/producto/${surtidoProductoId}/picklist/pdf`;
            }, productosGenerados * 500);

            productosGenerados++;
        });

        mostrarSuccess(`Descargando ${productosGenerados} picklist(s)...`);
    } catch (error) {
        console.error('Error:', error);
        mostrarError('Error descargando picklists');
    }
}


// ===================================================================
// AGREGAR PRODUCTO
// ===================================================================

// Helpers: cargar productos y destinos (usado por abrirModalAgregarProducto)
async function cargarProductosYDestinos() {
    try {
        // Endpoints: ajusta si tus endpoints reales son distintos
        const productosRes = await fetch('/api/productos', { credentials: 'include' });
        if (!productosRes.ok)
            throw new Error('Error cargando productos: ' + productosRes.status);
        const jsonProd = await productosRes.json();
        const productos = jsonProd.data || [];

        const destinosRes = await fetch('/api/destino/activos', { credentials: 'include' });
        if (!destinosRes.ok)
                    throw new Error('Error cargando productos: ' + destinosRes.status);
                const jsonDest = await destinosRes.json();
                const destinos = jsonDest.data || [];

        // Poblar selectProducto
        const selectProd = document.getElementById('selectProducto');
        selectProd.innerHTML = '<option value="">-- Seleccionar --</option>';
        (productos || []).forEach(p => {
            const opt = document.createElement('option');
            opt.value = p.id;
            // Muestra código + nombre para identificar bien
            opt.textContent = (p.codigo ? `${p.codigo} - ` : '') + (p.nombre || 'Sin nombre');
            selectProd.appendChild(opt);
        });

        // Poblar selectDestino
        const selectDest = document.getElementById('selectDestino');
        selectDest.innerHTML = '<option value="">-- Seleccionar --</option>';
        (destinos || []).forEach(d => {
            const opt = document.createElement('option');
            opt.value = d.id;
            opt.textContent = d.nombre || d.codigo || `Destino ${d.id}`;
            selectDest.appendChild(opt);
        });

        return { productos, destinos };
    } catch (err) {
        console.error(err);
        throw err;
    }
}

// Abrir modal agregar producto (ahora carga productos y destinos)
async function abrirModalAgregarProducto() {
    console.log('Abriendo modal agregar producto');
    if (!estadoActual.surtidoId) {
        mostrarError('Surtido no seleccionado');
        return;
    }

    limpiarMensajeModal('errorProductoMsg');

    // reset campos
    document.getElementById('selectProducto').value = '';
    document.getElementById('selectDestino').value = '';
    document.getElementById('cantidadProducto').value = '';
    document.getElementById('observacionesProducto').value = '';

    try {
        // carga listas y las pone en los selects
        await cargarProductosYDestinos();

        // Abre modal: mejor abrir después de poblar para que no se vea vacío
        cerrarModal('modalDetalle');
        abrirModal('modalProducto');
    } catch (err) {
        mostrarError('No se pudieron cargar productos o destinos. Reintenta.');
    }
}

// Agregar producto al surtido (POST)
async function agregarProducto() {
    const productoId = document.getElementById('selectProducto').value;
    const destinoId = document.getElementById('selectDestino').value;
    const cantidad = document.getElementById('cantidadProducto').value;
    const observaciones = document.getElementById('observacionesProducto').value;

    if (!productoId || !destinoId || !cantidad) {
        mostrarErrorModal('Completa los campos requeridos', 'errorProductoMsg');
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/${estadoActual.surtidoId}/producto`, {
            method: 'POST',
            credentials: 'include',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                productoId: parseInt(productoId),
                destinoId: parseInt(destinoId),
                cantidad: parseInt(cantidad),
                observaciones: observaciones || null
            })
        });

        // Manejo de errores HTTP
        if (!res.ok) {
            // intentar leer cuerpo JSON con error
            let errText = `${res.status} ${res.statusText}`;
            try {
                const errBody = await res.json();
                errText = errBody.error || errBody.message || JSON.stringify(errBody);
            } catch (e) {
                // no JSON, tal vez HTML -> fallback
                const txt = await res.text();
                errText = txt.substring(0, 300);
            }
            throw new Error(errText);
        }

        const data = await res.json();

        if (data.success) {
            mostrarSuccess('Producto agregado exitosamente');
            cerrarModal('modalProducto');
            // refrescar detalle del surtido para ver el nuevo producto
            abrirDetalleSurtido(estadoActual.surtidoId);
        } else {
            mostrarErrorModal(data.error || 'Error al agregar producto', 'errorProductoMsg');
        }
    } catch (error) {
        console.error('Error agregando producto:', error);
        mostrarErrorModal(typeof error === 'string' ? error : (error.message || 'Error al agregar producto'), 'errorProductoMsg');
    }
}


// ===================================================================
// CERRAR SURTIDO
// ===================================================================

function cerrarSurtidoConfirm(surtidoId) {
    if (confirm('¿Cerrar este surtido? Esta acción no se puede deshacer.')) {
        cerrarSurtido(surtidoId);
    }
}

function cerrarSurtido(surtidoId) {
    fetch(`${API_BASE}/${surtidoId}/cerrar`, {
        method: 'POST'
    })
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                mostrarSuccess('Surtido cerrado exitosamente');
                cerrarModal('modalDetalle');
                setTimeout(() => cargarSurtidos(), 500);
            } else {
                mostrarError(data.error || 'Error cerrando surtido');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            mostrarError('Error cerrando surtido');
        });
}

// ===================================================================
// DESCARGAR RESUMEN
// ===================================================================

function descargarResumen(surtidoId) {
    window.location.href = `${API_BASE}/diario/${surtidoId}/picklist/pdf`;
}

// ===================================================================
// UTILIDADES
// ===================================================================

function abrirModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.add('show');
    }
}

function cerrarModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.classList.remove('show');
    }
}

function mostrarError(mensaje) {
    const elem = document.getElementById('errorMsg');
    if (elem) {
        elem.textContent = '❌ ' + mensaje;
        elem.classList.add('show');
    }
}

function mostrarErrorModal(mensaje, elementId) {
    const elem = document.getElementById(elementId);
    if (elem) {
        elem.textContent = '❌ ' + mensaje;
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

function mostrarWarning(mensaje) {
    const elem = document.getElementById('warningMsg');
    if (elem) {
        elem.textContent = '⚠️ ' + mensaje;
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

function limpiarMensajeModal(elementId) {
    const elem = document.getElementById(elementId);
    if (elem) elem.classList.remove('show');
}

function formatDate(date) {
    if (!date) return 'N/A';
    const d = new Date(date);
    return d.toLocaleString('es-MX', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    });
}

// Click fuera del modal para cerrar
window.addEventListener('click', function(event) {
    if (event.target.classList.contains('modal-overlay') && event.target.classList.contains('show')) {
        event.target.classList.remove('show');
    }
});
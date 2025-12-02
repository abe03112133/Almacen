const API = '/api/insumos';

let insumoEnEdicion = null;
let maquinaEnEdicion = null;

// ====== INSUMOS ======

/**
 * Abre formulario para crear/editar insumo
 */
function abrirFormularioInsumo(id = null) {
    const modal = document.getElementById('modalInsumo');
    const form = document.getElementById('formInsumo');
    const title = document.getElementById('modalInsumoTitle');

    form.reset();
    insumoEnEdicion = null;

    if (id) {
        // Editar
        title.textContent = 'Editar Insumo';
        form.action = `/insumos/${id}/editar`;
        cargarInsumo(id);
    } else {
        // Crear
        title.textContent = 'Crear Insumo';
        form.action = '/insumos/crear';
    }

    modal.classList.add('show');
}

/**
 * Carga datos del insumo en el formulario
 */
function cargarInsumo(id) {
    fetch(`${API}/${id}`)
        .then(response => response.json())
        .then(data => {
            if (data.exitoso) {
                const insumo = data.insumo;
                document.getElementById('insumoCodigo').value = insumo.codigo;
                document.getElementById('insumoDescripcion').value = insumo.descripcion;
                insumoEnEdicion = id;
            }
        })
        .catch(error => console.error('Error cargando insumo:', error));
}

/**
 * Cierra modal de insumo
 */
function cerrarModalInsumo() {
    document.getElementById('modalInsumo').classList.remove('show');
}

/**
 * Elimina un insumo
 */
function eliminarInsumo(id) {
    if (confirm('¿Estás seguro de que deseas eliminar este insumo?')) {
        fetch(`${API}/${id}/eliminar`, {
            method: 'POST'
        })
        .then(response => response.json())
        .then(data => {
            if (data.exitoso) {
                location.reload();
            } else {
                alert('Error: ' + data.error);
            }
        })
        .catch(error => console.error('Error eliminando insumo:', error));
    }
}

// ====== MÁQUINAS ======

/**
 * Abre formulario para crear/editar máquina
 */
function abrirFormularioMaquina(id = null) {
    const modal = document.getElementById('modalMaquina');
    const form = document.getElementById('formMaquina');
    const title = document.getElementById('modalMaquinaTitle');

    form.reset();
    maquinaEnEdicion = null;

    if (id) {
        // Editar
        title.textContent = 'Editar Máquina';
        form.action = `/insumos/maquinas/${id}/editar`;
        cargarMaquina(id);
    } else {
        // Crear
        title.textContent = 'Crear Máquina';
        form.action = '/insumos/maquinas/crear';
        document.getElementById('maquinaActivo').checked = true;
    }

    modal.classList.add('show');
}

/**
 * Carga datos de la máquina en el formulario
 */
function cargarMaquina(id) {
    fetch(`${API}/maquinas/${id}`)
        .then(response => response.json())
        .then(data => {
            if (data.exitoso) {
                const maquina = data.maquina;
                // Extraer código sin prefijo M-
                const codigoSinPrefijo = maquina.codigo.replace('M-', '');
                document.getElementById('maquinaCodigo').value = codigoSinPrefijo;
                document.getElementById('maquinaNombre').value = maquina.nombre;
                document.getElementById('maquinaInsumo').value = maquina.insumoId || '';
                document.getElementById('maquinaActivo').checked = maquina.activo;
                maquinaEnEdicion = id;
            }
        })
        .catch(error => console.error('Error cargando máquina:', error));
}

/**
 * Cierra modal de máquina
 */
function cerrarModalMaquina() {
    document.getElementById('modalMaquina').classList.remove('show');
}

/**
 * Elimina una máquina
 */
function eliminarMaquina(id) {
    if (confirm('¿Estás seguro de que deseas eliminar esta máquina?')) {
        fetch(`${API}/maquinas/${id}/eliminar`, {
            method: 'POST'
        })
        .then(response => response.json())
        .then(data => {
            if (data.exitoso) {
                location.reload();
            } else {
                alert('Error: ' + data.error);
            }
        })
        .catch(error => console.error('Error eliminando máquina:', error));
    }
}

// ====== EVENT LISTENERS ======

document.addEventListener('DOMContentLoaded', () => {
    // Formulario insumo
    const formInsumo = document.getElementById('formInsumo');
    if (formInsumo) {
        formInsumo.addEventListener('submit', (e) => {
            e.preventDefault();
            guardarInsumo();
        });
    }

    // Formulario máquina
    const formMaquina = document.getElementById('formMaquina');
    if (formMaquina) {
        formMaquina.addEventListener('submit', (e) => {
            e.preventDefault();
            guardarMaquina();
        });
    }

    // Cerrar modales al hacer clic fuera
    window.addEventListener('click', (e) => {
        const modalInsumo = document.getElementById('modalInsumo');
        const modalMaquina = document.getElementById('modalMaquina');

        if (e.target === modalInsumo) {
            cerrarModalInsumo();
        }
        if (e.target === modalMaquina) {
            cerrarModalMaquina();
        }
    });
});

/**
 * Guarda un insumo (crear o editar)
 */
function guardarInsumo() {
    const codigo = document.getElementById('insumoCodigo').value.trim();
    const descripcion = document.getElementById('insumoDescripcion').value.trim();

    if (!codigo || !descripcion) {
        alert('Por favor completa todos los campos');
        return;
    }

    const url = insumoEnEdicion
        ? `${API}/${insumoEnEdicion}/actualizar`
        : `${API}/crear`;

    const data = {
        codigo: codigo,
        descripcion: descripcion
    };

    fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
    .then(response => response.json())
    .then(result => {
        if (result.exitoso) {
            location.reload();
        } else {
            alert('Error: ' + (result.error || 'Error desconocido'));
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Error al guardar insumo');
    });
}

/**
 * Guarda una máquina (crear o editar)
 */
function guardarMaquina() {
    const codigo = document.getElementById('maquinaCodigo').value.trim();
    const nombre = document.getElementById('maquinaNombre').value.trim();
    const insumoId = document.getElementById('maquinaInsumo').value;
    const activo = document.getElementById('maquinaActivo').checked;

    if (!codigo || !nombre || !insumoId) {
        alert('Por favor completa todos los campos');
        return;
    }

    const url = maquinaEnEdicion
        ? `${API}/maquinas/${maquinaEnEdicion}/actualizar`
        : `${API}/maquinas/crear`;

    const data = {
        codigo: 'M-' + codigo,  // Agregar prefijo
        nombre: nombre,
        insumoId: parseInt(insumoId),
        activo: activo
    };

    fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
    .then(response => response.json())
    .then(result => {
        if (result.exitoso) {
            location.reload();
        } else {
            alert('Error: ' + (result.error || 'Error desconocido'));
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Error al guardar máquina');
    });
}
document.addEventListener('DOMContentLoaded', function() {
    // Con sesiones tradicionales, no necesitas validar token
    // Spring Security lo hace automáticamente

    // Configurar botones de tarjetas
    const cardButtons = document.querySelectorAll('.card-button');

    const routes = {
        'almacen': '/almacen',
        'surtido': '/surtidos',
        'reportes': '/reportes',
        'usuarios': '/usuarios',
        'productos': '/productos',
        'producciones': '/producciones',
        'insumos': '/insumos',
        'racks': '/racks'
    };

    cardButtons.forEach((button) => {
        button.addEventListener('click', function(e) {
            e.preventDefault();

            const action = this.getAttribute('data-action');
            const url = routes[action];

            if (url) {
                window.location.href = url;
            } else {
                console.error('Acción desconocida:', action);
            }
        });
    });

    console.log('Dashboard cargado correctamente');
});
// pwa-register.js - Registra el Service Worker para PWA

(function() {
    'use strict';

    // Verificar si el navegador soporta Service Workers
    if (!navigator.serviceWorker) {
        console.warn('⚠️ Service Workers no soportados en este navegador');
        return;
    }

    // Registrar el Service Worker
    window.addEventListener('load', () => {
        navigator.serviceWorker.register('/service-worker.js', {
            scope: '/'
        })
        .then(registration => {
            console.log('✅ Service Worker registrado:', registration);

            // Escuchar actualizaciones del Service Worker
            registration.addEventListener('updatefound', () => {
                const newWorker = registration.installing;
                console.log('🔄 Nueva versión de Service Worker disponible');

                newWorker.addEventListener('statechange', () => {
                    if (newWorker.state === 'installed' && navigator.serviceWorker.controller) {
                        // Hay una nueva versión disponible
                        console.log('📢 Nueva versión disponible');
                        mostrarNotificacionActualizacion();
                    }
                });
            });

            // Verificar actualizaciones periódicamente
            setInterval(() => {
                registration.update();
            }, 60000); // Cada minuto

        })
        .catch(error => {
            console.error('❌ Error al registrar Service Worker:', error);
        });

        // Escuchar cambios de controlador (Service Worker activado)
        let refreshing = false;
        navigator.serviceWorker.addEventListener('controllerchange', () => {
            if (!refreshing) {
                refreshing = true;
                window.location.reload();
            }
        });
    });

    // Función para mostrar notificación de actualización
    function mostrarNotificacionActualizacion() {
        const notification = document.createElement('div');
        notification.className = 'pwa-update-notification';
        notification.innerHTML = `
            <div style="padding: 16px; background: #1a1a1a; color: white; border-radius: 4px; margin: 10px; display: flex; justify-content: space-between; align-items: center; box-shadow: 0 4px 12px rgba(0,0,0,0.15);">
                <span>📦 Nueva versión disponible</span>
                <div>
                    <button onclick="location.reload()" style="padding: 8px 16px; background: #e8dcc8; color: #1a1a1a; border: none; border-radius: 4px; cursor: pointer; font-weight: 600; margin-right: 8px;">Actualizar</button>
                    <button onclick="this.parentElement.parentElement.parentElement.remove()" style="padding: 8px 16px; background: transparent; color: white; border: 1px solid white; border-radius: 4px; cursor: pointer;">Luego</button>
                </div>
            </div>
        `;
        document.body.insertBefore(notification, document.body.firstChild);
    }

    // Solicitar permiso para notificaciones push
    function solicitarPermisoPush() {
        if (!('Notification' in window)) {
            console.warn('⚠️ Notificaciones no soportadas');
            return;
        }

        if (Notification.permission === 'granted') {
            console.log('✅ Permiso de notificaciones ya otorgado');
            return;
        }

        if (Notification.permission !== 'denied') {
            Notification.requestPermission().then(permission => {
                if (permission === 'granted') {
                    console.log('✅ Permiso de notificaciones otorgado');
                    // Aquí puedes suscribirse a notificaciones push
                }
            });
        }
    }

    // Solicitar permiso cuando el usuario esté en dashboard
    if (window.location.pathname === '/dashboard') {
        setTimeout(solicitarPermisoPush, 2000);
    }

})();
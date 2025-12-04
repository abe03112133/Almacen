// Service Worker para PWA - Almacenamiento en caché y funcionalidad offline

const CACHE_NAME = 'ppa-procesadora-v1';
const STATIC_ASSETS = [
    '/',
    '/login',
    '/dashboard',
    '/static/css/global.css',
    '/static/css/error.css',
    '/static/js/util.js',
    '/offline.html'
];

// Instalación del Service Worker
self.addEventListener('install', (event) => {
    console.log('🔧 Service Worker instalándose...');
    event.waitUntil(
        caches.open(CACHE_NAME).then((cache) => {
            console.log('✅ Cache creado:', CACHE_NAME);
            return cache.addAll(STATIC_ASSETS);
        }).catch(err => {
            console.error('❌ Error al cachear assets:', err);
        })
    );
    self.skipWaiting();
});

// Activación del Service Worker
self.addEventListener('activate', (event) => {
    console.log('⚡ Service Worker activándose...');
    event.waitUntil(
        caches.keys().then((cacheNames) => {
            return Promise.all(
                cacheNames.map((cacheName) => {
                    if (cacheName !== CACHE_NAME) {
                        console.log('🗑️ Eliminando cache antiguo:', cacheName);
                        return caches.delete(cacheName);
                    }
                })
            );
        })
    );
    self.clients.claim();
});

// Estrategia de fetch: Network First, Fall back to Cache
self.addEventListener('fetch', (event) => {
    const { request } = event;
    const url = new URL(request.url);

    // NO cachear requests POST, DELETE, etc.
    if (request.method !== 'GET') {
        event.respondWith(
            fetch(request).catch(() => {
                return new Response(
                    JSON.stringify({ error: 'No hay conexión' }),
                    { status: 503, headers: { 'Content-Type': 'application/json' } }
                );
            })
        );
        return;
    }

    // NO cachear APIs (excepto GET)
    if (url.pathname.startsWith('/api/')) {
        event.respondWith(
            fetch(request)
                .then(response => {
                    // Cachear respuestas exitosas de API
                    if (response.ok) {
                        const clonedResponse = response.clone();
                        caches.open(CACHE_NAME).then(cache => {
                            cache.put(request, clonedResponse);
                        });
                    }
                    return response;
                })
                .catch(() => {
                    // Si no hay conexión, intentar desde cache
                    return caches.match(request)
                        .then(cachedResponse => {
                            return cachedResponse || new Response(
                                JSON.stringify({ error: 'Sin conexión' }),
                                { status: 503, headers: { 'Content-Type': 'application/json' } }
                            );
                        });
                })
        );
        return;
    }

    // Para todo lo demás, usar estrategia: Network First, Fall back to Cache
    event.respondWith(
        fetch(request)
            .then(response => {
                // Actualizar cache si la respuesta es exitosa
                if (response.ok) {
                    const clonedResponse = response.clone();
                    caches.open(CACHE_NAME).then(cache => {
                        cache.put(request, clonedResponse);
                    });
                }
                return response;
            })
            .catch(() => {
                // Si falla la red, intentar desde cache
                return caches.match(request)
                    .then(cachedResponse => {
                        if (cachedResponse) {
                            return cachedResponse;
                        }
                        // Si no está en cache y es una navegación, mostrar página offline
                        if (request.mode === 'navigate') {
                            return caches.match('/offline.html')
                                .then(response => response || new Response('Offline', { status: 503 }));
                        }
                        return new Response('Recurso no disponible', { status: 404 });
                    });
            })
    );
});

// Recibir mensajes desde el cliente
self.addEventListener('message', (event) => {
    if (event.data && event.data.type === 'SKIP_WAITING') {
        self.skipWaiting();
    }
});

console.log('✅ Service Worker cargado y listo');
/**
 * Utilidades globales para toda la aplicación
 */

const PPA = {
    /**
     * Configuración global
     */
    config: {
        apiUrl: '/ppa/api',
        timeout: 5000,
        tokenKey: 'ppa_token'
    },

    /**
     * Obtiene el token del almacenamiento (localStorage o sessionStorage)
     */
    getToken: function() {
        return sessionStorage.getItem(this.config.tokenKey) ||
               localStorage.getItem(this.config.tokenKey);
    },

    /**
     * Guarda el token
     */
    setToken: function(token, persistent = false) {
        if (persistent) {
            localStorage.setItem(this.config.tokenKey, token);
        } else {
            sessionStorage.setItem(this.config.tokenKey, token);
        }
    },

    /**
     * Elimina el token
     */
    removeToken: function() {
        sessionStorage.removeItem(this.config.tokenKey);
        localStorage.removeItem(this.config.tokenKey);
    },

    /**
     * Realiza una petición HTTP
     */
    request: function(method, endpoint, data = null, options = {}) {
        const config = {
            method: method,
            url: this.config.apiUrl + endpoint,
            timeout: options.timeout || this.config.timeout,
            headers: {
                'Content-Type': 'application/json'
            }
        };

        const token = this.getToken();
        if (token) {
            config.headers['Authorization'] = 'Bearer ' + token;
        }

        if (data) {
            config.data = data;
        }

        return axios(config)
            .catch(error => {
                console.error('Error en petición:', error);
                throw error;
            });
    },

    /**
     * Realiza GET
     */
    get: function(endpoint) {
        return this.request('GET', endpoint);
    },

    /**
     * Realiza POST
     */
    post: function(endpoint, data) {
        return this.request('POST', endpoint, data);
    },

    /**
     * Realiza PUT
     */
    put: function(endpoint, data) {
        return this.request('PUT', endpoint, data);
    },

    /**
     * Realiza DELETE
     */
    delete: function(endpoint) {
        return this.request('DELETE', endpoint);
    },

    /**
     * Muestra un mensaje de error
     */
    showError: function(message, element = null) {
        console.error(message);

        if (element) {
            const errorDiv = document.createElement('div');
            errorDiv.className = 'error-message show';
            errorDiv.textContent = message;

            if (element.parentElement) {
                element.parentElement.insertBefore(errorDiv, element);
            }

            setTimeout(() => {
                errorDiv.remove();
            }, 5000);
        }
    },

    /**
     * Muestra un mensaje de éxito
     */
    showSuccess: function(message, element = null) {
        console.log(message);

        if (element) {
            const successDiv = document.createElement('div');
            successDiv.className = 'success-message show';
            successDiv.textContent = message;

            if (element.parentElement) {
                element.parentElement.insertBefore(successDiv, element);
            }

            setTimeout(() => {
                successDiv.remove();
            }, 3000);
        }
    },

    /**
     * Valida si el email es válido
     */
    isValidEmail: function(email) {
        const regex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return regex.test(email);
    },

    /**
     * Formatea una fecha
     */
    formatDate: function(date, format = 'dd/MM/yyyy') {
        const d = new Date(date);
        const day = String(d.getDate()).padStart(2, '0');
        const month = String(d.getMonth() + 1).padStart(2, '0');
        const year = d.getFullYear();
        const hours = String(d.getHours()).padStart(2, '0');
        const minutes = String(d.getMinutes()).padStart(2, '0');

        return format
            .replace('dd', day)
            .replace('MM', month)
            .replace('yyyy', year)
            .replace('HH', hours)
            .replace('mm', minutes);
    },

    /**
     * Debounce para funciones
     */
    debounce: function(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    },

    /**
     * Copia texto al portapapeles
     */
    copyToClipboard: function(text) {
        if (navigator.clipboard) {
            navigator.clipboard.writeText(text);
        } else {
            const textarea = document.createElement('textarea');
            textarea.value = text;
            document.body.appendChild(textarea);
            textarea.select();
            document.execCommand('copy');
            document.body.removeChild(textarea);
        }
    }
};

// Log inicial
console.log('PPA Utils cargado correctamente');
document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('loginForm');
    const usernameInput = document.getElementById('username');
    const passwordInput = document.getElementById('password');
    const errorMessage = document.getElementById('errorMessage');
    const submitButton = document.getElementById('submitButton');
    const loadingSpinner = document.getElementById('loadingSpinner');

    // Limpiar mensaje de error al escribir
    usernameInput.addEventListener('input', function() {
        if (errorMessage && errorMessage.classList.contains('show')) {
            errorMessage.classList.remove('show');
        }
    });

    passwordInput.addEventListener('input', function() {
        if (errorMessage && errorMessage.classList.contains('show')) {
            errorMessage.classList.remove('show');
        }
    });

    // Permitir envío con Enter
    passwordInput.addEventListener('keypress', function(event) {
        if (event.key === 'Enter') {
            form.submit();
        }
    });

    // Mostrar spinner cuando se envía el formulario
    form.addEventListener('submit', function(event) {
        // Validación básica del lado del cliente
        if (!usernameInput.value.trim()) {
            event.preventDefault();
            showError('Por favor ingresa tu usuario');
            usernameInput.focus();
            return false;
        }

        if (!passwordInput.value) {
            event.preventDefault();
            showError('Por favor ingresa tu contraseña');
            passwordInput.focus();
            return false;
        }

        // Si pasa validación, mostrar spinner
        loadingSpinner.style.display = 'flex';
        submitButton.disabled = true;
    });

    function showError(message) {
        if (errorMessage) {
            errorMessage.textContent = message;
            errorMessage.classList.add('show');
        }
    }

    console.log('Login inicializado correctamente');
});
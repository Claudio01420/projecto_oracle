// Helper de sesión para usar en todas las páginas que consumen el API
// Ruta: backend/src/main/frontend/public/Js/session.js
(function () {
  function getUserEmail() {
    let email = localStorage.getItem('userEmail');
    if (email && email.trim()) return email.trim();
    const m = document.cookie.match(/(?:^|;\s*)userEmail=([^;]+)/);
    if (m) return decodeURIComponent(m[1]).trim();
    return "";
  }

  function ensureEmailOrRedirect() {
    const email = getUserEmail();
    if (!email) {
      alert('Tu sesión expiró o no tiene correo. Inicia sesión nuevamente.');
      window.location.href = '/login.html';
      throw new Error('Sin sesión');
    }
    return email;
  }

  function commonHeaders() {
    const email = ensureEmailOrRedirect();
    return {
      'Content-Type': 'application/json',
      'X-User-Email': email    // <-- el backend lo usa para forzar owner y filtrar
    };
  }

  // Helper para llamadas al API que añade el header X-User-Email automáticamente.
  // Si se envía FormData, no establece Content-Type para que el navegador lo gestione.
  async function apiFetch(url, init) {
    init = init || {};
    const initCopy = Object.assign({}, init);
    // merge headers (init.headers may exist)
    const baseHeaders = commonHeaders();
    const headers = Object.assign({}, initCopy.headers || {}, baseHeaders);

    // Si body es FormData, quitar Content-Type para que fetch lo gestione
    const body = initCopy.body;
    if (body instanceof FormData) {
      delete headers['Content-Type'];
    }

    initCopy.headers = headers;
    return fetch(url, initCopy);
  }

  function logout() {
    localStorage.removeItem('userEmail');
    document.cookie = 'userEmail=; Max-Age=0; path=/';
    window.location.href = '/login.html';
  }

  window.Session = { getUserEmail, ensureEmailOrRedirect, commonHeaders, logout, apiFetch };
})();

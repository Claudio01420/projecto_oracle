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

  function logout() {
    localStorage.removeItem('userEmail');
    document.cookie = 'userEmail=; Max-Age=0; path=/';
    window.location.href = '/login.html';
  }

  window.Session = { getUserEmail, ensureEmailOrRedirect, commonHeaders, logout };
})();

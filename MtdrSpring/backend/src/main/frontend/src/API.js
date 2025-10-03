// API.js
// Configuración de la URL base para consumir el backend.
// Si existe REACT_APP_API_URL la usa, de lo contrario usa ruta relativa.
const BASE = process.env.REACT_APP_API_URL || '';
const API_LIST = `${BASE}/todolist`;
export default API_LIST;
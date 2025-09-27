# Configuración de Ambientes

Este directorio contiene los archivos de **configuración** para la aplicación web **TMDV**, separados por ambiente:  
- `development.json`  
- `staging.json`  
- `production.json`  

---

## 📂 Formato y Sintaxis
- Formato elegido: **JSON** (JavaScript Object Notation).  
- Sintaxis estricta:  
  - Todas las claves y valores de texto deben ir entre comillas dobles `"`.  
  - No se permiten **comas finales**.  
  - Archivos deben estar codificados en **UTF-8** y terminar con un salto de línea.  

---

## 📌 Parámetros Definidos

### `app`
- **name**: Nombre de la aplicación.  
- **environment**: Ambiente actual (`development`, `staging`, `production`).  

### `server`
- **port**: Puerto en el que corre el backend.  
- **baseUrl**: URL base del servidor.  
- **corsAllowedOrigins**: Lista de orígenes permitidos para CORS.  

### `frontend`
- **publicUrl**: URL pública de la interfaz web.  

### `database`
- **vendor**: Motor de base de datos (ej. `oracle`).  
- **host**: Host de la base de datos.  
- **port**: Puerto de conexión.  
- **serviceName**: Servicio de la BD.  
- **userEnv** / **passEnv**: Variables de entorno para credenciales.  
- **sslMode**: Requiere SSL en ambientes no locales.  
- **pool**: Configuración de pool de conexiones.  

### `auth`
- **issuer**: Emisor del token de autenticación.  
- **audience**: Audiencia esperada.  
- **clientIdEnv** / **clientSecretEnv**: Variables de entorno para OAuth/OIDC.  

### `api`
- **gatewayUrl**: Endpoint principal de la API.  
- **timeoutMs**: Tiempo máximo de espera.  
- **retries**: Número de reintentos.  

### `features`
- **chatbot**, **dashboards**, **notifications**: Funcionalidades activas.  
- **experimentalWearables**: Features experimentales (solo dev).  

### `logging`
- **level**: Nivel de logs (`debug`, `info`, `warn`).  
- **json**: Salida en formato JSON (true/false).  
- **trace**: Activar trazas detalladas.  

### `telemetry`
- **enabled**: Si se envían métricas a observabilidad.  
- **otlpEndpointEnv**: Endpoint OTLP como variable de entorno.  

### `rateLimit`
- **enabled**: Control de límite de peticiones.  
- **requestsPerMin**: Número máximo por minuto.  

### `cache`
- **enabled**: Uso de cache.  
- **ttlSeconds**: Tiempo de vida en segundos.  

### `thirdParty.telegram`
- **botTokenEnv**: Token del bot en variable de entorno.  
- **webhookUrl**: URL de callback de Telegram.  

### `oci`
- **region**: Región de OCI.  
- **bucketAssets**: Bucket asignado a los assets.  
- **compartmentOcidEnv**: OCID del compartimento en variable de entorno.  

---

## 📝 Convenciones
- **Variables sensibles** (usuarios, contraseñas, tokens) se referencian con `*_Env` y deben definirse en el entorno, **no** en los JSON.  
- **CamelCase** para las claves JSON.  
- **MAYÚSCULAS_SNAKE_CASE** para variables de entorno.  
- Un **salto de línea final** obligatorio en cada archivo (`\n`).  

---

## 🔍 Ejemplo de Uso
```bash
# Levantar backend en desarrollo con config JSON
export DB_USER=developer
export DB_PASSWORD=secret
npm run dev -- --config=config/development.json

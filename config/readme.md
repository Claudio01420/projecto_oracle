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

# Contribuciones a Configuración

Este documento describe el **flujo de trabajo** para proponer, revisar y aplicar cambios en los archivos de configuración (`config/`).

---

## 📌 Flujo de Contribución

1. **Crear una rama**
   - Para cualquier cambio en configuración, crea una rama con el prefijo `config/`.
   - Ejemplo:  
     ```bash
     git checkout -b config/update-feature-x
     ```

2. **Realizar cambios**
   - Edita el archivo correspondiente (`development.json`, `staging.json`, `production.json` o sus versiones `.yml`).
   - Asegúrate de actualizar también la documentación en `README.md` si agregas parámetros nuevos.

3. **Validación local**
   - Antes de subir cambios, valida la sintaxis con un linter:
     ```bash
     # Para JSON
     npx jsonlint -q config/production.json

     # Para YAML
     yamllint config/staging.yml
     ```

4. **Abrir un Pull Request (PR)**
   - Envía tus cambios a GitHub y abre un **Pull Request** hacia la rama principal (`main`).
   - En la descripción explica claramente:
     - Qué parámetros se modificaron o agregaron.
     - En qué ambiente (dev/stg/prod).
     - Justificación del cambio (ejemplo: activar feature flag, cambiar host de BD, etc.).

5. **Revisión por un compañero**
   - Al menos **un miembro distinto** del equipo debe revisar el PR.
   - El revisor debe:
     - Ejecutar el linter en su entorno.
     - Confirmar que la descripción del PR es clara y completa.
     - Verificar que el cambio no afecta negativamente otros ambientes.

6. **Aprobación y Merge**
   - Una vez aprobado, se realiza el **merge** a `main`.
   - El historial debe quedar limpio (usar `Squash and Merge` recomendado).

---

## ✅ Reglas Generales

- **No incluir secretos** en los archivos de configuración (contraseñas, API keys).  
- Usar **variables de entorno** (`*_Env`) en lugar de credenciales planas.  
- Mantener la consistencia en la convención de claves:  
  - **camelCase** en parámetros JSON/YAML.  
  - **MAYÚSCULAS_SNAKE_CASE** en nombres de variables de entorno.  
- Cada archivo debe terminar con **una nueva línea final** (`\n`).  
- Si agregas parámetros nuevos, documentarlos en `README.md`.  

---

## 📂 Archivos cubiertos
- `config/development.json` o `config/development.yml`  
- `config/staging.json` o `config/staging.yml`  
- `config/production.json` o `config/production.yml`  
- `config/README.md`  
- `config/CONTRIBUTING.md`  

---

## 🚀 Ejemplo de Pull Request

- **Rama creada:** `config/enable-telegram-stg`  
- **Cambio realizado:**  
  ```diff
  features:
    chatbot: true
    dashboards: true
    notifications: true
-   experimentalWearables: false
+   experimentalWearables: true

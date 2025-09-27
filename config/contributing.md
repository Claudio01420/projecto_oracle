
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

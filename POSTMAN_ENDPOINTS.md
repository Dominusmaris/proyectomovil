# 🚀 ENDPOINTS PARA POSTMAN - FINANZAS APP

## 📡 URLs BASE

### **Local (Desarrollo)**
```
http://localhost:8081
```

### **Producción (Render)**
```
https://backend-finanzas-xxxxx.onrender.com
```

---

## 🧪 ENDPOINTS DE PRUEBA

### 1. Verificar Backend Funcionando
```
GET {{base_url}}/api/pruebas/backend-funciona
```
**Respuesta esperada:**
```json
"✅ Backend funcionando correctamente"
```

### 2. Verificar Base de Datos
```
GET {{base_url}}/api/pruebas/base-datos-conectada
```
**Respuesta esperada:**
```json
{
  "baseDatosConectada": true,
  "version": "H2 Database",
  "servidor": "localhost:8081",
  "timestamp": 1734361234567
}
```

### 3. Prueba Echo (POST)
```
POST {{base_url}}/api/pruebas/enviar-datos
Content-Type: application/json

{
  "mensaje": "Hola desde Postman",
  "timestamp": 1734361234567,
  "usuario": "test"
}
```

### 4. Información del Sistema
```
GET {{base_url}}/api/pruebas/info-sistema
```

### 5. Listar Tablas de BD
```
GET {{base_url}}/api/pruebas/listar-tablas
```

---

## 🔐 ENDPOINTS DE AUTENTICACIÓN

### 1. Login
```
POST {{base_url}}/api/auth/login
Content-Type: application/json

{
  "email": "admin@finanzas.com",
  "password": "123456"
}
```

### 2. Registro
```
POST {{base_url}}/api/auth/registro
Content-Type: application/json

{
  "nombre": "Usuario Nuevo",
  "email": "nuevo@test.com",
  "password": "123456789"
}
```

### 3. Recuperar Contraseña
```
POST {{base_url}}/api/auth/recuperar-password
Content-Type: application/json

{
  "email": "admin@finanzas.com"
}
```

---

## 💰 ENDPOINTS DE TRANSACCIONES

### 1. Obtener Transacciones del Usuario
```
GET {{base_url}}/api/transacciones/usuario
Authorization: Bearer {{token}}
```

### 2. Crear Nueva Transacción
```
POST {{base_url}}/api/transacciones
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "tipo": "GASTO",
  "monto": 50000.0,
  "categoria": "🍽️ Alimentación",
  "descripcion": "Supermercado Jumbo",
  "fecha": 1734361234567,
  "latitud": -33.4489,
  "longitud": -70.6693,
  "ubicacionTexto": "Santiago Centro",
  "rutaFoto": "/storage/fotos/foto_123456.jpg"
}
```

### 3. Actualizar Transacción
```
PUT {{base_url}}/api/transacciones/1
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "tipo": "GASTO",
  "monto": 60000.0,
  "categoria": "🍽️ Alimentación",
  "descripcion": "Supermercado Jumbo - Actualizado"
}
```

### 4. Eliminar Transacción
```
DELETE {{base_url}}/api/transacciones/1
Authorization: Bearer {{token}}
```

---

## 👤 ENDPOINTS DE USUARIOS

### 1. Obtener Perfil
```
GET {{base_url}}/api/usuarios/perfil
Authorization: Bearer {{token}}
```

### 2. Actualizar Perfil
```
PUT {{base_url}}/api/usuarios/perfil
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "nombre": "Evan Mardones Actualizado",
  "monedaPreferida": "USD",
  "limiteMensualGastos": 500000.0
}
```

### 3. Listar Todos los Usuarios (Solo Admin)
```
GET {{base_url}}/api/usuarios/todos
Authorization: Bearer {{token}}
```

---

## 📊 ENDPOINTS DE REPORTES

### 1. Resumen Mensual
```
GET {{base_url}}/api/reportes/resumen-mensual
Authorization: Bearer {{token}}
```

### 2. Estadísticas Generales (Solo Admin)
```
GET {{base_url}}/api/reportes/estadisticas-generales
Authorization: Bearer {{token}}
```

---

## 🗂️ ENDPOINTS DE CATEGORÍAS

### 1. Obtener Categorías del Usuario
```
GET {{base_url}}/api/categorias/usuario
Authorization: Bearer {{token}}
```

### 2. Crear Nueva Categoría
```
POST {{base_url}}/api/categorias
Authorization: Bearer {{token}}
Content-Type: application/json

{
  "nombre": "🎮 Gaming",
  "tipo": "GASTO",
  "icono": "🎮"
}
```

---

## 🌐 API EXTERNA - TASAS DE CAMBIO

### 1. Obtener Tasas Actuales (USD Base)
```
GET https://api.exchangerate-api.com/v4/latest/USD
```

### 2. Obtener Tasas desde CLP
```
GET https://api.exchangerate-api.com/v4/latest/CLP
```

---

## 📋 COLECCIÓN POSTMAN

### Variables de Entorno Sugeridas:

**Desarrollo:**
```
base_url: http://localhost:8081
token: [Tu token JWT después del login]
```

**Producción:**
```
base_url: https://backend-finanzas-xxxxx.onrender.com
token: [Tu token JWT después del login]
```

---

## 🔧 EJEMPLOS DE USO

### 1. Flujo Completo de Autenticación:
1. `POST /api/auth/login` → Obtener token
2. Copiar el `token` de la respuesta
3. Usar en header `Authorization: Bearer {token}` para endpoints protegidos

### 2. Flujo Completo de Transacción:
1. Login para obtener token
2. `GET /api/transacciones/usuario` → Ver transacciones existentes
3. `POST /api/transacciones` → Crear nueva
4. `GET /api/reportes/resumen-mensual` → Ver resumen

### 3. Pruebas de Desarrollo:
1. `GET /api/pruebas/backend-funciona`
2. `GET /api/pruebas/base-datos-conectada`
3. `POST /api/pruebas/enviar-datos`

---

## ⚠️ NOTAS IMPORTANTES

- **Headers requeridos:** `Content-Type: application/json` para POST/PUT
- **Autenticación:** Endpoints protegidos requieren `Authorization: Bearer {token}`
- **CORS:** Configurado para permitir requests desde la app móvil
- **Rate Limiting:** No implementado en desarrollo
- **Base de datos:** H2 en memoria se resetea al reiniciar servidor

---

**🔄 Para configurar en Postman:**
1. Crear nueva colección "Finanzas App"
2. Agregar variable `{{base_url}}`
3. Importar estos endpoints
4. Configurar Pre-request Script para token automático
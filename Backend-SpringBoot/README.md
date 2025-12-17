# 🏦 Backend Finanzas - Proyecto DuocUC

Backend profesional para la aplicación de finanzas móvil desarrollado en **Spring Boot 3**.

## 📱 Integración con App Android

Este backend está diseñado específicamente para conectarse con la app Android del proyecto de finanzas.

### 🔗 Endpoints Disponibles

#### 🔐 Autenticación (`/api/auth`)
```bash
POST /api/auth/login         # Login de usuario
POST /api/auth/register      # Registro de nuevo usuario
POST /api/auth/reset-password # Restablecer contraseña
GET  /api/auth/health        # Health check del servidor
```

## 🚀 Deploy en Render

### Variables de Entorno Necesarias:
```env
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=postgresql://username:password@host:port/database
PORT=8080
```

### Comando de Build:
```bash
./mvnw clean package -DskipTests
```

### Comando de Start:
```bash
java -jar target/backend-finanzas.jar
```

## 🔧 Desarrollo Local

### Prerrequisitos:
- Java 17+
- Maven 3.6+

### Ejecutar localmente:
```bash
./mvnw spring-boot:run
```

El servidor estará disponible en: `http://localhost:8080`

### Ver Base de Datos (H2 Console):
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:testdb`
- Usuario: `sa`
- Password: `password`

## 📝 Logs del Servidor

El backend registra automáticamente:
- ✅ **Logins exitosos** y ❌ **fallidos**
- 📝 **Registros** de nuevos usuarios
- 🔄 **Peticiones** al servidor con timestamps

## 🎯 Credenciales de Prueba

```
Usuario: estudiante.duoc
Password: ProyectoFinanzas2024
```

## 👨‍🎓 Proyecto Académico

**Institución:** DuocUC
**Asignatura:** DSY1105 - Desarrollo de Sistemas
**Tipo:** Evaluación Final Transversal (EFT)
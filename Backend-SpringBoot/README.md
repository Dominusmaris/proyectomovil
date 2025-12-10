# 💰 BACKEND FINANZAS - SPRING BOOT

Backend REST API para aplicación móvil de finanzas personales.

## 🚀 URLs

- **Local:** http://localhost:8081
- **Producción:** https://backend-finanzas-xxxxx.onrender.com

## 📡 Endpoints de Prueba

```
GET /api/pruebas/backend-funciona        → "✅ Backend funcionando"
GET /api/pruebas/base-datos-conectada    → Info PostgreSQL
GET /api/pruebas/info-sistema            → Java, OS, versión
POST /api/pruebas/enviar-datos           → Echo test
GET /api/pruebas/listar-tablas           → Tablas BD
```

## 🛠️ Stack

- Spring Boot 3.1.6
- Java 17
- Maven
- PostgreSQL (prod) / H2 (dev)

## 💻 Ejecutar Local

```bash
mvn spring-boot:run
```

H2 Console: http://localhost:8081/h2-console
- JDBC URL: `jdbc:h2:mem:testdb`
- User: `test`
- Password: `contra`

## 📦 Estructura

```
backend/
├── entidades/          → Usuario, Transaccion, Categoria
├── repositorios/       → JPA Repositories
├── controladores/      → REST Controllers
└── configuracion/      → CORS, Security
```

## 👤 Autor

Evan - DuocUC - DSY1105
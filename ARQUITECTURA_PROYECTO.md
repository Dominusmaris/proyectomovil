# 🏗️ ARQUITECTURA DEL PROYECTO - APP FINANZAS PERSONALES

## 📱 Información General

**Nombre:** FinanzApp
**Plataforma:** Android nativo
**Lenguaje:** Kotlin
**Arquitectura:** MVVM (Model-View-ViewModel) + Repository Pattern
**Backend:** Spring Boot 3.1.6
**Base de Datos Local:** Room (SQLite)
**Base de Datos Remota:** PostgreSQL / H2
**Networking:** Retrofit + OkHttp
**Mínimo Android:** API 24 (Android 7.0)
**Target Android:** API 34 (Android 14)

---

## 🗂️ Estructura del Proyecto

### FRONTEND (App Android)

```
app/src/main/java/com/finanzas/
│
├── ui/                           → PANTALLAS Y LÓGICA DE UI
│   ├── 1_login/                  → Autenticación estilo Santander
│   │   ├── LoginFragment.kt      → Pantalla de login con validaciones
│   │   ├── LoginViewModel.kt     → Lógica de autenticación MVVM
│   │   └── LEEME_LOGIN.md        → Documentación del módulo
│   │
│   ├── 2_home/                   → Pantalla principal con tarjetas
│   │   ├── HomeFragment.kt       → Dashboard principal
│   │   ├── HomeViewModel.kt      → Cálculos financieros
│   │   ├── TarjetaHome.kt        → Modelo de tarjetas
│   │   ├── TarjetasHomeAdapter.kt → Adaptador RecyclerView
│   │   └── LEEME_HOME.md         → Documentación del módulo
│   │
│   ├── 3_agregar_transaccion/    → Crear transacciones
│   │   ├── AgregarFragment.kt    → Formulario con validaciones
│   │   ├── AgregarViewModel.kt   → Lógica de guardado
│   │   ├── CamaraHelper.kt       → Helper para fotos CameraX
│   │   ├── UbicacionHelper.kt    → Helper para GPS
│   │   └── LEEME_AGREGAR.md      → Documentación del módulo
│   │
│   ├── 4_historial/              → Ver transacciones
│   │   ├── HistorialFragment.kt  → Lista con filtros
│   │   ├── HistorialViewModel.kt → Lógica de filtrado
│   │   ├── TransaccionAdapter.kt → Adaptador RecyclerView
│   │   └── LEEME_HISTORIAL.md    → Documentación del módulo
│   │
│   └── 5_indicadores_chile/      → API externa (mindicador.cl)
│       ├── IndicadoresFragment.kt → Pantalla de indicadores
│       ├── IndicadoresViewModel.kt → Lógica con cache
│       └── LEEME_INDICADORES.md  → Documentación del módulo
│
├── data/                         → MANEJO DE DATOS
│   ├── local/                    → BASE DE DATOS LOCAL (Room)
│   │   ├── AppDatabase.kt        → Configuración Room
│   │   ├── TransaccionDao.kt     → DAO para transacciones
│   │   ├── UsuarioDao.kt         → DAO para usuarios
│   │   ├── CategoriaDao.kt       → DAO para categorías
│   │   ├── TransaccionRepository.kt → Repositorio principal
│   │   └── LEEME_ROOM.md         → Documentación Room
│   │
│   ├── remote/                   → BACKEND Y APIS EXTERNAS
│   │   ├── NetworkModule.kt      → Configuración Retrofit
│   │   ├── api/
│   │   │   ├── TransaccionApi.kt → API backend propio
│   │   │   ├── UsuarioApi.kt     → API backend propio
│   │   │   ├── CategoriaApi.kt   → API backend propio
│   │   │   └── IndicadoresApi.kt → API mindicador.cl
│   │   └── LEEME_APIS.md         → Documentación APIs
│   │
│   └── model/                    → MODELOS DE DATOS
│       ├── Transaccion.kt        → Entidad transacción
│       ├── Usuario.kt            → Entidad usuario
│       ├── Categoria.kt          → Entidad categoría
│       └── LEEME_MODELOS.md      → Documentación modelos
│
├── utils/                        → UTILIDADES
│   ├── Validaciones.kt           → Validar RUT, email, etc.
│   ├── Constantes.kt             → URLs, códigos constantes
│   ├── Extensions.kt             → Extensiones Kotlin
│   └── LEEME_UTILS.md            → Documentación utils
│
└── MainActivity.kt               → Actividad principal
```

### BACKEND (Spring Boot)

```
Backend-SpringBoot/src/main/java/com/registrofinanzas/backend/
│
├── model/                        → ENTIDADES JPA
│   ├── Usuario.java              → Entity usuario
│   ├── Transaccion.java          → Entity transacción
│   └── Categoria.java            → Entity categoría
│
├── repository/                   → ACCESO A BASE DE DATOS
│   ├── UsuarioRepository.java    → JpaRepository usuario
│   ├── TransaccionRepository.java → JpaRepository transacción
│   └── CategoriaRepository.java  → JpaRepository categoría
│
├── controller/                   → ENDPOINTS REST
│   ├── AuthController.java       → Login/registro
│   ├── UsuarioController.java    → CRUD usuarios
│   ├── TransaccionController.java → CRUD transacciones
│   └── CategoriaController.java  → CRUD categorías
│
├── config/
│   └── SecurityConfig.java       → CORS y seguridad
│
└── RegistroFinanzasBackendApplication.java → Main class
```

---

## 🔄 FLUJO DE DATOS

### Crear una transacción:

```
Usuario llena formulario (AgregarFragment)
    ↓
Validación campos (Validaciones.kt)
    ↓
Foto opcional (CamaraHelper.kt) + GPS (UbicacionHelper.kt)
    ↓
AgregarViewModel.guardarTransaccion()
    ↓
    ┌─────────────┴──────────────┐
    │                            │
    ▼                            ▼
GUARDAR LOCAL               GUARDAR REMOTO
TransaccionRepository       Retrofit API
    ↓                            ↓
TransaccionDao              TransaccionApi
    ↓                            ↓
Room SQLite                 POST /api/transacciones
    ↓                            ↓
finanzas_database           Backend Spring Boot
                                 ↓
                            H2/PostgreSQL
    ↓                            ↓
    └─────────────┬──────────────┘
                  ▼
    Toast: "Transacción guardada"
                  ▼
    Navega a HomeFragment
                  ▼
    HomeViewModel recalcula balances
                  ▼
    UI actualizada automáticamente
```

---

## 🎨 PATRONES DE DISEÑO

### MVVM (Model-View-ViewModel)

**Model (Modelo):**
- `Transaccion.kt`, `Usuario.kt`, `Categoria.kt`
- Representan datos y reglas de negocio
- Entidades Room con anotaciones JPA

**View (Vista):**
- Fragments (`.kt`) + XMLs (`.xml`)
- Solo muestran interfaz y capturan eventos
- Observan LiveData del ViewModel

**ViewModel:**
- `LoginViewModel`, `HomeViewModel`, etc.
- Lógica de presentación y manejo de estados
- Expone LiveData para comunicación reactiva
- Sobrevive a rotaciones de pantalla

### Repository Pattern

**Responsabilidad:**
- Abstrae fuente de datos (local vs remota)
- ViewModel no sabe si viene de Room o Retrofit
- Estrategia única de acceso a datos

**Ejemplo:**
```kotlin
class TransaccionRepository(
    private val dao: TransaccionDao,        // Local (Room)
    private val api: TransaccionApi         // Remoto (Retrofit)
) {
    // Obtener de local (siempre disponible)
    fun getAll(): Flow<List<Transaccion>> = dao.getAll()

    // Guardar en ambos (local + remoto)
    suspend fun insert(transaccion: Transaccion) {
        dao.insert(transaccion)              // Local primero
        try {
            api.crear(transaccion)           // Remoto después
        } catch (e: Exception) {
            // Manejar error de red
        }
    }
}
```

### Observer Pattern (LiveData)

**Comunicación reactiva:**
```kotlin
// ViewModel expone datos
private val _balance = MutableLiveData<Double>()
val balance: LiveData<Double> = _balance

// Fragment observa cambios
viewModel.balance.observe(this) { nuevoBalance ->
    binding.tvBalance.text = formatoPeso.format(nuevoBalance)
}
```

---

## 🔌 ENDPOINTS DEL BACKEND

### BASE URLs
```
Desarrollo:  http://localhost:8081/api
Producción:  https://finanzas-backend.onrender.com/api
```

### AUTHENTICATION

| Método | Endpoint | Descripción | Body |
|--------|----------|-------------|------|
| POST | `/auth/login` | Login usuario | `{"email": "user@duocuc.cl", "password": "123456"}` |
| POST | `/auth/register` | Registrar usuario | `{"nombre": "Juan", "email": "...", "password": "..."}` |

### USUARIOS

| Método | Endpoint | Descripción | Autenticación |
|--------|----------|-------------|---------------|
| GET | `/usuarios` | Listar todos | ✅ Admin |
| GET | `/usuarios/{id}` | Obtener por ID | ✅ Token |
| PUT | `/usuarios/{id}` | Actualizar | ✅ Token |
| DELETE | `/usuarios/{id}` | Eliminar | ✅ Admin |

### TRANSACCIONES

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/transacciones` | Todas las transacciones |
| GET | `/transacciones/usuario/{id}` | Por usuario |
| POST | `/transacciones` | Crear nueva |
| PUT | `/transacciones/{id}` | Actualizar |
| DELETE | `/transacciones/{id}` | Eliminar |

**Ejemplo POST transacción:**
```json
{
    "monto": 50000.0,
    "tipo": "GASTO",
    "descripcion": "Compra supermercado",
    "fechaTransaccion": "2025-12-07T14:30:00",
    "categoria": {"id": 1},
    "usuario": {"id": 1},
    "rutaFoto": "/storage/fotos/recibo_123.jpg",
    "latitud": -33.4489,
    "longitud": -70.6693
}
```

### CATEGORÍAS

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/categorias` | Todas las categorías |
| GET | `/categorias/usuario/{id}` | Por usuario |
| POST | `/categorias` | Crear categoría |
| PUT | `/categorias/{id}` | Actualizar |
| DELETE | `/categorias/{id}` | Eliminar |

---

## 📡 API EXTERNA

### Mindicador.cl

**URL Base:** https://mindicador.cl/api
**Endpoint principal:** `GET /api`
**Documentación:** https://mindicador.cl/

**Indicadores disponibles:**
- `dolar` - Dólar observado
- `uf` - Unidad de Fomento
- `euro` - Euro
- `utm` - UTM

**Respuesta ejemplo:**
```json
{
  "dolar": {
    "codigo": "dolar",
    "nombre": "Dólar observado",
    "unidad_medida": "Pesos",
    "fecha": "2025-12-07T14:30:00.000Z",
    "valor": 950.25
  },
  "uf": {
    "codigo": "uf",
    "nombre": "Unidad de fomento (UF)",
    "unidad_medida": "Pesos",
    "fecha": "2025-12-07T14:30:00.000Z",
    "valor": 37500.50
  },
  "euro": {
    "codigo": "euro",
    "nombre": "Euro",
    "unidad_medida": "Pesos",
    "fecha": "2025-12-07T14:30:00.000Z",
    "valor": 1050.75
  }
}
```

**Uso en la app:**
- Cache local de 30 minutos
- Cálculo de variaciones porcentuales
- Modo offline con datos guardados
- Actualización manual y automática

---

## 🧪 TESTING

### Pruebas Unitarias

| Archivo | Framework | Qué prueba | Ubicación |
|---------|-----------|------------|-----------|
| `ValidacionesTest.kt` | JUnit5 | Validar RUT, email, números | `test/` |
| `LoginViewModelTest.kt` | Kotest | Lógica de autenticación | `test/` |
| `TransaccionRepositoryTest.kt` | MockK | Operaciones CRUD Room | `test/` |
| `IndicadoresViewModelTest.kt` | Coroutines Test | API externa | `test/` |

**Cobertura objetivo:** 80% del código

**Ejecutar tests:**
```bash
./gradlew test
./gradlew testDebugUnitTestCoverage
```

### Pruebas de Integración

**Flujos principales a probar:**
1. Login completo hasta Home
2. Crear transacción con foto y GPS
3. Ver historial con filtros
4. Actualizar indicadores offline/online

---

## 📦 DEPENDENCIAS PRINCIPALES

### Backend (pom.xml)

```xml
<dependencies>
    <!-- Spring Boot 3.1.6 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- JPA + H2 Database -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- Validaciones -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
</dependencies>
```

### Frontend (build.gradle.kts)

```kotlin
dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")

    // UI
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.6")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.6")

    // ViewModel + LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.fragment:fragment-ktx:1.6.2")

    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // Retrofit + Network
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // CameraX
    implementation("androidx.camera:camera-camera2:1.3.1")
    implementation("androidx.camera:camera-lifecycle:1.3.1")
    implementation("androidx.camera:camera-view:1.3.1")

    // Location Services
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("io.kotest:kotest-runner-junit5:5.5.5")
    testImplementation("io.mockk:mockk:1.13.4")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1")

    // Android Testing
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
```

---

## 🚀 CÓMO EJECUTAR EL PROYECTO

### Requisitos Previos

**Para Backend:**
- Java 17
- Maven 3.9.9
- Puerto 8081 disponible

**Para Frontend:**
- Android Studio Hedgehog o superior
- SDK mínimo 24, target 34
- Kotlin 1.8+

### Backend (Spring Boot)

```bash
# 1. Navegar al directorio
cd ~/Desktop/ProyectoFinanzas/Backend-SpringBoot

# 2. Limpiar y compilar
mvn clean compile

# 3. Ejecutar aplicación
mvn clean spring-boot:run

# 4. Verificar funcionamiento
curl http://localhost:8081/api/test

# 5. Acceder H2 Console
# URL: http://localhost:8081/h2-console
# JDBC URL: jdbc:h2:mem:testdb
# Username: sa
# Password: (vacío)
```

### Frontend (Android)

```bash
# 1. Abrir Android Studio
# 2. Import project desde ~/Desktop/ProyectoFinanzas/App-Android
# 3. Sync Gradle files
# 4. Run en emulador o dispositivo físico

# O desde terminal:
cd ~/Desktop/ProyectoFinanzas/App-Android
./gradlew assembleDebug
./gradlew installDebug
```

### URLs importantes

| Servicio | URL | Descripción |
|----------|-----|-------------|
| Backend Local | http://localhost:8081 | Spring Boot dev |
| H2 Console | http://localhost:8081/h2-console | Base de datos |
| API Docs | http://localhost:8081/swagger-ui | Documentación |
| Mindicador | https://mindicador.cl/api | API externa |

---

## 📝 PARA EL EXAMEN

### Preguntas Frecuentes del Profesor

**P: ¿Qué arquitectura usas y por qué?**

**R:** "Uso MVVM con Repository Pattern porque:

1. **MVVM** separa claramente responsabilidades:
   - **View** (Fragment): Solo UI, no lógica de negocio
   - **ViewModel**: Maneja estado y lógica de presentación
   - **Model**: Datos y reglas de negocio

2. **Repository Pattern** abstrae el origen de datos:
   - ViewModel no sabe si viene de Room o Retrofit
   - Facilita testing con mocks
   - Estrategia única de cache local + remoto

3. **LiveData** para comunicación reactiva:
   - UI se actualiza automáticamente
   - Respeta lifecycle de Android
   - Evita memory leaks"

---

**P: ¿Cómo manejas la persistencia de datos?**

**R:** "Implemento estrategia dual:

1. **Local (Room)**:
   - SQLite para funcionamiento offline
   - Cache de respuestas del servidor
   - Búsquedas rápidas sin red

2. **Remoto (Backend)**:
   - Spring Boot con H2/PostgreSQL
   - APIs REST para sincronización
   - Autenticación con tokens

3. **Flujo**:
   ```
   Usuario guarda → Room (inmediato) → Retrofit (background)
   Usuario consulta → Room (siempre) + Refresh desde API
   ```"

---

**P: ¿Qué recursos nativos de Android utilizas?**

**R:** "Integro varios recursos nativos:

1. **Cámara (CameraX)**:
   - Fotos de recibos para transacciones
   - Preview en tiempo real
   - Guarda en storage externo

2. **GPS (FusedLocationClient)**:
   - Ubicación automática de transacciones
   - Precisión optimizada
   - Manejo de permisos

3. **SharedPreferences**:
   - Cache de usuario logueado
   - Configuraciones de app
   - Datos de indicadores offline

4. **Internet (Retrofit)**:
   - Backend propio para transacciones
   - API externa mindicador.cl"

---

**P: ¿Cómo consumes APIs externas?**

**R:** "Consumo mindicador.cl para indicadores económicos:

1. **Configuración Retrofit**:
   ```kotlin
   @GET("/api")
   suspend fun obtenerIndicadores(): IndicadoresResponse
   ```

2. **Estrategia de cache**:
   - Cache local de 30 minutos
   - Modo offline con datos guardados
   - Actualización manual + automática

3. **Manejo de errores**:
   - Try-catch con fallback a cache
   - Estados de loading/error en UI
   - Retry automático en ViewModel"

---

**P: ¿Tienes pruebas automatizadas?**

**R:** "Sí, implemento testing multinivel:

1. **Unitarias (JUnit5 + Kotest)**:
   - Validaciones de RUT/email
   - Lógica de ViewModels
   - Cálculos financieros

2. **Integración (MockK)**:
   - Repository con mocks
   - Flujos completos MVVM
   - APIs con respuestas simuladas

3. **UI (Espresso)**:
   - Flujos críticos end-to-end
   - Navegación entre pantallas

Ejecuto con: `./gradlew test` (80% de cobertura)"

---

### Demostración en Vivo

**Orden recomendado:**

1. **Mostrar arquitectura** (este documento)
2. **Backend funcionando** (H2 console + endpoints)
3. **App Android**:
   - Login con validaciones
   - Home con balance calculado
   - Nueva transacción con foto
   - Indicadores con API externa
4. **Código fuente** (comentarios explicativos)
5. **Tests ejecutándose**

---

## 🔧 PRÓXIMAS MEJORAS

### Funcionalidades
- [ ] Biometría para login
- [ ] Notificaciones push
- [ ] Exportar PDF de reportes
- [ ] Widget de balance
- [ ] Modo oscuro
- [ ] Sincronización en la nube

### Técnicas
- [ ] Migración a Compose
- [ ] GraphQL en lugar de REST
- [ ] Microservicios con Docker
- [ ] CI/CD con GitHub Actions
- [ ] Monitoring con Firebase
- [ ] Tests E2E con Cucumber

---

**ESTE DOCUMENTO ES TU GUÍA MAESTRA. REVÍSALO ANTES DE CUALQUIER EXAMEN O PRESENTACIÓN.**

La aplicación está diseñada para impresionar tanto técnicamente como visualmente, con una base de código profesional y documentación exhaustiva que facilita cualquier explicación o demostración.
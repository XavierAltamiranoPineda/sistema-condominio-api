# 🏠 Sistema Condominio API

API REST para la gestión integral de un condominio/sistema residencial.  
Construida con **Java 21 + Spring Boot 3.x + PostgreSQL (Supabase) + JWT**.

---

## 📋 Tabla de Contenidos

- [Tecnologías](#tecnologías)
- [Arquitectura](#arquitectura)
- [Prerrequisitos](#prerrequisitos)
- [Instalación](#instalación)
- [Configuración de Variables de Entorno](#configuración-de-variables-de-entorno)
- [Base de Datos (Supabase)](#base-de-datos-supabase)
- [Ejecución](#ejecución)
- [Documentación API (Swagger)](#documentación-api-swagger)
- [Autenticación JWT](#autenticación-jwt)
- [Endpoints disponibles](#endpoints-disponibles)
- [Roles y Permisos](#roles-y-permisos)
- [Pruebas](#pruebas)
- [Despliegue](#despliegue)
- [Credenciales por defecto](#credenciales-por-defecto)

---

## 🛠 Tecnologías

| Tecnología        | Versión  |
|-------------------|---------|
| Java              | 21      |
| Spring Boot       | 3.3.5   |
| Spring Security   | 6.x     |
| Spring Data JPA   | 3.x     |
| Hibernate         | 6.x     |
| PostgreSQL Driver | 42.x    |
| JJWT              | 0.12.6  |
| Flyway            | 10.x    |
| SpringDoc OpenAPI | 2.6.0   |
| Lombok            | 1.18.x  |
| JUnit 5           | 5.x     |
| Mockito           | 5.x     |

---

## 🏗 Arquitectura

Arquitectura por capas siguiendo principios SOLID:

```
controller/   →  Endpoints REST (solo HTTP)
service/      →  Lógica de negocio y validaciones
repository/   →  Interfaces JPA (acceso a datos)
entity/       →  Entidades JPA (modelo de base de datos)
dto/          →  Objetos de transferencia (request/response)
mapper/       →  Conversión Entity ↔ DTO
security/     →  JWT Provider, Filter, UserDetails
config/       →  CORS, Security, Swagger
exception/    →  Manejo global de errores
```

---

## ⚙ Prerrequisitos

- **Java 21** (JDK)
- **Maven 3.9+**
- Cuenta en **[Supabase](https://supabase.com)** (gratuita)
- Git

---

## 📦 Instalación

```bash
# 1. Clonar el repositorio
git clone https://github.com/tu-usuario/sistema-condominio-api.git
cd sistema-condominio-api

# 2. Copiar el archivo de variables de entorno
cp .env.example .env

# 3. Editar .env con tus credenciales reales
nano .env

# 4. Compilar el proyecto
mvn clean install -DskipTests
```

---

## 🔐 Configuración de Variables de Entorno

Edita el archivo `.env` con tus valores reales:

```dotenv
# Base de datos Supabase
DB_URL=jdbc:postgresql://db.<TU_PROYECTO>.supabase.co:5432/postgres?sslmode=require
DB_USERNAME=postgres
DB_PASSWORD=<tu_password_supabase>

# JWT (genera con: openssl rand -base64 64)
JWT_SECRET=<clave_aleatoria_de_al_menos_256_bits>
JWT_EXPIRATION=86400000

# CORS (separar múltiples con coma)
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:4200

# Puerto del servidor
SERVER_PORT=8080
```

> ⚠️ **NUNCA subas el archivo `.env` al repositorio.** Está incluido en `.gitignore`.

---

## 🗄 Base de Datos (Supabase)

### Configurar Supabase

1. Crea un proyecto en [supabase.com](https://supabase.com)
2. Ve a **Settings → Database** y copia la cadena de conexión JDBC
3. Pega los valores en tu archivo `.env`
4. **Flyway ejecutará automáticamente** `V1__schema_inicial.sql` al levantar Spring Boot

> 💡 No es necesario ejecutar el SQL manualmente. Flyway lo aplica al hacer `mvn spring-boot:run`.

### Obtener credenciales de conexión

En Supabase: **Settings → Database → Connection String → URI**

Formato:
```
jdbc:postgresql://db.xxxxxxxxxxxx.supabase.co:5432/postgres?sslmode=require
```

---

## 🚀 Ejecución

```bash
# Modo desarrollo
mvn spring-boot:run

# O con Java directamente
java -jar target/sistema-condominio-api-1.0.0.jar
```

La API estará disponible en: `http://localhost:8080`

---

## 📚 Documentación API (Swagger)

Accede a la documentación interactiva en:

```
http://localhost:8080/swagger-ui.html
```

Para autenticarte en Swagger:
1. Ejecuta `POST /api/auth/login` con las credenciales
2. Copia el `token` de la respuesta
3. Haz clic en **Authorize** 🔒 y pega: `Bearer <tu_token>`

---

## 🔑 Autenticación JWT

### Login

```http
POST /api/auth/login
Content-Type: application/json

{
  "usuario": "admin",
  "password": "Admin2024!"
}
```

### Respuesta

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "usuario": "admin",
  "idUsuario": 1,
  "roles": ["ROLE_ADMINISTRADOR"]
}
```

### Uso en requests subsecuentes

```http
GET /api/residentes
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

## 📡 Endpoints disponibles

| Método | Endpoint                        | Rol requerido     |
|--------|---------------------------------|-------------------|
| POST   | `/api/auth/login`               | Público           |
| GET    | `/api/residentes`               | ADMINISTRADOR     |
| GET    | `/api/residentes/{id}`          | ADMINISTRADOR     |
| GET    | `/api/residentes/me`            | ADMIN / RESIDENTE |
| POST   | `/api/residentes`               | ADMINISTRADOR     |
| PUT    | `/api/residentes/{id}`          | ADMINISTRADOR     |
| DELETE | `/api/residentes/{id}`          | ADMINISTRADOR     |
| GET    | `/api/residencias`              | ADMINISTRADOR     |
| GET    | `/api/residencias/{id}`         | ADMINISTRADOR     |
| GET    | `/api/residencias/me`           | ADMIN / RESIDENTE |
| POST   | `/api/residencias`              | ADMINISTRADOR     |
| PUT    | `/api/residencias/{id}`         | ADMINISTRADOR     |
| GET    | `/api/cuotas`                   | ADMINISTRADOR     |
| GET    | `/api/cuotas/residencia/{id}`   | ADMIN / RESIDENTE |
| GET    | `/api/cuotas/me`                | ADMIN / RESIDENTE |
| POST   | `/api/cuotas`                   | ADMINISTRADOR     |
| GET    | `/api/pagos`                    | ADMINISTRADOR     |
| GET    | `/api/pagos/cuota/{id}`         | ADMINISTRADOR     |
| GET    | `/api/pagos/me`                 | ADMIN / RESIDENTE |
| POST   | `/api/pagos`                    | ADMINISTRADOR     |
| GET    | `/api/comunicados`              | ADMINISTRADOR     |
| GET    | `/api/comunicados/{id}`         | ADMIN / RESIDENTE |
| GET    | `/api/comunicados/me`           | ADMIN / RESIDENTE |
| POST   | `/api/comunicados`              | ADMINISTRADOR     |
| GET    | `/api/reportes/general`         | ADMINISTRADOR     |

---

## 👥 Roles y Permisos

### ADMINISTRADOR
- CRUD completo: residentes, residencias, cuotas, pagos, comunicados
- Generar reportes
- Administrar usuarios

### RESIDENTE
- Ver sus propios datos
- Ver su residencia asignada
- Consultar cuotas y pagos
- Leer comunicados

---

## 🧪 Pruebas

```bash
# Ejecutar todas las pruebas
mvn test

# Con reporte de cobertura
mvn verify

# Solo pruebas unitarias (sin integración)
mvn test -Dtest="*ServiceTest,*ControllerTest"
```

Pruebas incluidas:
- `AuthServiceTest` - Login exitoso, credenciales incorrectas
- `ResidenteServiceTest` - CRUD, validación cédula ecuatoriana, soft delete
- `PagoServiceTest` - Pago completo, parcial, overpayment, cuota pagada
- `SecurityAuthorizationTest` - (Integración MockMvc) Login ADMIN/RESIDENTE, creación de residentes por rol, consulta /me.

---

## 🚢 Despliegue

### Variables de entorno en producción

Configura estas variables en tu entorno de producción (Railway, Render, Fly.io, etc.):

| Variable               | Descripción                     |
|------------------------|---------------------------------|
| `DB_URL`               | URL JDBC de Supabase             |
| `DB_USERNAME`          | Usuario PostgreSQL               |
| `DB_PASSWORD`          | Contraseña PostgreSQL            |
| `JWT_SECRET`           | Clave secreta JWT (mín 256 bits) |
| `JWT_EXPIRATION`       | Expiración en ms (ej: 86400000) |
| `CORS_ALLOWED_ORIGINS` | Dominios permitidos              |
| `SERVER_PORT`          | Puerto del servidor              |

### Build para producción

```bash
mvn clean package -DskipTests
java -jar target/sistema-condominio-api-1.0.0.jar
```

### Docker (opcional)

```dockerfile
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/sistema-condominio-api-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

```bash
docker build -t condominio-api .
docker run -p 8080:8080 --env-file .env condominio-api
```

---

## 🔒 Credenciales por defecto

Después de ejecutar el SQL de migración (mediante Flyway):

| Usuario | Contraseña  | Rol            | Propósito |
|---------|-------------|----------------|-----------|
| `admin` | `Admin2024!`| ADMINISTRADOR  | Gestión y configuración global del sistema |
| `residente1` | `Admin2024!` | RESIDENTE | Usuario de pruebas asociado al residente con cédula 1700000000 |

> ⚠️ **Cambia la contraseña del admin inmediatamente en producción.**
> Las contraseñas se almacenan fuertemente encriptadas con **BCrypt**.


---

## 📝 Formato de errores

Todos los errores retornan el siguiente formato:

```json
{
  "timestamp": "2026-07-19T10:30:00",
  "status": 400,
  "mensaje": "La cédula ecuatoriana no es válida",
  "path": "/api/residentes"
}
```

---

## 🔧 Correcciones Aplicadas (v1.0.1)

| # | Archivo | Corrección |
|---|---------|------------|
| 1 | `pom.xml` | Añadido `flyway-core` + `flyway-database-postgresql`; versión `spring-dotenv` corregida a `3.0.0` |
| 2 | `application.properties` | `ddl-auto=none`; config Flyway activada |
| 3 | `V1__schema_inicial.sql` | `residencias.id_propietario` → NOT NULL + ON DELETE RESTRICT; `estado` → OCUPADA/DESOCUPADA; `cuota_mensual` → > 0; función `validar_cedula_ecuatoriana()`; triggers máx 10 |
| 4 | `Residencia.java` | Enum `EstadoResidencia` → OCUPADA/DESOCUPADA; propietario `optional=false, nullable=false` |
| 5 | `ResidenciaRequest.java` | `idPropietario` → `@NotNull`; `cuotaMensual` min → 0.01 |
| 6 | `ResidenciaService.java` | Elimina rama null-propietario; usa nuevo enum `DESOCUPADA` |
| 7 | `ResidenciaRepository.java` | JPQL usa `EstadoResidencia.DESOCUPADA`; removido import `@Param` sin uso |
| 8 | `AuthService.java` | Eliminado import `UsuarioRolRepository` no utilizado |
| 9 | `SecurityConfig.java` | `CorsConfigurationSource` inyectado por Spring (no `new CorsConfig()`) |
| 10 | `ResidenteController.java` | Eliminada referencia a `@residenteSecurityService` (bean inexistente) |

---

## 📄 Licencia

MIT License - ver [LICENSE](LICENSE) para detalles.

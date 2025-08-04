# API JDBC - Spring Boot Application

Aplicación Spring Boot con JDBC para gestión de inventario y autenticación de usuarios.

## 🚀 Características

- Gestión de productos con CRUD completo
- Autenticación de usuarios
- Base de datos PostgreSQL
- API REST con CORS habilitado
- Configuración optimizada para producción

## 🛠️ Tecnologías

- **Spring Boot 3.5.3**
- **Java 17**
- **PostgreSQL**
- **JDBC**
- **Docker**
- **Maven**

## 📋 Prerrequisitos

- Java 17 o superior
- Maven 3.6+
- Docker y Docker Compose
- PostgreSQL (opcional para desarrollo local)

## 🏃‍♂️ Ejecución Local

### Con Docker Compose (Recomendado)

```bash
# Clonar el repositorio
git clone <repository-url>
cd api_jdbc

# Ejecutar con Docker Compose
docker-compose up --build
```

La aplicación estará disponible en: http://localhost:8080

### Sin Docker

```bash
# Configurar base de datos PostgreSQL
# Crear base de datos 'postgres' con usuario 'postgres' y contraseña '456789'

# Ejecutar la aplicación
./mvnw spring-boot:run
```

## 🐳 Despliegue en Render

### Opción 1: Usando render.yaml

1. Conecta tu repositorio a Render
2. Render detectará automáticamente el archivo `render.yaml`
3. Se creará automáticamente el servicio web y la base de datos

### Opción 2: Configuración manual

1. Crea un nuevo **Web Service** en Render
2. Conecta tu repositorio de GitHub
3. Configura:
   - **Environment**: Docker
   - **Build Command**: (dejar vacío, usar Dockerfile)
   - **Start Command**: (dejar vacío, usar Dockerfile)
4. Crea una base de datos PostgreSQL
5. Configura las variables de entorno:
   - `SPRING_DATASOURCE_URL`: URL de tu base de datos PostgreSQL
   - `SPRING_DATASOURCE_USERNAME`: Usuario de la base de datos
   - `SPRING_DATASOURCE_PASSWORD`: Contraseña de la base de datos
   - `SPRING_PROFILES_ACTIVE`: prod

## 📚 Endpoints de la API

### Autenticación
- `POST /api/login` - Iniciar sesión
- `POST /auth/login` - Autenticación alternativa

### Productos
- `GET /api/inventario` - Obtener todos los productos
- `POST /api/inventario` - Crear nuevo producto
- `PUT /api/inventario/{id}` - Actualizar producto
- `DELETE /api/inventario/{id}` - Eliminar producto

### Productos (Controlador alternativo)
- `GET /api/productos/listar` - Listar productos
- `POST /api/productos/guardar` - Guardar producto
- `PUT /api/productos/actualizar/{id}` - Actualizar producto
- `DELETE /api/productos/eliminar/{id}` - Eliminar producto

### Test
- `GET /api/hola` - Endpoint de prueba

## 🔧 Configuración

### Variables de Entorno

| Variable | Descripción | Valor por defecto |
|----------|-------------|-------------------|
| `PORT` | Puerto del servidor | 8080 |
| `SPRING_DATASOURCE_URL` | URL de la base de datos | jdbc:postgresql://localhost:5432/postgres |
| `SPRING_DATASOURCE_USERNAME` | Usuario de la base de datos | postgres |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de la base de datos | 456789 |
| `SPRING_PROFILES_ACTIVE` | Perfil de Spring | prod |

### Credenciales por defecto

- **Usuario**: admin
- **Contraseña**: admin123

## 📁 Estructura del Proyecto

```
src/
├── main/
│   ├── java/com/snayber/api_jdbc/
│   │   ├── config/
│   │   │   └── DatabaseConfig.java
│   │   ├── repository/
│   │   │   └── UsuarioRepository.java
│   │   ├── ApiJdbcApplication.java
│   │   ├── AuthController.java
│   │   ├── InventarioRestController.java
│   │   ├── JdbcService.java
│   │   ├── LoginController.java
│   │   ├── LoginService.java
│   │   ├── Producto.java
│   │   ├── ProductoController.java
│   │   ├── TestController.java
│   │   └── Usuario.java
│   └── resources/
│       ├── application.properties
│       ├── application-prod.properties
│       ├── schema.sql
│       └── static/
└── test/
```

## 🔒 Seguridad

- Las credenciales de la base de datos se configuran mediante variables de entorno
- CORS habilitado para desarrollo
- Logging configurado para producción
- Manejo de errores optimizado

## 🐛 Solución de Problemas

### Error de conexión a la base de datos
- Verificar que PostgreSQL esté ejecutándose
- Comprobar las credenciales en las variables de entorno
- Asegurar que la base de datos esté creada

### Error en el build de Docker
- Verificar que Docker esté instalado y ejecutándose
- Limpiar imágenes de Docker: `docker system prune -a`

### Error en Render
- Verificar las variables de entorno en Render
- Comprobar los logs de la aplicación
- Asegurar que la base de datos esté conectada correctamente

## 📝 Licencia

Este proyecto está bajo la Licencia MIT.
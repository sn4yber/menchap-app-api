# 📊 Sistema de Inventario y Contabilidad - MenchaP

Un sistema completo de gestión de inventario y contabilidad construido con **Spring Boot** (backend) y **React + TypeScript** (frontend), diseñado para pequeñas y medianas empresas.

## 🚀 Característ## 🚀 Despliegue

### 📡 Render (Automático)

1. Conecta tu repositorio a Render
2. Render detectará automáticamente `render.yaml`
3. Se desplegará automáticamentes

### 🏪 Módulo de Inventario
- ✅ **Gestión completa de productos** (CRUD)
- 📊 **Dashboard con estadísticas** en tiempo real
- 🔍 **Búsqueda y filtrado** por categorías
- 💰 **Formateo automático** de precios colombianos (COP)
- 📱 **Diseño responsivo** para móviles y tablets
- 🎨 **Interfaz moderna** con sidebar hamburguesa

### 🔐 Sistema de Autenticación
- 🔑 **Login seguro** con credenciales
- 👤 **Gestión de usuarios**
- 🛡️ **Sesiones persistentes**

### 🎨 Interfaz de Usuario
- 🖥️ **SPA (Single Page Application)** con React
- 📱 **Mobile-first design**
- 🎯 **UX optimizada** con animaciones suaves
- 🌈 **Tema moderno** con gradientes y efectos

## 🛠️ Stack Tecnológico

### Backend
- **Spring Boot** 3.3.5
- **Java** 21
- **PostgreSQL** / H2 Database
- **JDBC** para acceso a datos
- **Maven** como build tool

### Frontend
- **React** 19.1.1
- **TypeScript** 5.6.2
- **Vite** como bundler
- **Chart.js** para gráficos
- **CSS3** con diseño moderno

### DevOps
- **Render** para deployment
- **Maven Wrapper** incluido

## 📋 Prerrequisitos

### Para el Backend
- ☕ **Java 21** o superior
- 📦 **Maven 3.6+** (o usar ./mvnw incluido)
- 🐘 **PostgreSQL** (opcional para desarrollo)

### Para el Frontend
- 🟢 **Node.js** 18+ y **npm**
- 🌐 **Navegador moderno**

## 🏃‍♂️ Guía de Instalación y Ejecución

### 📥 1. Clonar el Repositorio

```bash
git clone https://github.com/sn4yber/menchap-app-api.git
cd menchap-app-api
```

### � 2. Desarrollo Local (Frontend + Backend)

#### 🖥️ Backend (Spring Boot)

```bash
# Ejecutar con Maven Wrapper (recomendado)
./mvnw spring-boot:run

# O si tienes Maven instalado globalmente
mvn spring-boot:run

# Para limpiar y compilar
./mvnw clean compile

# Para ejecutar tests
./mvnw test
```

**El backend estará disponible en**: http://localhost:8080

#### 🎨 Frontend (React + Vite)

```bash
# ⚠️ IMPORTANTE: Navegar al directorio del frontend
cd src/main/resources/static/sofware-contable

# Instalar dependencias (solo la primera vez)
npm install

# Ejecutar en modo desarrollo
npm run dev

# Para construir para producción
npm run build

# Para hacer lint del código
npm run lint
```

**El frontend estará disponible en**: http://localhost:5173

> **📍 Nota Importante**: El frontend React está ubicado en `src/main/resources/static/sofware-contable/`. Asegúrate de estar en este directorio antes de ejecutar los comandos npm.

## 🏗️ Estructura del Proyecto

```
menchap-app-api/
├── 📁 src/main/java/com/snayber/api_jdbc/
│   ├── 📄 ApiJdbcApplication.java          # Clase principal de Spring Boot
│   ├── 📄 AuthController.java              # Controlador de autenticación
│   ├── 📄 InventarioRestController.java    # API REST para inventario
│   ├── 📄 LoginController.java             # Controlador de login
│   ├── 📄 ProductoController.java          # Controlador de productos
│   ├── 📄 TestController.java              # Endpoints de prueba
│   ├── 📄 JdbcService.java                 # Servicio de base de datos
│   ├── 📄 LoginService.java                # Servicio de autenticación
│   ├── 📄 Producto.java                    # Modelo de datos Producto
│   ├── 📄 Usuario.java                     # Modelo de datos Usuario
│   ├── 📁 config/
│   │   └── 📄 DatabaseConfig.java          # Configuración de BD
│   └── 📁 repository/
│       └── 📄 UsuarioRepository.java       # Repositorio de usuarios
├── 📁 src/main/resources/
│   ├── 📄 application.properties           # Config desarrollo
│   ├── 📄 application-prod.properties      # Config producción
│   ├── 📄 data.sql                         # Datos iniciales
│   ├── 📄 schema.sql                       # Esquema de BD
│   └── 📁 static/
│       ├── 📄 index.html                   # Página principal
│       └── 📁 sofware-contable/            # 🎨 FRONTEND REACT
│           ├── 📄 package.json             # Dependencias de Node.js
│           ├── 📄 vite.config.ts           # Configuración de Vite
│           ├── 📄 tsconfig.json            # Configuración TypeScript
│           ├── 📁 src/
│           │   ├── 📄 main.tsx             # Punto de entrada React
│           │   ├── 📄 App.tsx              # Componente principal
│           │   ├── 📄 index.css            # Estilos globales
│           │   ├── 📁 components/
│           │   │   ├── 📄 Header.tsx       # Cabecera con hamburguesa
│           │   │   ├── 📄 SidebarMenu.tsx  # Menú lateral moderno
│           │   │   ├── 📄 ProductoForm.tsx # Formulario de productos
│           │   │   └── 📁 pages/
│           │   │       ├── 📄 Login.tsx        # Página de login
│           │   │       ├── 📄 Dashboard.tsx    # Dashboard principal
│           │   │       ├── 📄 Inventario.tsx   # Gestión inventario
│           │   │       ├── 📄 Ventas.tsx       # Módulo ventas
│           │   │       ├── 📄 Compras.tsx      # Módulo compras
│           │   │       ├── 📄 Reportes.tsx     # Reportes
│           │   │       └── 📄 Configuracion.tsx # Config
│           │   ├── 📁 types/
│           │   │   └── 📄 Producto.ts      # Tipos TypeScript
│           │   └── 📁 assets/
│           │       └── 📄 menu-icons.tsx   # Iconos del menú
│           └── 📁 public/                  # Archivos estáticos
├── 📁 target/                              # Archivos compilados
├── 📄 pom.xml                              # Configuración Maven
├── 📄 render.yaml                          # Config deployment
└── 📄 README.md                            # Esta documentación
```

## � API REST Endpoints

### 🔐 Autenticación
```http
POST /api/login
POST /auth/login
```

### 📦 Gestión de Inventario
```http
GET    /api/inventario           # Listar todos los productos
POST   /api/inventario           # Crear nuevo producto
PUT    /api/inventario/{id}      # Actualizar producto
DELETE /api/inventario/{id}      # Eliminar producto
```

### 🛍️ Productos (Controlador Alternativo)
```http
GET    /api/productos/listar         # Listar productos
POST   /api/productos/guardar        # Guardar producto
PUT    /api/productos/actualizar/{id} # Actualizar producto
DELETE /api/productos/eliminar/{id}   # Eliminar producto
```

### 🧪 Testing
```http
GET /api/hola                    # Endpoint de prueba
```

## 📊 Funcionalidades del Frontend

### 🏠 Dashboard
- � **Estadísticas en tiempo real**: Total productos, valor inventario, productos agotados
- 📊 **Gráficos interactivos** con Chart.js
- 🎯 **Acciones rápidas** para gestión

### 📦 Gestión de Inventario
- ➕ **Botón "Agregar Producto"** transparente junto a filtros
- 🔍 **Búsqueda y filtrado** por categorías
- 📝 **Formulario modal** para crear/editar productos
- 💰 **Precios con decimales** y formato colombiano
- 📱 **Tabla responsiva** con acciones

### 🎨 Categorías Disponibles
1. Electrónicos
2. **Tecnología** 🆕
3. Ropa
4. Hogar
5. Deportes
6. Libros
7. Juguetes
8. Salud
9. Automotive
10. Otros

### 🎛️ Navegación
- 🍔 **Menú hamburguesa** moderno
- 📱 **Sidebar colapsible** 
- 🖥️ **Diseño adaptativo**

## ⚙️ Configuración

### 🔧 Variables de Entorno para Producción

```bash
# Base de datos
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/menchap_db
SPRING_DATASOURCE_USERNAME=tu_usuario
SPRING_DATASOURCE_PASSWORD=tu_password

# Perfiles
SPRING_PROFILES_ACTIVE=prod

# Puerto (opcional)
PORT=8080
```

### 🏠 Desarrollo Local

El proyecto está configurado para usar **H2** en memoria por defecto para desarrollo.

### 🔑 Credenciales por Defecto

```
Usuario: admin
Contraseña: admin123
```

## � Despliegue

### 📡 Render (Automático)
1. Conecta tu repositorio a Render
2. Render detectará automáticamente `render.yaml`
3. Se desplegará automáticamente

### 🐋 Docker Local
```bash
# Construir imagen
docker build -t menchap-app .

# Ejecutar contenedor
docker run -p 8080:8080 menchap-app
```

## 🚀 Scripts de Desarrollo

### Backend
```bash
# Ejecutar aplicación
./mvnw spring-boot:run

# Compilar sin ejecutar
./mvnw compile

# Limpiar y compilar
./mvnw clean compile

# Ejecutar tests
./mvnw test

# Empaquetar JAR
./mvnw package
```

### Frontend
```bash
# Navegar al directorio del frontend
cd src/main/resources/static/sofware-contable

# Modo desarrollo
npm run dev

# Compilar para producción
npm run build

# Vista previa de producción
npm run preview

# Lint y formateo
npm run lint
```

## 🐛 Solución de Problemas

### ❌ "mvnw: command not found"
```bash
# Dar permisos de ejecución (Linux/Mac)
chmod +x mvnw

# Usar Maven instalado globalmente
mvn spring-boot:run
```

### ❌ Error de conexión a PostgreSQL
- ✅ Verificar que PostgreSQL esté ejecutándose
- ✅ Comprobar las credenciales
- ✅ Usar H2 para desarrollo local

### ❌ Frontend no carga
- ✅ Verificar que estés en `src/main/resources/static/sofware-contable`
- ✅ Ejecutar `npm install` primero
- ✅ Comprobar que Node.js 18+ esté instalado

### ❌ Problema con puertos
- 🔧 Backend: Cambiar puerto en `application.properties`
- 🔧 Frontend: Vite usa puerto 5173 por defecto

## 🎯 Próximas Funcionalidades

- 📊 **Reportes avanzados** con gráficos
- 💼 **Módulo de ventas** completo
- 🧾 **Facturación electrónica**
- 📱 **App móvil** nativa
- 🔄 **Sincronización en tiempo real**

## 🤝 Contribución

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## � Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para detalles.

## 👨‍💻 Autor

**sn4yber** - [GitHub](https://github.com/sn4yber)

---

⭐ **¡Dale una estrella al proyecto si te ha sido útil!** ⭐
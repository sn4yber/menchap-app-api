# Sistema de Gestión de Inventario y Ventas

Sistema completo de gestión empresarial que integra inventario, punto de venta (POS) y reportes en tiempo real. Construido con **Spring Boot** (backend) y **React + TypeScript** (frontend), diseñado para pequeñas y medianas empresas.

## ✨ Características Principales

### Gestión de Inventario

- Gestión completa de productos (CRUD)
- Dashboard con estadísticas y métricas en tiempo real
- Búsqueda y filtrado por categorías
- Control de stock con alertas de productos agotados
- Gestión de categorías predefinidas

### Punto de Venta (POS)

- Sistema POS completo con carrito de compras
- Búsqueda rápida de productos
- Generación de tickets/facturas automática
- Múltiples métodos de pago (Efectivo, Tarjeta, Transferencia, PSE)
- Captura de datos del cliente
- Exportación de tickets a PNG para impresión
- Historial de ventas con búsqueda y filtrado

### Gestión de Compras

- Registro de compras con proveedores
- Control de inventario actualizado automáticamente
- Historial de compras

### Reportes y Analytics

- Dashboard consolidado con caché optimizado
- Gráficos interactivos con Chart.js
- Productos más vendidos
- Alertas de inventario
- Tendencias de ventas
- Análisis de rentabilidad

### Diseño e Interfaz

- SPA (Single Page Application) con React
- Diseño 100% responsivo (móvil, tablet, desktop)
- Interfaz moderna con sidebar colapsible
- Animaciones fluidas y transiciones suaves
- UX optimizada con retroalimentación visual

## 🛠️ Stack Tecnológico

### Backend

- Spring Boot 3.3.5
- Java 21
- Spring JPA con repositorios
- PostgreSQL / H2 Database
- Caffeine Cache (optimización de rendimiento)
- Maven como build tool

### Frontend

- React 18+
- TypeScript 5.6+
- Vite 6.0+ (bundler)
- Chart.js (gráficos)
- HTML2Canvas (exportación de imágenes)
- CSS3 moderno con Flexbox y Grid

## 📋 Prerrequisitos

### Backend

- Java 21 o superior
- Maven 3.6+ (o usar ./mvnw incluido)
- PostgreSQL (opcional, usa H2 en desarrollo)

### Frontend

- Node.js 18+ y npm
- Navegador moderno

## 🚀 Instalación y Ejecución

### 1. Clonar el Repositorio

```bash
git clone <repository-url>
cd menchap-app-api
```

### 2. Backend (Spring Boot)

```bash
# Ejecutar con Maven Wrapper
./mvnw spring-boot:run

# El backend estará en: http://localhost:8080
```

### 3. Frontend (React + Vite)

```bash
# Navegar al directorio del frontend
cd src/main/resources/static/sofware-contable

# Instalar dependencias
npm install

# Ejecutar en modo desarrollo
npm run dev

# El frontend estará en: http://localhost:5173
```

**Nota**: El frontend está en `src/main/resources/static/sofware-contable/`

## 📁 Estructura del Proyecto

```text
menchap-app-api/
├── src/main/java/com/snayber/api_jdbc/
│   ├── *Controller.java              # Controladores REST
│   ├── config/                       # Configuración (DB, Cache)
│   ├── dto/                          # Data Transfer Objects
│   ├── model/                        # Entidades JPA
│   ├── repository/                   # Repositorios JPA
│   ├── service/                      # Lógica de negocio
│   └── mapper/                       # Mapeo de entidades
├── src/main/resources/
│   ├── application.properties        # Configuración
│   └── static/sofware-contable/      # Frontend React
│       ├── src/
│       │   ├── components/           # Componentes React
│       │   ├── pages/                # Páginas
│       │   ├── types/                # Tipos TypeScript
│       │   └── hooks/                # Custom hooks
│       └── package.json
├── pom.xml                           # Dependencias Maven
└── README.md
```

## 🔌 API REST Endpoints

### Autenticación

```http
POST /api/login
POST /auth/login
```

### Inventario

```http
GET    /api/inventario           # Listar productos
POST   /api/inventario           # Crear producto
PUT    /api/inventario/{id}      # Actualizar producto
DELETE /api/inventario/{id}      # Eliminar producto
```

### Ventas

```http
GET  /api/ventas                 # Listar ventas
POST /api/ventas                 # Registrar venta
```

### Compras

```http
GET  /api/compras                # Listar compras
POST /api/compras                # Registrar compra
```

### Reportes

```http
GET /api/reportes/dashboard-completo  # Dashboard consolidado (cached)
```

## ⚙️ Configuración

### Base de Datos

El proyecto usa **H2** en memoria por defecto para desarrollo. Para producción, configura PostgreSQL:

```properties
# application-prod.properties
spring.datasource.url=jdbc:postgresql://host:5432/db_name
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASSWORD}
```

### Credenciales

Configura las credenciales de acceso en `application.properties` o variables de entorno.

## � Scripts Útiles

### Backend

```bash
./mvnw spring-boot:run     # Ejecutar aplicación
./mvnw clean compile       # Compilar
./mvnw test                # Ejecutar tests
./mvnw package             # Empaquetar JAR
```

### Frontend

```bash
cd src/main/resources/static/sofware-contable
npm run dev                # Modo desarrollo
npm run build              # Compilar para producción
npm run lint               # Lint del código
```

## 🐛 Solución de Problemas

### "mvnw: command not found"

```bash
chmod +x mvnw              # Dar permisos de ejecución
```

### Frontend no carga

- Verificar que estés en `src/main/resources/static/sofware-contable`
- Ejecutar `npm install` primero
- Comprobar que Node.js 18+ esté instalado

## � Licencia

Este proyecto está bajo la Licencia MIT.

## � Estado del Proyecto

✅ **Desarrollo completado** - Sistema funcional y en producción.

---

⭐ Si te ha sido útil, considera darle una estrella al proyecto.

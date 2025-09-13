-- Scripts SQL para crear todas las tablas del sistema contable Menchap
-- Ejecutar en PostgreSQL (Neon Database)

-- ============================================
-- LIMPIAR BASE DE DATOS (SOLO SI ES NECESARIO)
-- ============================================
DROP TABLE IF EXISTS compras CASCADE;
DROP TABLE IF EXISTS ventas CASCADE;
DROP TABLE IF EXISTS productos CASCADE;
DROP TABLE IF EXISTS usuarios CASCADE;

-- ============================================
-- CREAR FUNCIÓN PARA HASH DE CONTRASEÑAS
-- ============================================
CREATE OR REPLACE FUNCTION hash_password(password_text TEXT)
RETURNS TEXT AS $$
BEGIN
    -- Por simplicidad, usaremos MD5. En producción usar bcrypt
    RETURN MD5(password_text);
END;
$$ LANGUAGE plpgsql;

-- ============================================
-- TABLA DE USUARIOS
-- ============================================
CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE,
    nombre_completo VARCHAR(200),
    rol VARCHAR(20) DEFAULT 'USER',
    activo BOOLEAN NOT NULL DEFAULT true,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_ultimo_acceso TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- TABLA DE PRODUCTOS
-- ============================================
CREATE TABLE productos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    cantidad NUMERIC(10,2) NOT NULL DEFAULT 0,
    precio NUMERIC(10,2) NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================
-- TABLA DE VENTAS
-- ============================================
CREATE TABLE ventas (
    id BIGSERIAL PRIMARY KEY,
    producto_id BIGINT NOT NULL,
    nombre_producto VARCHAR(100) NOT NULL,
    cantidad NUMERIC(10,2) NOT NULL,
    precio_unitario NUMERIC(10,2) NOT NULL,
    precio_total NUMERIC(10,2) NOT NULL,
    costo_unitario NUMERIC(10,2),
    ganancia NUMERIC(10,2),
    cliente VARCHAR(100),
    metodo_pago VARCHAR(50),
    fecha_venta TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observaciones TEXT,
    usuario_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL
);

-- ============================================
-- TABLA DE COMPRAS
-- ============================================
CREATE TABLE compras (
    id BIGSERIAL PRIMARY KEY,
    producto_id BIGINT NOT NULL,
    nombre_producto VARCHAR(100) NOT NULL,
    cantidad NUMERIC(10,2) NOT NULL,
    costo_unitario NUMERIC(10,2) NOT NULL,
    costo_total NUMERIC(10,2) NOT NULL,
    proveedor VARCHAR(100),
    numero_factura VARCHAR(50),
    metodo_pago VARCHAR(50),
    fecha_compra TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observaciones TEXT,
    usuario_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL
);

-- ============================================
-- ÍNDICES PARA PERFORMANCE
-- ============================================
CREATE INDEX IF NOT EXISTS idx_usuarios_username ON usuarios(username);
CREATE INDEX IF NOT EXISTS idx_usuarios_email ON usuarios(email);
CREATE INDEX IF NOT EXISTS idx_productos_nombre ON productos(nombre);
CREATE INDEX IF NOT EXISTS idx_productos_tipo ON productos(tipo);
CREATE INDEX IF NOT EXISTS idx_ventas_fecha ON ventas(fecha_venta);
CREATE INDEX IF NOT EXISTS idx_ventas_producto ON ventas(producto_id);
CREATE INDEX IF NOT EXISTS idx_ventas_usuario ON ventas(usuario_id);
CREATE INDEX IF NOT EXISTS idx_compras_fecha ON compras(fecha_compra);
CREATE INDEX IF NOT EXISTS idx_compras_producto ON compras(producto_id);
CREATE INDEX IF NOT EXISTS idx_compras_usuario ON compras(usuario_id);

-- ============================================
-- TRIGGERS PARA ACTUALIZACIÓN AUTOMÁTICA
-- ============================================
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.fecha_actualizacion = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Aplicar triggers
CREATE TRIGGER update_usuarios_updated_at BEFORE UPDATE ON usuarios
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_productos_updated_at BEFORE UPDATE ON productos
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ============================================
-- INSERTAR USUARIO ADMINISTRADOR POR DEFECTO
-- ============================================
INSERT INTO usuarios (username, password, email, nombre_completo, rol, activo)
VALUES 
    ('admin', hash_password('admin123'), 'admin@menchap.com', 'Administrador Sistema', 'ADMIN', true),
    ('sn4yber', hash_password('snayber4589'), 'sn4yber@menchap.com', 'SN4YBER Developer', 'ADMIN', true)
ON CONFLICT (username) DO NOTHING;

-- ============================================
-- INSERTAR PRODUCTOS DE EJEMPLO
-- ============================================
INSERT INTO productos (nombre, tipo, cantidad, precio)
VALUES 
    ('Producto Demo 1', 'Categoría A', 50.00, 25.99),
    ('Producto Demo 2', 'Categoría B', 30.00, 45.50),
    ('Producto Demo 3', 'Categoría A', 100.00, 12.75)
ON CONFLICT DO NOTHING;

-- ============================================
-- FUNCIONES AUXILIARES PARA EL SISTEMA
-- ============================================

-- Función para verificar contraseña
CREATE OR REPLACE FUNCTION verify_password(input_password TEXT, stored_hash TEXT)
RETURNS BOOLEAN AS $$
BEGIN
    RETURN stored_hash = MD5(input_password);
END;
$$ LANGUAGE plpgsql;

-- Función para obtener estadísticas del dashboard
CREATE OR REPLACE FUNCTION get_dashboard_stats()
RETURNS JSON AS $$
DECLARE
    result JSON;
BEGIN
    SELECT json_build_object(
        'total_productos', (SELECT COUNT(*) FROM productos),
        'productos_en_stock', (SELECT COUNT(*) FROM productos WHERE cantidad > 0),
        'total_ventas_mes', (SELECT COALESCE(SUM(precio_total), 0) FROM ventas WHERE DATE_TRUNC('month', fecha_venta) = DATE_TRUNC('month', CURRENT_DATE)),
        'total_compras_mes', (SELECT COALESCE(SUM(costo_total), 0) FROM compras WHERE DATE_TRUNC('month', fecha_compra) = DATE_TRUNC('month', CURRENT_DATE)),
        'ganancias_mes', (SELECT COALESCE(SUM(ganancia), 0) FROM ventas WHERE DATE_TRUNC('month', fecha_venta) = DATE_TRUNC('month', CURRENT_DATE))
    ) INTO result;
    
    RETURN result;
END;
$$ LANGUAGE plpgsql;

-- ============================================
-- VERIFICACIONES FINALES
-- ============================================

-- Mostrar estructura de tablas
\d usuarios;
\d productos;
\d ventas;
\d compras;

-- Mostrar usuarios creados
SELECT id, username, email, nombre_completo, rol, activo, fecha_creacion
FROM usuarios
ORDER BY id;

-- Mostrar productos de ejemplo
SELECT id, nombre, tipo, cantidad, precio, fecha_creacion
FROM productos
ORDER BY id;

-- Verificar funciones
SELECT get_dashboard_stats();

COMMENT ON TABLE usuarios IS 'Tabla de usuarios del sistema con autenticación';
COMMENT ON TABLE productos IS 'Inventario de productos para el sistema contable';
COMMENT ON TABLE ventas IS 'Registro de ventas realizadas';
COMMENT ON TABLE compras IS 'Registro de compras a proveedores';

-- ============================================
-- SCRIPT COMPLETADO EXITOSAMENTE
-- ============================================
SELECT 'Base de datos creada exitosamente para Sistema Contable Menchap' as mensaje;

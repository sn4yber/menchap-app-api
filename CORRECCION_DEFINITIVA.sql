-- SCRIPT DE EMERGENCIA PARA CORREGIR LA BASE DE DATOS
-- Ejecutar este script COMPLETO en la consola de Neon PostgreSQL

-- 1. Verificar estructura actual
\d productos;

-- 2. LIMPIAR TABLA COMPLETAMENTE (SOLO SI ES NECESARIO)
-- DESCOMENTAR SOLO SI QUIERES EMPEZAR DE CERO
-- DROP TABLE IF EXISTS productos CASCADE;

-- 3. Crear tabla con estructura CORRECTA
CREATE TABLE IF NOT EXISTS productos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    cantidad NUMERIC(10,2) NOT NULL,
    precio_unitario NUMERIC(10,2) NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. Si la tabla ya existe, agregar columnas faltantes
DO $$
BEGIN
    -- Agregar precio_unitario si no existe
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'productos' AND column_name = 'precio_unitario'
    ) THEN
        ALTER TABLE productos ADD COLUMN precio_unitario NUMERIC(10,2);
    END IF;

    -- Agregar fecha_creacion si no existe
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'productos' AND column_name = 'fecha_creacion'
    ) THEN
        ALTER TABLE productos ADD COLUMN fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
    END IF;

    -- Agregar fecha_actualizacion si no existe
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'productos' AND column_name = 'fecha_actualizacion'
    ) THEN
        ALTER TABLE productos ADD COLUMN fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
    END IF;
END $$;

-- 5. Corregir datos existentes
UPDATE productos
SET precio_unitario = COALESCE(precio_unitario, 0.00)
WHERE precio_unitario IS NULL;

-- 6. Eliminar columna 'precio' si existe (causa del problema)
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'productos' AND column_name = 'precio'
    ) THEN
        -- Copiar datos de precio a precio_unitario antes de eliminar
        UPDATE productos SET precio_unitario = COALESCE(precio, precio_unitario, 0.00);
        -- Eliminar columna problemática
        ALTER TABLE productos DROP COLUMN precio;
    END IF;
END $$;

-- 7. Hacer las columnas NOT NULL
ALTER TABLE productos ALTER COLUMN precio_unitario SET NOT NULL;
ALTER TABLE productos ALTER COLUMN cantidad SET NOT NULL;
ALTER TABLE productos ALTER COLUMN nombre SET NOT NULL;
ALTER TABLE productos ALTER COLUMN tipo SET NOT NULL;

-- 8. Crear índices para mejor rendimiento
CREATE INDEX IF NOT EXISTS idx_productos_nombre ON productos(nombre);
CREATE INDEX IF NOT EXISTS idx_productos_tipo ON productos(tipo);

-- 9. Verificar la estructura final
\d productos;

-- 10. Mostrar datos existentes
SELECT id, nombre, tipo, cantidad, precio_unitario, fecha_creacion
FROM productos
ORDER BY id;

-- 11. Insertar producto de prueba para verificar que funciona
INSERT INTO productos (nombre, tipo, cantidad, precio_unitario)
VALUES ('Producto de Prueba', 'Categoria', 10.00, 25.50)
ON CONFLICT DO NOTHING;

-- 12. Verificar que el insert funcionó
SELECT * FROM productos WHERE nombre = 'Producto de Prueba';

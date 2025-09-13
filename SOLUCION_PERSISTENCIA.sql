-- Script para solucionar definitivamente el problema de persistencia
-- Ejecutar este script en la consola de Neon PostgreSQL

-- 1. Verificar estructura actual de la tabla productos
\d productos;

-- 2. Crear tabla productos con la estructura correcta si no existe
CREATE TABLE IF NOT EXISTS productos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    cantidad NUMERIC(10,2) NOT NULL,
    precio_unitario NUMERIC(10,2) NOT NULL
);

-- 3. Si ya existe la tabla pero le falta la columna precio_unitario, agregarla
DO $$
BEGIN
    -- Verificar si la columna precio_unitario existe
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'productos'
        AND column_name = 'precio_unitario'
    ) THEN
        -- Agregar la columna precio_unitario
        ALTER TABLE productos ADD COLUMN precio_unitario NUMERIC(10,2);

        -- Si existe una columna 'precio', copiar los valores
        IF EXISTS (
            SELECT 1
            FROM information_schema.columns
            WHERE table_name = 'productos'
            AND column_name = 'precio'
        ) THEN
            UPDATE productos SET precio_unitario = precio WHERE precio_unitario IS NULL;
        ELSE
            -- Si no existe columna precio, establecer valor por defecto
            UPDATE productos SET precio_unitario = 0.00 WHERE precio_unitario IS NULL;
        END IF;

        -- Hacer la columna NOT NULL
        ALTER TABLE productos ALTER COLUMN precio_unitario SET NOT NULL;
    END IF;
END $$;

-- 4. Verificar la estructura final
\d productos;

-- 5. Mostrar todos los productos existentes
SELECT * FROM productos;

-- 6. Verificar que tenemos usuarios para login
SELECT * FROM usuarios;

-- Script para corregir la estructura de la tabla productos en Neon
-- Ejecuta este script en la consola SQL de Neon

-- 1. Verificar la estructura actual de la tabla
\d productos;

-- 2. Agregar la columna precio_unitario si no existe
ALTER TABLE productos ADD COLUMN IF NOT EXISTS precio_unitario NUMERIC(10,2);

-- 3. Si tienes datos existentes con una columna "precio", copia los valores
-- UPDATE productos SET precio_unitario = precio WHERE precio_unitario IS NULL;

-- 4. Si no tienes datos importantes, puedes establecer un valor por defecto
UPDATE productos SET precio_unitario = 0.00 WHERE precio_unitario IS NULL;

-- 5. Hacer la columna NOT NULL
ALTER TABLE productos ALTER COLUMN precio_unitario SET NOT NULL;

-- 6. Verificar la estructura final
\d productos;

-- 7. Mostrar los datos actuales
SELECT * FROM productos;

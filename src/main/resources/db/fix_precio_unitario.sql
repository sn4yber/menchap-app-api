-- 1. Agregar la columna permitiendo NULL temporalmente
ALTER TABLE productos ADD COLUMN IF NOT EXISTS precio_unitario NUMERIC(10,2);

-- 2. Actualizar filas existentes (ajusta el valor según tu lógica de negocio)
UPDATE productos SET precio_unitario = 0 WHERE precio_unitario IS NULL;

-- 3. Cambiar la columna a NOT NULL
ALTER TABLE productos ALTER COLUMN precio_unitario SET NOT NULL;


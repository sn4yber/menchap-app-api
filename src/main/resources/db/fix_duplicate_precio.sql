-- Script para eliminar el campo precio redundante de la tabla productos
-- Solo se mantiene precio_unitario que es el que usa el modelo Java

-- Verificar si existe la columna precio
-- Si existe, eliminarla
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'productos' 
        AND column_name = 'precio'
        AND table_schema = 'public'
    ) THEN
        ALTER TABLE productos DROP COLUMN precio;
        RAISE NOTICE 'Campo precio eliminado exitosamente';
    ELSE
        RAISE NOTICE 'Campo precio no existe, no es necesario eliminarlo';
    END IF;
END $$;

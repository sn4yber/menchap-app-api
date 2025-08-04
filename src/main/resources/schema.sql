-- Insertar usuarios solo si no existen
INSERT INTO usuarios (username, password) 
SELECT 'sn4', 'snayber4589'
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE username = 'sn4');

INSERT INTO usuarios (username, password) 
SELECT 'shelsin', '4dejulio'
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE username = 'shelsin');

INSERT INTO usuarios (username, password) 
SELECT 'adalciry', '45577624'
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE username = 'adalciry');

-- Mostrar usuarios
SELECT * FROM usuarios;

-- Eliminar tabla productos si existe
DROP TABLE IF EXISTS productos;

-- Crear tabla productos
CREATE TABLE productos (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    cantidad INTEGER NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    precio_total DECIMAL(10,2) NOT NULL
	
	
);
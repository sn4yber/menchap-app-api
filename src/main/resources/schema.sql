-- Crear tabla de productos
CREATE TABLE IF NOT EXISTS productos (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    tipo VARCHAR(100),
    cantidad DECIMAL(10,2) DEFAULT 0,
    precio_unitario DECIMAL(10,2) DEFAULT 0,
    precio_total DECIMAL(10,2) DEFAULT 0
);

-- Crear tabla de usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL
);

-- Insertar datos de ejemplo para usuarios
INSERT INTO usuarios (username, password) VALUES 
    ('admin', 'admin123'),
    ('user', 'user123')
ON CONFLICT (username) DO NOTHING;

-- Insertar datos de ejemplo para productos
INSERT INTO productos (nombre, tipo, cantidad, precio_unitario, precio_total) VALUES 
    ('Laptop HP', 'Electrónicos', 5.00, 1200.00, 6000.00),
    ('Mouse Inalámbrico', 'Accesorios', 20.00, 25.00, 500.00),
    ('Teclado Mecánico', 'Accesorios', 10.00, 150.00, 1500.00)
ON CONFLICT (id) DO NOTHING; 
-- Datos iniciales para H2 Database (desarrollo)

-- Insertar usuarios de prueba
INSERT INTO usuarios (username, password, email, nombre_completo, rol, activo, fecha_creacion, fecha_actualizacion) VALUES 
('admin', 'c93ccd78b2076528346216b3b2f701e6', 'admin@menchap.com', 'Administrador Sistema', 'ADMIN', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('sn4yber', '8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'sn4yber@menchap.com', 'SN4YBER Developer', 'ADMIN', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('demo', '5d41402abc4b2a76b9719d911017c592', 'demo@menchap.com', 'Usuario Demo', 'USER', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insertar productos de ejemplo
INSERT INTO productos (nombre, tipo, cantidad, precio) VALUES 
('Laptop Dell XPS 13', 'Electrónicos', 15.00, 1299.99),
('Mouse Logitech MX Master', 'Accesorios', 50.00, 89.99),
('Teclado Mecánico RGB', 'Accesorios', 25.00, 149.99),
('Monitor 4K Samsung', 'Electrónicos', 8.00, 599.99),
('Auriculares Sony WH-1000XM4', 'Audio', 20.00, 349.99),
('Tablet iPad Air', 'Electrónicos', 12.00, 649.99),
('Cable USB-C', 'Cables', 100.00, 19.99),
('Cargador Portátil', 'Accesorios', 30.00, 39.99),
('Webcam HD 1080p', 'Electrónicos', 18.00, 79.99),
('Disco SSD 1TB', 'Almacenamiento', 22.00, 119.99);

-- Insertar algunas ventas de ejemplo
INSERT INTO ventas (producto_id, nombre_producto, cantidad, precio_unitario, precio_total, costo_unitario, ganancia, cliente, metodo_pago, fecha_venta) VALUES 
(1, 'Laptop Dell XPS 13', 2, 1299.99, 2599.98, 1000.00, 599.98, 'Juan Pérez', 'Tarjeta', CURRENT_TIMESTAMP),
(2, 'Mouse Logitech MX Master', 5, 89.99, 449.95, 60.00, 149.95, 'María García', 'Efectivo', CURRENT_TIMESTAMP),
(3, 'Teclado Mecánico RGB', 1, 149.99, 149.99, 100.00, 49.99, 'Carlos López', 'Transferencia', CURRENT_TIMESTAMP);

-- Insertar algunas compras de ejemplo  
INSERT INTO compras (producto_id, nombre_producto, cantidad, costo_unitario, costo_total, proveedor, numero_factura, metodo_pago, fecha_compra) VALUES 
(1, 'Laptop Dell XPS 13', 10, 1000.00, 10000.00, 'Dell Distribuidor', 'DELL-2024-001', 'Transferencia', CURRENT_TIMESTAMP),
(2, 'Mouse Logitech MX Master', 25, 60.00, 1500.00, 'Logitech SA', 'LOG-2024-002', 'Cheque', CURRENT_TIMESTAMP),
(5, 'Auriculares Sony WH-1000XM4', 15, 250.00, 3750.00, 'Sony Electronics', 'SONY-2024-003', 'Transferencia', CURRENT_TIMESTAMP);

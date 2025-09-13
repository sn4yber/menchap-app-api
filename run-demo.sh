#!/bin/bash

# Script para ejecutar el sistema contable con H2 (demo mode)
echo "🚀 Iniciando Sistema Contable MainChap (Modo Demo)..."
echo "💾 Usando base de datos en memoria H2..."

# Configuración para H2 Database
export SPRING_DATASOURCE_URL="jdbc:h2:mem:testdb"
export SPRING_DATASOURCE_USERNAME="sa"
export SPRING_DATASOURCE_PASSWORD="password"
export SPRING_DATASOURCE_DRIVER_CLASS_NAME="org.h2.Driver"

# Habilitar consola H2
export SPRING_H2_CONSOLE_ENABLED=true

# Configuración adicional
export SERVER_PORT=8080
export SPRING_JPA_HIBERNATE_DDL_AUTO=create-drop
export SPRING_JPA_SHOW_SQL=false
export SPRING_SQL_INIT_MODE=always

echo "✅ Variables de entorno configuradas para modo demo"
echo "🌐 La aplicación estará disponible en: http://localhost:8080"
echo "🔧 Consola H2 disponible en: http://localhost:8080/h2-console"
echo ""

# Ejecutar la aplicación
./mvnw spring-boot:run

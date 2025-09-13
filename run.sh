#!/bin/bash

# Script para ejecutar MainChap Sistema Contable
echo "🚀 Iniciando MainChap Sistema Contable..."
echo ""

# Detectar si hay parámetros
if [ "$1" = "prod" ] || [ "$1" = "production" ]; then
    echo "🌍 Modo: PRODUCCIÓN (con base de datos Neon)"
    echo "📊 Conectando a base de datos Neon..."
    
    export SPRING_PROFILES_ACTIVE=prod
    export SPRING_DATASOURCE_URL="jdbc:postgresql://ep-aged-union-adsr2xcz-pooler.us-east-1.postgres.neon.tech/neondb"
    export SPRING_DATASOURCE_USERNAME="neondb_owner"
    export SPRING_DATASOURCE_PASSWORD="npg_lFrvG8wOgv0fzFLCzLMbCTrS0eLNKOIv"
else
    echo "💻 Modo: DESARROLLO (con base de datos H2 en memoria)"
    echo "📊 Usando base de datos H2..."
    export SPRING_PROFILES_ACTIVE=dev
fi

echo "✅ Variables de entorno configuradas"
echo "🌐 La aplicación estará disponible en: http://localhost:8080"
if [ "$SPRING_PROFILES_ACTIVE" = "dev" ]; then
    echo "🔧 Consola H2 disponible en: http://localhost:8080/h2-console"
    echo "   - JDBC URL: jdbc:h2:mem:mainchap_db"
    echo "   - Usuario: sa"
    echo "   - Contraseña: (vacía)"
fi
echo ""
echo "👤 Usuarios disponibles:"
echo "   - Usuario: sn4 | Contraseña: snayber4589#"
echo ""

# Configurar Java si no está disponible
export JAVA_HOME=/app/extra/jbr
export PATH=$JAVA_HOME/bin:$PATH

echo "⏳ Iniciando aplicación..."
echo ""

# Ejecutar la aplicación Spring Boot
./mvnw spring-boot:run

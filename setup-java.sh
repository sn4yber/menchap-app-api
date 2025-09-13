#!/bin/bash
# Script para configurar Java en el entorno
export JAVA_HOME=/app/extra/jbr
export PATH=$JAVA_HOME/bin:$PATH

echo "✅ Java configurado correctamente:"
echo "JAVA_HOME: $JAVA_HOME"
java -version

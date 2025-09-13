#!/bin/bash
# Script para resolver problemas de IDE con Java y Spring Boot

echo "🔧 Resolviendo problemas de IDE con Java y Spring Boot..."

# Configurar Java correctamente
export JAVA_HOME=/app/extra/jbr
export PATH=$JAVA_HOME/bin:$PATH

echo "☕ Configurando Java..."
echo "JAVA_HOME: $JAVA_HOME"
java -version

# 1. Limpiar completamente el proyecto
echo "📁 Limpiando proyecto..."
./mvnw clean

# 2. Eliminar carpetas de cache del IDE
echo "🗑️ Eliminando cache del IDE..."
rm -rf .idea/
rm -rf .vscode/
rm -rf .settings/
rm -rf .project
rm -rf .classpath
rm -rf target/
rm -rf *.iml
rm -rf *.ipr
rm -rf *.iws

# 3. Reinstalar dependencias
echo "📦 Reinstalando dependencias..."
./mvnw dependency:resolve dependency:sources

# 4. Compilar con la versión correcta de Java
echo "🔨 Compilando con Java 21..."
./mvnw compile

# 5. Crear archivos de configuración del IDE
echo "⚙️ Creando configuración del IDE..."

# Crear archivo de configuración para IntelliJ IDEA
cat > .idea/misc.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="ProjectRootManager" version="2" languageLevel="JDK_21" default="true" project-jdk-name="21" project-jdk-type="JavaSDK">
    <output url="file://$PROJECT_DIR$/target/classes" />
  </component>
  <component name="ProjectType">
    <option name="id" value="jpab" />
  </component>
</project>
EOF

# Crear directorio .idea si no existe
mkdir -p .idea

# Configuración del compilador
cat > .idea/compiler.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="CompilerConfiguration">
    <annotationProcessing>
      <profile name="Maven default annotation processors profile" enabled="true">
        <sourceOutputDir name="target/generated-sources/annotations" />
        <sourceTestOutputDir name="target/generated-test-sources/test-annotations" />
        <outputRelativeToContentRoot value="true" />
        <module name="api_jdbc" />
      </profile>
    </annotationProcessing>
    <bytecodeTargetLevel target="21">
      <module name="api_jdbc" target="21" />
    </bytecodeTargetLevel>
  </component>
</project>
EOF

# Configuración del módulo
cat > .idea/modules.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<project version="4">
  <component name="ProjectModuleManager">
    <modules>
      <module fileurl="file://$PROJECT_DIR$/api_jdbc.iml" filepath="$PROJECT_DIR$/api_jdbc.iml" />
    </modules>
  </component>
</project>
EOF

# Crear archivo de módulo
cat > api_jdbc.iml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<module type="JAVA_MODULE" version="4">
  <component name="NewModuleRootManager" LANGUAGE_LEVEL="JDK_21">
    <output url="file://$MODULE_DIR$/target/classes" />
    <output-test url="file://$MODULE_DIR$/target/test-classes" />
    <content url="file://$MODULE_DIR$">
      <sourceFolder url="file://$MODULE_DIR$/src/main/java" isTestSource="false" />
      <sourceFolder url="file://$MODULE_DIR$/src/main/resources" type="java-resource" />
      <sourceFolder url="file://$MODULE_DIR$/src/test/java" isTestSource="true" />
      <sourceFolder url="file://$MODULE_DIR$/target/generated-sources/annotations" isTestSource="false" generated="true" />
      <excludeFolder url="file://$MODULE_DIR$/target" />
    </content>
    <orderEntry type="inheritedJdk" />
    <orderEntry type="sourceFolder" forTests="false" />
  </component>
</module>
EOF

echo "✅ Configuración del IDE completada!"
echo ""
echo "🎯 Pasos adicionales para IntelliJ IDEA:"
echo "1. Reinicia IntelliJ IDEA"
echo "2. Ve a File → Project Structure → Project Settings → Project"
echo "3. Asegúrate de que Project SDK esté configurado en Java 21"
echo "4. En Project language level, selecciona '21 - Sealed types, always-strict floating-point semantics'"
echo "5. Ve a File → Settings → Build → Compiler → Java Compiler"
echo "6. Asegúrate de que Project bytecode version esté en '21'"
echo "7. Haz clic en File → Invalidate Caches and Restart"
echo ""
echo "🚀 Tu proyecto debería compilar correctamente ahora!"

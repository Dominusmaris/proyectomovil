# Dockerfile para Backend Spring Boot - Proyecto Finanzas DuocUC

# Usar imagen base de Java 17 (versión actualizada)
FROM amazoncorretto:17-alpine

# Información del proyecto
LABEL maintainer="Proyecto DuocUC - DSY1105"
LABEL description="Backend Spring Boot para App de Finanzas"

# Directorio de trabajo
WORKDIR /app

# Copiar archivos de Maven
COPY pom.xml ./
COPY mvnw ./
COPY .mvn .mvn

# Dar permisos de ejecución a mvnw
RUN chmod +x mvnw

# Descargar dependencias (para aprovechar cache de Docker)
RUN ./mvnw dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Compilar la aplicación
RUN ./mvnw clean package -DskipTests

# Exponer puerto 8080
EXPOSE 8080

# Variables de entorno por defecto
ENV SPRING_PROFILES_ACTIVE=prod

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "target/backend-finanzas.jar"]
# Dockerfile para Backend Spring Boot - Proyecto Finanzas DuocUC

# Stage 1: Build con Maven
FROM amazoncorretto:17-alpine as build

# Instalar Maven
RUN apk add --no-cache maven

# Directorio de trabajo
WORKDIR /app

# Copiar archivos de configuración Maven
COPY pom.xml .

# Descargar dependencias
RUN mvn dependency:go-offline -B

# Copiar código fuente
COPY src ./src

# Compilar la aplicación
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM amazoncorretto:17-alpine

# Información del proyecto
LABEL maintainer="Proyecto DuocUC - DSY1105"
LABEL description="Backend Spring Boot para App de Finanzas"

# Directorio de trabajo
WORKDIR /app

# Copiar JAR compilado desde stage build
COPY --from=build /app/target/backend-finanzas.jar app.jar

# Exponer puerto 8080
EXPOSE 8080

# Variables de entorno por defecto
ENV SPRING_PROFILES_ACTIVE=prod

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "app.jar"]
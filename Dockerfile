# Imagen para desplegar en Render, Fly.io, Railway, etc. (no en Vercel como app Java).
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /src
COPY . .
RUN apk add --no-cache bash \
	&& chmod +x mvnw \
	&& ./mvnw -B -DskipTests package

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -g 1001 -S app && adduser -u 1001 -S app -G app
COPY --from=build /src/target/deportivo-futbol-1.0.0.jar app.jar
USER app
ENV SPRING_PROFILES_ACTIVE=neon
# Render inyecta PORT; Spring usa server.port=${PORT:9090}
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "app.jar"]

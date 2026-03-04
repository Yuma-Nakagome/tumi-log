# ビルド用の環境（Java 21）
FROM maven:3.9-eclipse-temurin-21 AS build
COPY . .
RUN mvn clean package -DskipTests

# 実行用の軽量環境
FROM eclipse-temurin:21-jre
COPY --from=build /target/*.jar app.jar

# ポート8080で起動（Renderのデフォルト）
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
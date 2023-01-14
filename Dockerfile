FROM eclipse-temurin:17-jdk

COPY rest/target/rest-1.0-SNAPSHOT.jar /app.jar

CMD java -jar app.jar
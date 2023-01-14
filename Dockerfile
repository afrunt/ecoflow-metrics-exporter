FROM eclipse-temurin:17.0.5_8-jdk

COPY rest/target/rest-1.0-SNAPSHOT.jar /app.jar

CMD java -jar app.jar
## Requirements
Docker, Java 11

## How to run tests
### All tests (integration tests + unit tests)
```
./mvnw clean verify
```

### Unit tests
```
./mvnw clean test
```

## How to build application
```
./mvnw clean install
```

##How to run application locally
```
docker run -d -p 27017:27017 mongo
```
```
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

##Swagger
```
http://localhost:8080/swagger-ui/
```

##Production
1. Build application
2. Copy home-assignment-1.0.0-SNAPSHOT.jar to server (Linux)
3. Create file application-prod.yml in dir where home-assignment-1.0.0-SNAPSHOT.jar is
4. Fill connection parameters to mongodb in application-prod.yml file. More details you can find on https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html#application-properties.data.spring.data.mongodb.authentication-database
Example application-prod.yml
```
spring:
  data:
    mongodb:
      host: localhost
      port: 27017
      database: drugs
```
5. Run application
```
java -jar -Dspring.profiles.active=prod home-assignment-1.0.0-SNAPSHOT.jar
```
It should be run as a service. More details you can find on https://dzone.com/articles/run-your-java-application-as-a-service-on-ubuntu
6. Application logs are located in logs dir. Log configuration is in logback-spring.xml file
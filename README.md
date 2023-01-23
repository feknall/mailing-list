# mailing-list
# Maling List Project

## Tech Stack
* Language: Kotlin
* Build Tool: Gradle
* Http Server: Http4k
* Database Connection: JDBC 
* ORM: Exposed
* Databases: PostgreSQL, H2DB for Testing
* Test: Junit and Mockito
* Documentation: Swagger, OpenAPI 3. 


Note: OpenAPI 3 does not support showing request body for GET requests. It is solved in OpenAPI 3.1, but it seems that http4k-contract still uses OpenAPI 3.


## How to Run
You need to have Open JDK 18. Make sure to use the correct path in `gradle.properties` file for `org.gradle.java.home`. Then, just run:
```
./gradlew build
```
And:
```
./gradlew run
```

Keep the terminal open after running `./gradlew run`. Server is up and running. Open a browser and go to `localhost:8080`.



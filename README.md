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
The default database is PostgreSQL. However, for running the project you have two choices. Either running an instance of PostgreSQL or changing `application.properties` file. Therefore, either run the below command to run an instance of PostgreSQL on Docker:
```
docker run -e POSTGRES_USERNAME=postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 -v /var/run/postgresql:/var/run/postgresql -d --name postgres postgres
```
Or comment first few lines of `application.properties` to stop using PostgreSQL, and uncomment last few lines to replace it with H2DB. 

Note: In any case, tests are running on H2DB.

You need to have Open JDK 18. Make sure to use the correct path in `gradle.properties` file for `org.gradle.java.home`. Then, just run:
```
./gradlew build
```
And:
```
./gradlew run
```

Keep the terminal open after running `./gradlew run`. Server is up and running. Open a browser and go to `localhost:8080`. RestAPIs are shown using Swagger.

## Samples
* Get list of users
```
GET localhost:8080/user
```
Response Body:
```
[
    {
        "userId": 1,
        "name": "hom2",
        "emailList": [
            {
                "emailId": 3,
                "address": "xwx@gmail.com"
            },
            {
                "emailId": 4,
                "address": "xwx@gmail.com"
            }
        ]
    },
    {
        "userId": 2,
        "name": "Jack",
        "emailList": []
    },
    {
        "userId": 3,
        "name": "Jill",
        "emailList": []
    },
    {
        "userId": 4,
        "name": "Jack",
        "emailList": []
    },
    {
        "userId": 5,
        "name": "Jill",
        "emailList": []
    },
    {
        "userId": 6,
        "name": "Tom",
        "emailList": []
    },
    {
        "userId": 7,
        "name": "hom23",
        "emailList": []
    }
]
```
* Add a new user
```
POST localhost:8080/user
{
    "name": "Maverik"
}
```
Response:
```
{
    "userId": 8,
    "name": "Maverik",
    "emailList": null
}
```
* Get list of emails for an specific user
```
GET localhost:8080/email
{
    "userId": 1
}
```
Response:
```
[
    {
        "emailId": 3,
        "address": "xwx@gmail.com"
    },
    {
        "emailId": 4,
        "address": "xwx@gmail.com"
    }
]
```
* Add a new email for a specific user
```
POST localhost:8080/email
{
    "userId": 1,
    "emailAddress": "xwx@gmail.com"
}
```
Response:
```
{
    "userId": 1,
    "emailId": 5,
    "emailAddress": "xwx@gmail.com"
}
```
* Delete an email of a specific user
```
DELETE localhost:8080/email
```


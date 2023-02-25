# Spring Boot App with JWT
This project is configured with H2 in memory DataBase, all the data will be lost when the application is stopped.
## Description
A SpringBoot App that expose an API with public and private endpoint, it is intended for educational purpose and is not ready to be used in a production environment.
### Endpoints available
There are 4 endpoints available

| HTTP Method | Endpoint         | Description                       | Type    |
|-------------|------------------|-----------------------------------|---------|
| POST        | /api/auth/login  | Validate a user and password      | public  |
| POST        | /api/auth/signup | Create a new user                 | public  |
| GET         | /api/test/all    | Get data available for all public | public  |
| GET         | /api/test/user   | Get data for a user authenticated | private |

## Run the App
1. Clone the repo in your local
2. Run the following command in your terminal from the root of the cloned project `./gradlew bootRun`
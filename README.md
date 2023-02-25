# Spring Boot API server with JWT Authentication
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

## Run the Server
1. Clone the repo in your local
2. Run `./gradlew bootRun` in your terminal from the root path of the cloned project
3. Now [http://localhost:8080](http://localhost:8080) is exposed to receive the request

> **Acknowledgement**: Code is based on this [Coders Campus playlist](https://www.youtube.com/watch?v=xuOuzLWQy3A&list=PL2OrQJM8zmZ2-O_rM2Ju9zYMbY8Ta-8I4)

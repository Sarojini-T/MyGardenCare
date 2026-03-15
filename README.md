# MyGardenCare API

A Spring Boot REST API for gardeners to manage their plants by getting personalized plant recommendations using their input and plant metadata from [Permapeople](https://permapeople.org/) API.

[Swagger UI Documentation](http://mygardencare-env.eba-c3d4mat3.us-east-2.elasticbeanstalk.com/swagger-ui/index.html)

## Tech Stack
* **Framework:** Java 21, Spring Boot, Spring Security
*  **Database:** PostgreSQL (hosted on Neon DB)
*  **Migrations:** Flyway
*  **Testing:** JUnit 5, Mockito
*  **Deployment & CI/CD:** Docker, AWS Elastic Beanstalk, GitHub Actions


## Core Features
* Secure registration and login using BCrypt password hashing and JWT
* CRUD operations for user plant collections and metadata
* Generates personalized care schedules based on user inputs and external botanical API data
* GitHub Actions pipeline triggers automated testing on every push to maintain code quality

## Local Setup

### Prerequisites
* Java 21
* Docker & Docker Compose
* Maven

### Installation
1. Clone the repository
2. Configure environment variables. Create a .env file in the root directory and copy the variables defined in .env.example
3. Run the application using Docker Compose:
   ```bash
   docker-compose up --build
   ```
5. Access the API locally at: http://localhost:8080/swagger-ui/index.html

## Attribution & License
Plant data and metadata are provided by [Permapeople.org.](https://permapeople.org/)
In accordance with their terms of use, any work built upon this dataset is distributed under the same [Creative Commons Attribution-ShareAlike 4.0 International (CC BY-SA 4.0)](https://creativecommons.org/licenses/by-sa/4.0/) license.
   

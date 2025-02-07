# Warehouse Management System

## Overview
This is a simple warehouse management system built with Spring Boot and MongoDB. It allows users to manage inventory and products efficiently.

## Running the Application

### Using Docker Compose
If `docker-compose` is installed, you can run the application with:
```sh
docker-compose up -d
```
To stop the application, run:
```sh
docker-compose down
```

### Running Locally
To run the application locally, you need to install and start MongoDB. You can start a MongoDB container using Docker:
```sh
docker run --name mongodb-container -d -p 27017:27017 mongo
```
Then, start the Spring Boot application:
```sh
./mvnw spring-boot:run
```

## API Documentation
Once the application is running, the OpenAPI (Swagger UI) documentation will be available at:
[http://localhost:8080/swagger-ui/index.html#/](http://localhost:8080/swagger-ui/index.html#/)

## Testing
- **Postman Collection:** A Postman collection and related test files are included in the `docs/` folder.
- **Unit and Integration Tests:**
  - Unit tests are included.
  - Integration tests require port `27017` to be free.

## Docker Image
The application is available as a Docker image on Docker Hub:
```sh
docker pull ebrazi/warehouse
```

## Future Enhancements
- Add an endpoint to sell multiple products at once instead of one-by-one.
- Implement pagination for fetching all products.
- Set up database authentication (username/password) for a real-world scenario and store credentials in a secure cloud secret service.

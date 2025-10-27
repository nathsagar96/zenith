# Zenith Blog Application

## Project Overview

Zenith is a comprehensive blog application built with Spring Boot that provides a robust platform for managing blog content, users, and interactions. The application follows modern software architecture principles and leverages various technologies to deliver a scalable and maintainable solution.

### Key Features

- **User Management**: Registration, authentication, and role-based access control
- **Content Management**: Create, update, and manage blog posts with categories and tags
- **Comment System**: Moderated commenting with approval workflow
- **API Documentation**: Comprehensive OpenAPI documentation
- **Security**: JWT-based authentication and authorization
- **Data Validation**: Comprehensive input validation
- **Containerization**: Docker Compose support for easy deployment

## Technologies Used

- **Spring Boot 3.5.7**: Core framework for building the application
- **Spring Data JPA**: ORM for database interactions
- **PostgreSQL**: Relational database for data persistence
- **JWT (jjwt)**: JSON Web Token for authentication
- **MapStruct**: Object mapping between entities and DTOs
- **Lombok**: Boilerplate code reduction
- **SpringDoc OpenAPI**: API documentation generation
- **Docker Compose**: Container orchestration for development and deployment

## Project Structure

The project follows a clean architecture with clear separation of concerns:

```file
src/main/java/com/zenith/
├── controllers/          # REST API controllers
├── dtos/                 # Data Transfer Objects
│   ├── requests/         # Request DTOs
│   └── responses/        # Response DTOs
├── entities/             # JPA entities
├── enums/                # Enumerations
├── exceptions/           # Custom exceptions
├── mappers/              # MapStruct mappers
├── repositories/         # Spring Data repositories
├── security/             # Security configuration
├── services/             # Business logic services
└── utils/                # Utility classes
```

## Setup Instructions

### Prerequisites

- Java 25
- Maven 3.9+
- Docker

### Installation

1. Clone the repository:

    ```bash
    git clone https://github.com/nathsagar96/zenith.git
    cd zenith
    ```

2. Build the project:

    ```bash
    ./mvnw clean install
    ```

3. Generate and set the JWT secret key:

    ```bash
    # Generate a secure random key (you can use openssl or any other method)
    JWT_SECRET=$(openssl rand -base64 32)

    # Set it as an environment variable
    export APP_JWT_SECRET=$JWT_SECRET

    # Alternatively, update the application.yml file:
    # Edit src/main/resources/application.yml and set:
    # app.jwt.secret: your-generated-secret-key-here
    ```

4. Run the application:

    ```bash
    ./mvnw spring-boot:run
    ```

## Usage Examples

### API Endpoints

The application provides comprehensive REST APIs for all operations:

#### Authentication

- **Register a new user**

  ```http
  POST /api/v1/auth/register
  Content-Type: application/json

  {
    "username": "john_doe",
    "email": "john@example.com",
    "password": "password123"
  }
  ```

- **User login**

  ```http
  POST /api/v1/auth/login
  Content-Type: application/json

  {
    "email": "john@example.com",
    "password": "password123"
  }
  ```

#### Categories

- **Get all categories**

  ```http
  GET /api/v1/categories?page=0&size=10&sortBy=createdAt&direction=desc
  ```

- **Create a new category (Admin only)**

  ```http
  POST /api/v1/categories
  Content-Type: application/json
  Authorization: Bearer {token}

  {
    "name": "Technology"
  }
  ```

#### Posts

- **Get all posts (Admin only)**

  ```http
  GET /api/v1/posts?page=0&size=10&sortBy=createdAt&direction=desc
  Authorization: Bearer {token}
  ```

- **Get all public posts**

  ```http
  GET /api/v1/posts/public?page=0&size=10&sortBy=createdAt&direction=desc
  ```

- **Create a new post**

  ```http
  POST /api/v1/posts
  Content-Type: application/json
  Authorization: Bearer {token}

  {
    "title": "My First Post",
    "content": "This is the content of my post",
    "categoryId": 1,
    "tagIds": [1, 2]
  }
  ```

#### Comments

- **Get approved comments for a post**

  ```http
  GET /api/v1/comments/post/{postId}?page=0&size=10
  ```

- **Create a new comment**

  ```http
  POST /api/v1/comments
  Content-Type: application/json
  Authorization: Bearer {token}

  {
    "content": "This is a great post!",
    "postId": 1
  }
  ```

## API Documentation

The application includes comprehensive API documentation using SpringDoc OpenAPI. After starting the application, you can access the documentation at:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Code Formatting

This project uses the Spotless plugin to ensure consistent code formatting across all Java files.

- Uses Palantir Java Format for consistent code style
- Automatically formats all Java files in the project
- Configuration in `pom.xml` under the Spotless plugin section

### Running the Formatter

To format all code in the project, run:

```bash
./mvnw spotless:apply
```

This will automatically format all Java files according to the configured rules.

### Troubleshooting

- If formatting fails, check your Maven configuration
- Ensure you have the Spotless plugin properly configured in your `pom.xml`
- Verify that all required dependencies are available

## Running Tests

### Test Setup

The project uses JUnit 5 for testing and Testcontainers for database integration tests. All tests are located in the `src/test/java/com/zenith/` directory.

### Running All Tests

To run all tests, use the following Maven command:

```bash
./mvnw test
```

### Running Specific Tests

To run tests for a specific module (e.g., controllers), use:

```bash
./mvnw test -Dtest=AuthControllerTest
```

### Test Structure

- **Unit Tests**: Test individual components in isolation
- **Controller Tests**: Test REST API endpoints using MockMvc
- **Repository Tests**: Test database operations using Testcontainers

### Expected Outcomes

- All tests should pass with green status
- Test reports are generated in `target/surefire-reports/`

### Troubleshooting Tests

- Ensure Docker is running for Testcontainers
- Check database connection settings
- Verify all required environment variables are set

## Building Docker Image

Spring Boot 3.5 provides default support for building Docker images using buildpacks. This allows you to easily containerize your application without writing a Dockerfile.

### Using Maven

To build a Docker image with Maven, run the following command:

```bash
./mvnw spring-boot:build-image
```

This command will:

1. Use the Spring Boot buildpack to create an optimized Docker image
2. Automatically handle all dependencies and configuration
3. Produce a production-ready container image

### Using the Docker Image

After building the image, you can run it with:

```bash
docker run --rm -p 8080:8080 your-image-name
```

Replace `your-image-name` with the actual name of the generated image, add spring data source environment variables

### Benefits

- **Optimized Size**: Spring Boot buildpacks create efficient images with only necessary dependencies
- **Security**: Images are built with security best practices
- **Portability**: Runs consistently across different environments
- **Performance**: Optimized for fast startup and low memory footprint

For more details on customizing the build process, refer to the [Spring Boot documentation](https://docs.spring.io/spring-boot/maven-plugin/build-image.html#build-image).

## Contributing

We welcome contributions to the Zenith project! Please follow these guidelines:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/your-feature`)
3. Commit your changes (`git commit -am 'Add new feature'`)
4. Push to the branch (`git push origin feature/your-feature`)
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

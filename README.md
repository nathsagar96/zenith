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
- **Automated Cleanup**: Scheduled job for deleting old archived content

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
└── services/             # Business logic services
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
    "password": "SecurePass123!"
  }
  ```

- **User login**

  ```http
  POST /api/v1/auth/login
  Content-Type: application/json

  {
    "username": "john_doe",
    "email": "john@example.com",
    "password": "password123"
  }
  ```

#### Categories

- **Get all categories**

  ```http
  GET /api/v1/categories?page=0&size=10&sortBy=createdAt&sortDirection=DESC
  ```

- **Create a new category**

  ```http
  POST /api/v1/categories
  Content-Type: application/json
  Authorization: Bearer {token}

  {
    "name": "Technology"
  }
  ```

- **Get category by ID**

  ```http
  GET /api/v1/categories/{categoryId}
  ```

- **Update a category**

  ```http
  PUT /api/v1/categories/{categoryId}
  Content-Type: application/json
  Authorization: Bearer {token}

  {
    "name": "Updated Category Name"
  }
  ```

- **Delete a category**

  ```http
  DELETE /api/v1/categories/{categoryId}
  Authorization: Bearer {token}
  ```

#### Posts

- **Get published posts**

  ```http
  GET /api/v1/posts?page=0&size=10&sortBy=createdAt&sortDirection=DESC&categoryId={categoryId}&tag={tag}
  ```

- **Create a new post**

  ```http
  POST /api/v1/posts
  Content-Type: application/json
  Authorization: Bearer {token}

  {
    "title": "Getting Started with Spring Boot",
    "content": "This is the content of the post",
    "categoryId": "123e4567-e89b-12d3-a456-426614174000",
    "tags": ["spring", "java"]
  }
  ```

- **Get post by ID**

  ```http
  GET /api/v1/posts/{postId}
  Authorization: Bearer {token}
  ```

- **Update a post**

  ```http
  PUT /api/v1/posts/{postId}
  Content-Type: application/json
  Authorization: Bearer {token}

  {
    "title": "Updated Title",
    "content": "This is the updated content",
    "categoryId": "123e4567-e89b-12d3-a456-426614174000",
    "tags": ["spring", "java"]
  }
  ```

- **Delete a post**

  ```http
  DELETE /api/v1/posts/{postId}
  Authorization: Bearer {token}
  ```

- **Get current user's posts**

  ```http
  GET /api/v1/posts/my?page=0&size=10&sortBy=createdAt&sortDirection=DESC&status={status}
  Authorization: Bearer {token}
  ```

#### Comments

- **Get comments for a post**

  ```http
  GET /api/v1/posts/{postId}/comments?page=0&size=10&sortBy=createdAt&sortDirection=DESC
  ```

- **Create a new comment**

  ```http
  POST /api/v1/posts/{postId}/comments
  Content-Type: application/json
  Authorization: Bearer {token}

  {
    "content": "This is a great post!"
  }
  ```

- **Update a comment**

  ```http
  PUT /api/v1/posts/{postId}/comments/{commentId}
  Content-Type: application/json
  Authorization: Bearer {token}

  {
    "content": "This is an updated comment"
  }
  ```

- **Delete a comment**

  ```http
  DELETE /api/v1/posts/{postId}/comments/{commentId}
  Authorization: Bearer {token}
  ```

#### Tags

- **Get all tags**

  ```http
  GET /api/v1/tags?page=0&size=10&sortBy=createdAt&sortDirection=DESC
  ```

- **Create a new tag**

  ```http
  POST /api/v1/tags
  Content-Type: application/json
  Authorization: Bearer {token}

  {
    "name": "Spring Boot"
  }
  ```

- **Get tag by ID**

  ```http
  GET /api/v1/tags/{tagId}
  ```

- **Update a tag**

  ```http
  PUT /api/v1/tags/{tagId}
  Content-Type: application/json
  Authorization: Bearer {token}

  {
    "name": "Updated Tag Name"
  }
  ```

- **Delete a tag**

  ```http
  DELETE /api/v1/tags/{tagId}
  Authorization: Bearer {token}
  ```

#### Users

- **Get all users**

  ```http
  GET /api/v1/users?page=0&size=10&sortBy=createdAt&sortDirection=DESC&role={role}
  Authorization: Bearer {token}
  ```

- **Get user by ID**

  ```http
  GET /api/v1/users/{userId}
  Authorization: Bearer {token}
  ```

- **Get current user**

  ```http
  GET /api/v1/users/me
  Authorization: Bearer {token}
  ```

- **Update a user**

  ```http
  PUT /api/v1/users/{userId}
  Content-Type: application/json
  Authorization: Bearer {token}

  {
    "username": "john_doe",
    "email": "john@example.com",
    "password": "newpassword123",
    "firstName": "John",
    "lastName": "Doe",
    "bio": "Software Developer"
  }
  ```

- **Delete a user**

  ```http
  DELETE /api/v1/users/{userId}
  Authorization: Bearer {token}
  ```

- **Update user role**

  ```http
  PATCH /api/v1/users/{userId}/role?role={role}
  Authorization: Bearer {token}
  ```

#### Moderator

- **Get posts by status**

  ```http
  GET /api/v1/moderator/posts?page=0&size=10&sortBy=createdAt&sortDirection=DESC&status={status}
  Authorization: Bearer {token}
  ```

- **Update post status**

  ```http
  PATCH /api/v1/moderator/posts/{postId}/status?status={status}
  Authorization: Bearer {token}
  ```

- **Get comments by status**

  ```http
  GET /api/v1/moderator/comments?page=0&size=10&sortBy=createdAt&sortDirection=DESC&status={status}
  Authorization: Bearer {token}
  ```

- **Update comment status**

  ```http
  PATCH /api/v1/moderator/comments/{commentId}/status?status={status}
  Authorization: Bearer {token}
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

## Setting Up Test Data

The application includes a data initializer that sets up default test data for development and testing purposes. This includes:

- Default categories (Technology, Lifestyle, Travel)
- Common tags (Java, Spring, Travel, Lifestyle)
- Test users with different roles (admin and regular users)
- Sample blog posts in various states (draft, published, archived)
- Comments with different statuses (approved, pending, spam, archived)

To use this test data, simply run the application normally. The data initializer is configured to run automatically on application startup.

The test data configuration is located in [`DataInitializer.java`](src/main/java/com/zenith/configs/DataInitializer.java).

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

## Archive Cleanup Job

The application includes an automated cleanup job that runs daily at midnight to maintain database health and performance by removing old archived content.

### Purpose

The archive cleanup job automatically deletes:

- Archived posts that are older than 30 days
- Archived comments that are older than 30 days

This helps to:

- Keep the database clean and performant
- Remove unnecessary data that is no longer needed
- Maintain system efficiency

### Scheduling Details

- **Frequency**: Daily at midnight (00:00)
- **Implementation**: Spring Boot's `@Scheduled` annotation with cron expression

### Technical Implementation

- Uses JPQL queries for efficient bulk deletion
- Includes transaction management with `@Transactional`
- Provides basic logging for monitoring the cleanup process

## Contributing

We welcome contributions to the Zenith project! Please follow these guidelines:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/your-feature`)
3. Commit your changes (`git commit -am 'Add new feature'`)
4. Push to the branch (`git push origin feature/your-feature`)
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

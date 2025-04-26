# 🚀 Zenith

A modern, high-performance blog platform built with Spring Boot and Java.

[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Java Version](https://img.shields.io/badge/Java-21-orange)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.4-brightgreen)](https://spring.io/projects/spring-boot)

## 📑 Table of Contents

- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [Technologies](#technologies)
- [Contributing](#contributing)
- [License](#license)
- [Contact](#contact)

## ✨ Features

- 🔐 Secure authentication using JWT
- 📝 CRUD operations for blog posts
- 🏷️ Tag management system
- 🔍 Post filtering and search
- 📊 Post status tracking (Draft/Published)
- 🎯 RESTful API architecture

## ⚙️ Installation

1. Ensure you have the following installed:
   - Java 21
   - Maven
   - Docker

2. Clone the repository:

```bash
git clone https://github.com/nathsagar96/zenith.git
cd zenith
```

3. Configure the database in `application.yaml`

4. Build the project:

```bash
mvn clean install
```

## 🚀 Usage

Run the application:

```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`

## 🛠️ Technologies

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- MapStruct
- PostgreSQL
- JWT
- Lombok
- Maven
- TestContainers

## 👥 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature-name`
3. Commit changes: `git commit -m 'Add new feature'`
4. Push to the branch: `git push origin feature-name`
5. Submit a Pull Request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📫 Contact

Sagar Nath - [@nathsagar96](https://github.com/nathsagar96)

## 🙏 Acknowledgements

- [Spring Boot](https://spring.io/projects/spring-boot) for the framework
- [PostgreSQL](https://www.postgresql.org/) for the database
- [MapStruct](https://mapstruct.org/) for object mapping
- [JWT](https://jwt.io/) for authentication
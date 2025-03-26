# Backend for Frontend (BFF) Project

<div style="display: flex; align-items: center; justify-content: space-evenly; width: 100vw; height: 100px">
  <img src="https://upload.wikimedia.org/wikipedia/en/3/30/Java_programming_language_logo.svg" alt="Java" width="80" height="80"/>
  <img src="https://upload.wikimedia.org/wikipedia/commons/4/4e/Docker_%28container_engine%29_logo.svg" alt="Docker" width="100"/>
  <img src="https://upload.wikimedia.org/wikipedia/commons/4/44/Spring_Framework_Logo_2018.svg" alt="Spring" width="100" height="80"/>
  <img src="./assets/Redis.svg" alt="Redis" width="80" height="80"/>
  <img src="./assets/RabbitMQ.svg" alt="RabbitMQ" width="80" height="80"/>
  <img src="https://upload.wikimedia.org/wikipedia/commons/2/29/Postgresql_elephant.svg" alt="PostgreSQL" width="80" height="80"/>
  <img src="./assets/Keycloak_Logo.png" alt="Keycloak" width="80" height="80"/>
</div>

This repository contains a Backend for Frontend (BFF) implementation designed to serve as an intermediary between frontend applications and various backend services. The BFF pattern enhances security, simplifies frontend development, and optimizes communication with backend systems.

## Project Structure

The project is organized into the following modules:

- **ai-assistant**: Handles AI-related functionalities and services.
- **auth-server**: Manages authentication and authorization processes.
- **domain**: Contains core business logic and domain models.
- **gateway**: Serves as the main entry point, routing requests to appropriate services.

## Prerequisites

Ensure you have the following installed:

- Java Development Kit (JDK) 11 or higher
- Docker
- Docker Compose

## Getting Started

1. **Clone the Repository**:

   ```bash
   git clone https://github.com/LunovVladyslav/bff.git
   ```

2. **Navigate to the Project Directory**:

   ```bash
   cd bff
   ```

3. **Build the Project**:

   Use Maven to build the project:

   ```bash
   mvn clean install
   ```

4. **Run the Services**:

   Start all services using Docker Compose:

   ```bash
   docker-compose up
   ```

   This command will launch all the necessary services defined in the `docker-compose.yaml` file.

## Configuration

Configuration files for each module are located within their respective directories. Ensure that any required environment variables or properties are set appropriately before running the services.

## Contributing

Contributions are welcome! If you have suggestions or improvements, please fork the repository and submit a pull request.

## License

This project is licensed under the MIT License.

---

*Note: This README is based on the available project structure and common practices for BFF implementations. For detailed information on each module's functionality and configurations, please refer to the respective module documentation or source code.*

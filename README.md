#Lumio — AI-Powered BFF Platform
Multi-module Java backend implementing the Backend for Frontend pattern.

## Modules

| Module | Description |
|--------|-------------|
| `gateway` | API Gateway — entry point, request routing |
| `auth-server` | Authentication via Keycloak (OAuth2/OIDC) |
| `ai-assistant` | AI microservice for intelligent features |
| `domain` | Core business logic and shared domain models |

## Stack
Java · Spring Boot · Spring Cloud Gateway · Keycloak  
Redis · RabbitMQ · PostgreSQL · Docker Compose · Maven

## Run
```bash
git clone https://github.com/LunovVladyslav/bff.git
cd bff
docker-compose up

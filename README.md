# Whispr Backend

A real-time chat application backend built with Spring Boot 3.4.0 and Java 21.

## Features

- Real-time messaging using WebSocket
- Thread-based conversations
- User authentication with JWT
- Redis for WebSocket session management
- PostgreSQL for data persistence

## Technologies

- Java 21
- Spring Boot 3.4.0
- Spring Security
- Spring WebSocket
- Redis
- PostgreSQL
- JWT Authentication
- Maven

## Prerequisites

- Java 21
- Maven
- Redis Server
- PostgreSQL Database

## Setup

1. Clone the repository
```bash
git clone https://github.com/yourusername/whispr.git
```

2. Configure database in `application.properties`
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/whispr
spring.datasource.username=your_username
spring.datasource.password=your_password
```

3. Configure Redis in `application.properties`
```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
```

4. Build the project
```bash
mvn clean install
```

5. Run the application
```bash
mvn spring-boot:run
```

## API Documentation

### Authentication
- `POST /api/v1/auth/register` - Register new user
- `POST /api/v1/auth/login` - Login user

### Threads
- `GET /api/v1/thread/{threadId}` - Get thread by ID
- `POST /api/v1/thread` - Create new thread
- `POST /api/v1/thread/join/{code}` - Join thread using invite code

### WebSocket Endpoints
- `ws://localhost:8083/whispr` - WebSocket connection endpoint

WebSocket Message Types:
```json
// Subscribe to thread
{
    "messageType": "SUBSCRIBE_THREAD",
    "threadId": "thread-uuid"
}

// Send message
{
    "messageType": "MESSAGE",
    "message": {
        "content": "Hello",
        "threadId": "thread-uuid"
    }
}
```

## Security

- JWT-based authentication
- WebSocket sessions managed in Redis
- Secure password hashing
- Role-based thread access

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

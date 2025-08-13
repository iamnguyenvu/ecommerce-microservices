# E-Commerce Microservices Platform

A modern, cloud-native microservices-based e-commerce application built with Spring Boot, featuring product management, user authentication, and comprehensive API design. This project demonstrates microservices architecture patterns, including service discovery, distributed databases, and scalable design.

[![Java](https://img.shields.io/badge/Java-21-orange)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.3-brightgreen)](https://spring.io/projects/spring-boot)
[![MongoDB](https://img.shields.io/badge/MongoDB-7.0-green)](https://www.mongodb.com/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue)](https://www.docker.com/)

## 🚀 Project Status

**Current Implementation Status:**

### ✅ Completed Services:
- **auth-service** - Complete user authentication & authorization system
- **product-service** - Complete product catalog management with MongoDB

### 🔄 In Development:
- **order-service** - Order management (planned)
- **payment-service** - Payment processing (planned)
- **notification-service** - Email & SMS notifications (planned)
- **api-gateway** - Service routing & load balancing (planned)
- **frontend** - React-based user interface (planned)

## 🏗️ Architecture Overview

- **Backend:** Java 21, Spring Boot 3.5.3, Maven
- **Frontend:** React 18, Vite, TypeScript (planned)
- **Databases:** PostgreSQL (Auth), MongoDB (Products)
- **API Gateway:** Spring Cloud Gateway (planned)
- **Message Broker:** RabbitMQ (planned)
- **Containerization:** Docker, Docker Compose
- **Security:** JWT-based authentication
- **Documentation:** OpenAPI 3.0 (Swagger)

## 🎯 Microservices

### 🔐 **auth-service** ✅ **COMPLETE**
**User Authentication & Authorization**
- **Database:** PostgreSQL
- **Features:** JWT-based auth, phone number login, role management
- **APIs:** Register, login, token refresh, user management
- **Security:** BCrypt password hashing, JWT tokens
- **Testing:** Comprehensive unit & integration tests
- **Port:** 8081

### 🛍️ **product-service** ✅ **COMPLETE**
**Product Catalog & Inventory Management**
- **Database:** MongoDB with optimized queries
- **Features:** Product CRUD, search, rating system, supplier management
- **Advanced:** QueryConstants pattern for performance optimization
- **Models:** Product, ProductSeries, Supplier with embedded documents
- **APIs:** Product management, inventory tracking, advanced search
- **Testing:** Service layer and repository tests
- **Port:** 8082

### 🛒 **order-service** 🔄 **PLANNED**
**Order Processing & Management**
- **Database:** PostgreSQL
- **Features:** Order creation, status tracking, inventory updates
- **Port:** 8083

### 💳 **payment-service** 🔄 **PLANNED**
**Payment Processing**
- **Database:** PostgreSQL
- **Features:** Payment gateway integration, transaction management
- **Port:** 8084

### 📧 **notification-service** 🔄 **PLANNED**
**Communication & Notifications**
- **Database:** PostgreSQL
- **Features:** Email, SMS, push notifications
- **Port:** 8085

### 🌐 **api-gateway** 🔄 **PLANNED**
**Service Gateway & Load Balancing**
- **Features:** Request routing, authentication, rate limiting
- **Port:** 8080

### 🎨 **frontend** 🔄 **PLANNED**
**User Interface**
- **Tech:** React 18, TypeScript, Vite
- **Features:** Responsive design, user dashboard
- **Port:** 3000

## 📁 Directory Structure

```
ecommerce-microservices/
├── auth-service/                 # ✅ User authentication service
│   ├── src/main/java/           # JWT auth, phone login
│   ├── src/test/                # Comprehensive tests
│   └── README.md                # Service documentation
├── product-service/             # ✅ Product catalog service
│   ├── src/main/java/           # MongoDB integration
│   ├── docs/database/           # Database schemas
│   ├── src/test/                # Service tests
│   └── README.md                # Service documentation
├── order-service/               # 🔄 Order management (planned)
├── payment-service/             # 🔄 Payment processing (planned)
├── notification-service/        # 🔄 Notifications (planned)
├── api-gateway/                 # 🔄 Service gateway (planned)
├── frontend/                    # 🔄 React frontend (planned)
├── docker-compose.yml           # Multi-service orchestration
├── .env.example                 # Environment template
└── README.md                    # This file
```

## 🚀 Quick Start

### Prerequisites
- **Java 21+**
- **Docker & Docker Compose**
- **Node.js 18+** (for frontend)
- **Git**

### 1. Clone Repository
```bash
git clone <repository-url>
cd ecommerce-microservices
```

### 2. Environment Setup
```bash
# Copy environment template (if available)
cp .env.example .env

# Edit .env with your configuration
# Update database credentials, JWT secrets, etc.
```

### 3. Start Infrastructure
```bash
# Start databases and supporting services
docker-compose up -d postgres mongodb

# Wait for databases to initialize (~30 seconds)
```

### 4. Run Services

#### Option A: Docker (Recommended)
```bash
# Build and start all services
docker-compose up --build

# Or start individual services
docker-compose up auth-service product-service
```

#### Option B: Local Development
```bash
# Auth Service
cd auth-service
./mvnw spring-boot:run

# Product Service (new terminal)
cd product-service
./mvnw spring-boot:run
```

### 5. Verify Services
- **Auth Service:** http://localhost:8081/actuator/health
- **Product Service:** http://localhost:8082/actuator/health
- **Swagger UI:** http://localhost:8081/swagger-ui.html

## 🔌 Service Endpoints

| Service | Port | Health Check | API Docs |
|---------|------|-------------|----------|
| **Auth Service** | 8081 | `/actuator/health` | `/swagger-ui.html` |
| **Product Service** | 8082 | `/actuator/health` | `/swagger-ui.html` |
| **Order Service** | 8083 | *Planned* | *Planned* |
| **Payment Service** | 8084 | *Planned* | *Planned* |
| **Notification Service** | 8085 | *Planned* | *Planned* |
| **API Gateway** | 8080 | *Planned* | *Planned* |
| **Frontend** | 3000 | *Planned* | *Planned* |

## 🧪 API Testing

### Auth Service Examples
```bash
# Register new user
curl -X POST http://localhost:8081/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "phoneNumber": "+1234567890",
    "password": "SecurePass123"
  }'

# Login
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "phoneNumber": "+1234567890",
    "password": "SecurePass123"
  }'
```

### Product Service Examples
```bash
# Get all products
curl -X GET http://localhost:8082/api/v1/products

# Search products
curl -X GET "http://localhost:8082/api/v1/products/search?title=laptop&category=electronics"

# Create product
curl -X POST http://localhost:8082/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{
    "title": "MacBook Pro",
    "sku": "MBP-001", 
    "description": "Latest MacBook Pro",
    "pricing": {
      "salePrice": 2499.99,
      "currency": "USD"
    }
  }'

# Rate a product (requires JWT token)
curl -X POST http://localhost:8082/api/v1/products/{productId}/rate \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"rating": 5, "comment": "Excellent product!"}'
```

## 🛠️ Tech Stack

### Backend
- **Java 21** - Latest LTS version
- **Spring Boot 3.5.3** - Main framework
- **Spring Security** - Authentication & authorization
- **Spring Data JPA** - PostgreSQL integration
- **Spring Data MongoDB** - MongoDB integration
- **JWT** - Stateless authentication
- **MapStruct** - Entity-DTO mapping
- **Maven** - Dependency management

### Databases
- **PostgreSQL 16+** - User data, orders, payments
- **MongoDB 7+** - Product catalog, inventory

### Development Tools
- **Docker** - Containerization
- **Docker Compose** - Multi-service orchestration
- **Swagger/OpenAPI** - API documentation
- **JUnit 5** - Testing framework

### Planned Technologies
- **React 18** - Frontend framework
- **TypeScript** - Type-safe frontend
- **Spring Cloud Gateway** - API gateway
- **RabbitMQ** - Message broker

## 📚 Documentation

### Service Documentation
- **Auth Service** - Authentication & user management
- **Product Service** - Product catalog & inventory

### Database Schemas
- **Product Service Database** - MongoDB collections & indexes
- **Auth Service Database** - PostgreSQL tables for user management

### Additional Resources
- **Environment Setup** - Development environment configuration
- **API Examples** - Request/response examples

## 🏗️ Development

### Project Structure
Each service follows Spring Boot best practices:
```
service-name/
├── src/main/java/
│   ├── controller/     # REST API endpoints
│   ├── service/        # Business logic
│   ├── repository/     # Data access layer
│   ├── model/          # Entity classes
│   ├── dto/            # Data transfer objects
│   └── config/         # Configuration classes
├── src/test/           # Unit & integration tests
└── src/main/resources/ # Configuration files
```

### Code Quality
- **Design Patterns:** Repository, Service, DTO patterns
- **Performance:** QueryConstants pattern for MongoDB optimization
- **Security:** JWT tokens, password hashing, input validation
- **Testing:** Unit tests, integration tests, service layer tests
- **Documentation:** Swagger UI, comprehensive README files

### Contributing
1. Fork the repository
2. Create feature branch: `git checkout -b feature/new-feature`
3. Commit changes: `git commit -m "Add new feature"`
4. Push branch: `git push origin feature/new-feature`
5. Submit pull request

## 📈 Roadmap

### Phase 1 ✅ **COMPLETED**
- [x] Auth service with JWT authentication
- [x] Product service with MongoDB integration
- [x] Docker containerization
- [x] API documentation

### Phase 2 🔄 **IN PROGRESS**
- [ ] Order service implementation
- [ ] Payment service integration
- [ ] API gateway setup
- [ ] Service discovery

### Phase 3 🎯 **PLANNED**
- [ ] React frontend development
- [ ] Real-time notifications
- [ ] Advanced search features
- [ ] Performance monitoring

## 🤝 Contributing

We welcome contributions! Please see our contributing guidelines and submit pull requests for any improvements.

## 📄 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

---

**Built with ❤️ for learning microservices architecture**
# Velocity Renderer

> A modern, web-based Apache Velocity template rendering application built with Spring Boot and Vaadin.

[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.8-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vaadin](https://img.shields.io/badge/Vaadin-24.9.6-blue.svg)](https://vaadin.com/)

---

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Running the Application](#running-the-application)
- [Usage](#usage)
- [Project Architecture](#project-architecture)
- [Development](#development)
- [Deployment](#deployment)
- [Testing](#testing)
- [Contributing](#contributing)

---

## Overview

**Velocity Renderer** is a modern web application designed to simplify working with Apache Velocity templates. It provides a split-panel, browser-based interface for rendering Velocity templates with JSON data in real-time, making it ideal for:

- Testing and debugging Velocity templates with live preview
- Rapid prototyping of dynamic HTML content
- Learning the Velocity Template Language (VTL)
- Template development with instant visual feedback

The application combines the Apache Velocity templating engine with Vaadin's reactive UI framework and includes advanced performance optimizations like Spring AOT compilation, ZGC, and native image support.

---

## Features

### Core Functionality
- ✅ **Real-time Template Rendering** - Live preview of Velocity templates with instant updates
- ✅ **JSON Data Integration** - Parse and validate JSON input for template variables
- ✅ **Sandboxed Preview** - Secure iframe-based HTML rendering with optional JavaScript execution
- ✅ **Local Storage Persistence** - Save and restore templates and data automatically
- ✅ **Split-Panel Editor** - Dual editor layout for templates and JSON data
- ✅ **Error Handling** - User-friendly error messages with XSS protection

### Technical Highlights
- ⚡ **Advanced Performance** - Spring AOT compilation, ZGC garbage collector, JLink, and CDS enabled
- 🚀 **Production Ready** - Optimized production builds with Vaadin production mode
- 🐳 **Container Optimized** - Docker buildpacks with native image and UPX compression support
- 🔧 **GraalVM Native** - Native image compilation for ultra-fast startup and reduced memory footprint
- 🌐 **Internationalization** - Multi-language support (English & Sinhala)

---

## Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| **Language** | Java | 25 |
| **Framework** | Spring Boot | 3.5.8 |
| **UI Framework** | Vaadin | 24.9.6 |
| **Template Engine** | Apache Velocity | 2.4.1 |
| **Build Tool** | Apache Maven | 3.6+ |
| **Runtime** | JVM / GraalVM Native | - |
| **GC** | ZGC (Z Garbage Collector) | - |

---

## Getting Started

### Prerequisites

Ensure you have the following installed on your system:

- **Java Development Kit (JDK) 25** or higher
  ```bash
  java -version
  ```
- **Maven 3.6+** (optional, Maven Wrapper included)
  ```bash
  mvn -version
  ```
- **(Optional) GraalVM** - For native image compilation

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd velocity-renderer
   ```

2. **Build the project**
   
   Using Maven Wrapper (recommended):
   ```bash
   # Unix/Linux/macOS
   ./mvnw clean install
   
   # Windows
   mvnw.cmd clean install
   ```
   
   Or using your system Maven:
   ```bash
   mvn clean install
   ```

### Running the Application

#### Development Mode

Start the application with hot-reload enabled for rapid development:

```bash
./mvnw spring-boot:run
```

The application will start on **http://localhost:8080**

> **Note:** Development mode includes Vaadin development bundle with live reload capabilities.

#### Production Mode

1. **Build the production package:**
   ```bash
   ./mvnw clean package -Pproduction
   ```

2. **Run the production JAR:**
   ```bash
   java -jar target/velocity-renderer-1.0.0.jar
   ```

#### Native Image Build (GraalVM)

For ultra-fast startup times and reduced memory footprint, build a native executable:

**Option 1: Native compilation with production optimization (recommended):**
```bash
./mvnw clean package -Pnative -Pproduction
./target/velocity-renderer
```

**Option 2: Native compilation only:**
```bash
./mvnw -Pnative native:compile
./target/velocity-renderer
```

> **Note:** Native builds require GraalVM to be installed. The combined `-Pnative -Pproduction` profiles create an optimized native executable with production frontend assets.

---

## Usage

### Basic Workflow

1. **Launch the application** and navigate to `http://localhost:8080`

2. **Enter your Velocity template** in the HTML editor (top-left panel):
   ```velocity
   <h1>Hello, $name!</h1>
   
   #if($items)
   <ul>
   #foreach($item in $items)
     <li>$item</li>
   #end
   </ul>
   #end
   ```

3. **Provide JSON data** for template variables (bottom-left panel):
   ```json
   {
     "name": "John Doe",
     "items": ["Apple", "Banana", "Orange"]
   }
   ```

4. **View the live preview** in the right panel (updates automatically)

5. **Save your work** using the Save button (persists to browser's local storage)

6. **Toggle JavaScript** execution if needed (disabled by default for security)

### Example Templates

<details>
<summary>Simple Variable Substitution</summary>

**Template:**
```velocity
Welcome to $company!
User: $user.firstName $user.lastName
Email: $user.email
```

**Data:**
```json
{
  "company": "Acme Corp",
  "user": {
    "firstName": "Jane",
    "lastName": "Smith",
    "email": "jane.smith@example.com"
  }
}
```
</details>

<details>
<summary>Loops and Conditionals</summary>

**Template:**
```velocity
#if($orders.size() > 0)
Order Summary:
#foreach($order in $orders)
  Order #$order.id - $order.product: $$order.price
#end
Total: $$total
#else
No orders found.
#end
```

**Data:**
```json
{
  "orders": [
    {"id": 1, "product": "Widget", "price": 29.99},
    {"id": 2, "product": "Gadget", "price": 49.99}
  ],
  "total": 79.98
}
```
</details>

---

## Project Architecture

### Directory Structure

```
velocity-renderer/
├── src/
│   ├── main/
│   │   ├── java/dev/iamkavindu/
│   │   │   ├── config/                      # Application Configuration
│   │   │   │   └── AppConfig.java
│   │   │   ├── errors/                      # Custom Exceptions
│   │   │   │   └── TemplateRenderException.java
│   │   │   ├── service/                     # Service Interfaces
│   │   │   │   ├── JsonParserService.java
│   │   │   │   ├── TemplateRenderService.java
│   │   │   │   └── impl/                    # Service Implementations
│   │   │   │       ├── JsonParserServiceImpl.java
│   │   │   │       └── VelocityTemplateService.java
│   │   │   ├── ui/                          # Vaadin UI Layer
│   │   │   │   └── MainView.java           # Main application view
│   │   │   └── VelocityRendererApplication.java
│   │   ├── resources/
│   │   │   ├── application.properties       # Spring Boot configuration
│   │   │   ├── ui.properties               # UI customization
│   │   │   └── vaadin-i18n/                # Internationalization
│   │   │       ├── translations.properties  # English
│   │   │       └── translations_si.properties # Sinhala
│   │   └── frontend/                        # Vaadin Frontend Resources
│   └── test/                                # Unit & Integration Tests
├── pom.xml                                  # Maven Configuration
├── mvnw / mvnw.cmd                          # Maven Wrapper Scripts
└── README.md
```

### Key Components

#### Service Layer

| Service | Responsibility |
|---------|---------------|
| **TemplateRenderService** | Interface for template rendering operations |
| **VelocityTemplateService** | Apache Velocity implementation with error handling |
| **JsonParserService** | Interface for JSON parsing operations |
| **JsonParserServiceImpl** | JSON to Map conversion with validation |

#### UI Layer

| Component | Description |
|-----------|-------------|
| **MainView** | Split-panel interface with template editor, JSON editor, and live preview |
| **LocalStorage Integration** | Persists templates and settings to browser storage |
| **Sandboxed Preview** | Secure iframe with configurable JavaScript execution |

#### Configuration

- **AppConfig** - Spring beans and Velocity engine configuration
- **application.properties** - Spring Boot settings (auto-launch browser, caching)
- **ui.properties** - UI customization and labels
- **vaadin-i18n/** - Translation files for supported languages

---

## Development

### Code Style

This project follows standard Java coding conventions. Key points:
- Use meaningful variable and method names
- Keep methods focused and concise
- Add JavaDoc comments for public APIs
- Follow Spring Boot best practices

### Running Tests

Execute the test suite:

```bash
./mvnw test
```

Run with coverage:

```bash
./mvnw clean verify
```

### Hot Reload

In development mode, Vaadin supports hot reload:
- Java changes require application restart
- Frontend changes (TypeScript/CSS) reload automatically

### Internationalization

The application supports multiple languages. Add new translations in `src/main/resources/vaadin-i18n/`:

- `translations.properties` - Default (English)
- `translations_si.properties` - Sinhala
- `translations_{lang}.properties` - Add more languages as needed

The language selector in the UI allows users to switch between available languages.

---

## Deployment

### Docker Deployment

Build an optimized container image using Spring Boot Buildpacks with advanced configurations:

```bash
./mvnw spring-boot:build-image
```

This creates a highly optimized image with:
- **ZGC** garbage collector for low-latency
- **JLink** for minimal JRE footprint
- **Spring AOT** for faster startup
- **CDS** (Class Data Sharing) for reduced memory usage
- **Native Image** support with UPX compression
- **Tiny Builder** (Paketo) for smallest possible image size

Run the container:

```bash
docker run -p 8080:8080 velocity-renderer:1.0.0
```

### Traditional Deployment

Deploy the production JAR to any Java-compatible environment:

```bash
java -jar velocity-renderer-1.0.0.jar
```

### Environment Configuration

Key configuration options in `src/main/resources/application.properties`:

```properties
# Application Settings
spring.application.name=velocity-renderer
vaadin.launch-browser=true

# Performance Tuning
spring.web.resources.cache.use-last-modified=false

# Server Configuration (optional override)
server.port=8080
```

---

## Testing

### Unit Tests

The project includes comprehensive unit tests for all service layers:

```bash
./mvnw test
```

### Integration Tests

Integration tests verify the full application stack:

```bash
./mvnw verify
```

---

## Contributing

We welcome contributions! Please follow these steps:

1. **Fork** the repository
2. **Create** a feature branch
   ```bash
   git checkout -b feature/your-amazing-feature
   ```
3. **Commit** your changes
   ```bash
   git commit -m "Add: your amazing feature"
   ```
4. **Push** to your branch
   ```bash
   git push origin feature/your-amazing-feature
   ```
5. **Open** a Pull Request

### Contribution Guidelines

- Write clear, concise commit messages
- Add tests for new features
- Update documentation as needed
- Follow the existing code style
- Ensure all tests pass before submitting

---

## Acknowledgments

Built with ❤️ using:

- **[Apache Velocity](https://velocity.apache.org/)** - Powerful template engine
- **[Vaadin](https://vaadin.com/)** - Modern web framework
- **[Spring Boot](https://spring.io/projects/spring-boot)** - Enterprise-grade Java framework

---

**Author:** Kavindu Perera ([@iamkavindu](https://github.com/iamkavindu))

**Questions?** Feel free to open an issue or reach out!

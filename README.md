# Velocity Renderer

> A web-based tool for rendering Apache Velocity templates with JSON data in real-time.

[![Java](https://img.shields.io/badge/Java-25-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.8-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vaadin](https://img.shields.io/badge/Vaadin-24.9.6-blue.svg)](https://vaadin.com/)

---

## Overview

**Velocity Renderer** is a web-based tool for rendering Apache Velocity templates with JSON data in real-time. It features a split-panel interface with template/data editors on the left and live preview on the right, ideal for:

- Testing and debugging Velocity templates with instant feedback
- Rapid prototyping of dynamic HTML content
- Learning the Velocity Template Language (VTL)
- Sandboxed HTML preview with optional JavaScript execution control

---

## Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| **Language** | Java | 25 |
| **Framework** | Spring Boot | 3.5.8 |
| **UI Framework** | Vaadin | 24.9.6 |
| **Template Engine** | Apache Velocity | 2.4.1 |
| **Build Tool** | Apache Maven | 3.6+ |

---

## Usage

### Basic Workflow

1. **Launch the application** and navigate to `http://localhost:8080`

2. **Enter your Velocity template** in the top editor:
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

3. **Provide JSON data** for template variables in the bottom editor:
   ```json
   {
     "name": "John Doe",
     "items": ["Apple", "Banana", "Orange"]
   }
   ```

4. **View the live preview** in the right panel (updates automatically with 2-second delay)

5. **Controls available:**
   - **Save** - Persist templates and data to browser local storage
   - **Clear** - Clear all content and local storage
   - **Render** - Manual render (when live rendering is disabled)
   - **Live Render** - Toggle automatic preview updates
   - **Enable JavaScript** - Allow JavaScript execution in preview (disabled by default for security)
   - **Language Selector** - Switch between English and Sinhala

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

## Deployment

### Using Docker Compose

The easiest way to run the application:

```bash
docker-compose up -d
```

This pulls the pre-built image from GitHub Container Registry and starts the application with health checks and automatic restart policies.

### Using Docker

Build and run as a container:

```bash
# Build the image
docker build -t velocity-renderer:1.0.0 .

# Run the container
docker run -p 8080:8080 velocity-renderer:1.0.0
```

### Using Maven

Run directly with Maven:

```bash
# Development mode
./mvnw spring-boot:run

# Production build
./mvnw clean package -Pproduction
java -jar target/velocity-renderer-1.0.0.jar
```

The application will be available at `http://localhost:8080`

---

## Acknowledgments

Built with ❤️ using:

- **[Apache Velocity](https://velocity.apache.org/)** - Powerful template engine
- **[Vaadin](https://vaadin.com/)** - Modern web framework
- **[Spring Boot](https://spring.io/projects/spring-boot)** - Enterprise-grade Java framework

---

**Author:** Kavindu Perera ([@iamkavindu](https://github.com/iamkavindu))

**Questions?** Feel free to open an issue or reach out!

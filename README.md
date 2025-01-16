# E-Commerce Backend Application

This is a **Java-based e-commerce backend application** built with **Spring Boot** and **Spring Framework**. It provides RESTful APIs for user authentication, product management, cart functionality, order processing, and address management. The application also includes features like **JWT-based authentication**, **email verification**, and **role-based access control**.

---

## **Technologies Used**
- **Backend**: Java, Spring Boot, Spring Framework (Spring MVC, Spring Data JPA, Spring Security)
- **Database**: MySQL
- **Authentication**: JWT (JSON Web Tokens), Role-Based Access Control (USER, ADMIN)
- **Tools**: Maven, Git, Postman, Docker
- **APIs**: RESTful APIs, Pagination, Sorting, Validation
- **Other**: Email Verification, OTP (One-Time Password), File Upload (Images)

---

## **Features**
- **User Management**: User registration, login, email verification, and role-based access control (USER, ADMIN).
- **Product Management**: CRUD operations for products, category-based product filtering, and image upload for products.
- **Cart & Order Management**: Users can add/remove items to/from the cart, place orders, and cancel orders.
- **Address Management**: Users can add, update, and delete shipping addresses.
- **Security**: JWT-based authentication, password encryption (BCrypt), and OTP-based password reset.
- **Validation**: Custom validators for file types (e.g., images) and input fields (e.g., email, password).

---

## **How to Run the Project**

### **Prerequisites**
- Java 17 or higher
- Maven 3.x
- MySQL or PostgreSQL
- IntelliJ IDEA (or any IDE of your choice)

### **Steps to Run**
1. **Clone the Repository**:
   ```bash
   git clone https://github.com/your-username/ecommerce-backend.git
   cd ecommerce-backend

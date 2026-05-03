

```markdown
# 📋 Enterprise Task Management System (RBAC)

![React](https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-F2F4F9?style=for-the-badge&logo=spring-boot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)

A robust, full-stack Task Management application designed for organizational workflows. Built with **Spring Boot** and **React**, this system implements strict **Role-Based Access Control (RBAC)** to ensure data security and operational hierarchy. 

Managers can oversee enterprise projects and delegate tasks, while Team Members benefit from a focused, isolated workspace to manage their active assignments.

---

## 🌐 Live Demo & Deployment

> ⚠️ **CRITICAL INSTRUCTION: PLEASE READ BEFORE TESTING** ⚠️
>
> **You MUST wake up the Backend server first before using the Frontend.** 
> Because the backend is hosted on Render's free tier, the server automatically goes to sleep after a period of inactivity. It can take **30 to 60 seconds** to wake up on the first request. 
> 
> **If you try to log in or interact with the Frontend before the Backend is awake, the application will throw a network error.**

Follow these exact steps to test the live application:

### Step 1: Wake up the Backend
Click the link below and wait until the page finishes loading. (Depending on your default route, it may show a "Whitelabel Error Page" or a 404—this is completely fine, it just means the Java server is awake and running!)
🔗 **Backend Server:** [https://backend-l89a.onrender.com](https://backend-l89a.onrender.com)

### Step 2: Open the Frontend
Once the backend link has successfully loaded, open the frontend. You can now register, log in, and test the Role-Based Access Control seamlessly.
🔗 **Frontend App:** [https://frontend-tau-eight-67.vercel.app](https://frontend-tau-eight-67.vercel.app)

---

## ✨ System Features

### 🔐 Advanced Security & Authentication
* **Stateless JWT Authentication:** Secure API communication using JSON Web Tokens.
* **Strict Role-Based Access Control (RBAC):** Distinct `ADMIN` (Manager) and `MEMBER` roles enforced at both the UI routing level and the Spring Security API level.
* **Defensive Registration (Anti-Privilege Escalation):** The backend actively overrides frontend payloads, forcing all new registrations to default to the `MEMBER` role, preventing malicious users from creating Admin accounts.
* **Smart Axios Interceptors:** Global frontend error handling that automatically clears invalid/expired tokens (401) and elegantly handles unauthorized data access attempts (403) without disrupting the user session.

### 💼 Manager Portal (`ADMIN`)
* **Project Lifecycle Management:** Create, read, update, and delete organizational projects.
* **Team Construction:** Search system users and selectively assign or remove them from specific project teams.
* **Targeted Task Delegation:** Create tasks with mandatory due dates and assign them *only* to verified members of that specific project team.
* **Global Oversight:** View all tasks across the system, monitor overdue items, and permanently delete tasks.

### 👥 Member Portal (`MEMBER`)
* **Isolated Project Dashboard:** Members only see projects they belong to. Unassigned projects are visually locked using a secure CSS backdrop-filter blur.
* **Task Execution:** View assigned tasks, track deadlines with automatic overdue highlighting, and update task statuses to `COMPLETED`.
* **Restricted Action Scope:** Members are strictly prevented (via UI and Backend) from creating projects, deleting tasks, or completing tasks assigned to other users.

---

## 🏗️ Technical Architecture

The application follows a decoupled, client-server architecture:

1. **Frontend (Client):** A React Single Page Application (SPA) utilizing `react-router-dom` for navigation and the Context API for global authentication state management.
2. **REST API (Server):** A Spring Boot backend utilizing Spring Web, Spring Security, and Spring Data JPA to process business logic and secure endpoints.
3. **Database (Persistence):** A PostgreSQL relational database storing Users, Projects, and Tasks with enforced cascading constraints.

---

## 🚀 Getting Started (Local Development)

### Prerequisites
Before you begin, ensure you have the following installed:
* **Java Development Kit (JDK) 17+**
* **Node.js (v18+)** and **npm**
* **PostgreSQL** (Running locally on port `5432`)

### 1. Database Configuration
Create a new PostgreSQL database for the application:
```sql
CREATE DATABASE taskmanager;
```

### 2. Backend Setup (Spring Boot)
1. Clone the repository and navigate to the `backend` directory.
2. Open `src/main/resources/application.properties` and configure your environment:

   ```properties
   # Database Configuration
   spring.datasource.url=jdbc:postgresql://localhost:5432/taskmanager
   spring.datasource.username=postgres
   spring.datasource.password=your_password
   
   # Hibernate Configuration
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
   spring.jpa.show-sql=true
   
   # JWT Security Configuration
   jwt.secret=your_super_secret_key_for_jwt_generation_in_production
   jwt.expiration=86400000 
   ```

3. Start the application:
   
```bash
   ./mvnw spring-boot:run
   ```
   *The backend will initialize and run on `http://localhost:8080`.*

### 3. Frontend Setup (React)
1. Navigate to the `frontend` directory.
2. Install the necessary Node modules:
   ```bash
   npm install
   ```
3. Start the development server:
   ```bash
   npm run dev
   ```
   *The application will open at `http://localhost:5173` (or port 3000).*

---

## 🔑 Initial Administrator Setup

Because this system employs strict defensive registration, **it is impossible to register an Admin account via the frontend interface.** All new accounts default to `MEMBER`.

To establish your initial System Administrator (Manager) locally:
1. Navigate to the frontend and register a new account through the Member Portal (e.g., `admin@company.com`).
2. Open your database management tool (pgAdmin, DBeaver, etc.) and connect to your `taskmanager` database.
3. Execute the following SQL script to manually elevate the user's privileges:

   ```sql
   UPDATE users 
   SET role = 'ADMIN' 
   WHERE email = 'admin@company.com';
   ```
4. Return to the frontend, select the **Manager Portal**, and log in to begin configuring the system.

---

## 📡 Core API Reference

| HTTP Method | Endpoint | Description | Required Role |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/login` | Authenticates user and returns JWT | `ANY` |
| `POST` | `/api/users/register` | Registers a new user (defaults to MEMBER) | `ANY` |
| `GET` | `/api/users` | Retrieves all system users | `ADMIN` |
| `POST` | `/api/projects` | Creates a new project | `ADMIN` |
| `GET` | `/api/projects` | Retrieves all projects | `ADMIN` / `MEMBER` |
| `DELETE` | `/api/projects/{id}` | Deletes a project | `ADMIN` |
| `POST` | `/api/projects/{projectId}/members/{userId}` | Adds a user to a project team | `ADMIN` |
| `POST` | `/api/tasks/project/{projectId}/assign/{userId}` | Creates a task and assigns to member | `ADMIN` |
| `GET` | `/api/tasks/project/{projectId}` | Gets all tasks for a specific project | `ADMIN` / `MEMBER` |
| `PUT` | `/api/tasks/{taskId}/status` | Updates task status to COMPLETED | `ADMIN` / *`TASK_OWNER`* |
| `DELETE` | `/api/tasks/{taskId}` | Permanently deletes a task | `ADMIN` |

---

## 📂 Project Structure
```text
task-management-system/
├── backend/                  # Spring Boot API
│   ├── src/main/java/com/taskmanager/
│   │   ├── config/           # Security & CORS configuration
│   │   ├── controller/       # REST API Endpoints
│   │   ├── entity/           # JPA Domain Models (User, Project, Task)
│   │   ├── enums/            # Role and Status Enumerations
│   │   ├── repository/       # Spring Data JPA Interfaces
│   │   ├── security/         # JWT Filters and Authentication Providers
│   │   └── service/          # Business Logic and Validation
│   └── pom.xml
└── frontend/                 # React Application
    ├── src/
    │   ├── components/       # Reusable UI components
    │   ├── context/          # Global AuthContext (JWT/Role State)
    │   ├── pages/            # View components (Dashboard, Login, TaskView)
    │   ├── services/         # Axios API configuration and interceptors
    │   ├── App.jsx           # Main router configuration
    │   └── main.jsx
    └── package.json
```

```

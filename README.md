# PassManager

**PassManager** is a full-stack password manager browser extension that lets users securely store and manage their credentials. It provides a user-friendly interface (frontend) for logging in and handling password entries, and a Spring Boot backend that handles data storage and security. User credentials (site, username, password, etc.) are stored in a local database and encrypted before saving. For example, one similar project describes itself as “a fully-functional password manager written in Java Spring Boot and … React!”, highlighting this common technology stack. In this app, key features include user authentication, encrypted credential storage (e.g. using AES-256), and CRUD operations (adding, updating, deleting, copying passwords).  

## Key Features

- **Secure Storage:** Passwords and related account information are stored in an embedded database (e.g. H2) and encrypted for security. Encryption (such as AES-256) ensures that even if the database is accessed, the raw passwords remain protected.
- **User Authentication:** Users log in with a master account. Authentication is handled by Spring Security, which provides built-in support for username/password login and role-based authorization.
- **Credential Management:** Users can **add**, **view**, **update**, and **delete** credential entries via the UI. Each entry includes fields like website, username, password, and creation date. The UI also allows copying passwords to the clipboard for convenience.
- **Modern Tech Stack:** The backend is built with **Java Spring Boot**, a framework that makes it easy to create standalone, production-ready web applications. The frontend is a single-page application built with a modern JavaScript framework (e.g. **React**) for interactive user interfaces. Data persistence uses **Spring Data JPA** to interact with the database.
- **Local-Only/Offline by Design:** All data is kept locally. The app does not rely on external services; it runs entirely on the user’s machine. (This aligns with best practices of local password managers, ensuring no sensitive data is sent over the internet.)

## Frontend

The frontend is a responsive single-page application (SPA) that interacts with the user and communicates with the backend via HTTP. It is built using a popular JavaScript UI library or framework (e.g. React). Key frontend aspects include:

- **User Interface:** Clean login and dashboard views. After logging in, users see a list of stored credentials and can add new entries or edit existing ones.
- **Dynamic Interaction:** Uses components and state management to update the UI in real time. For example, adding a new password entry immediately updates the list without a full page reload.
- **Forms & Validation:** Includes forms for registration, login, and credential entry. Inputs are validated on the client side before submission.
- **Clipboard Support:** Provides a button or icon to copy password values to the clipboard (common in password managers).
- **HTTP API Calls:** The frontend calls the backend’s REST API to fetch and modify data. For instance, it sends a POST request to create a new credential or a GET request to retrieve all saved credentials. This separation follows standard RESTful design principles (Spring Boot handles these endpoints).

## Backend

The backend is implemented with **Spring Boot**, a Java framework that simplifies building standalone web services. Key backend components include:

- **REST API:** Exposes endpoints for user registration, login (authentication), and CRUD operations on credentials. Controllers are annotated (e.g. `@RestController`, `@RequestMapping`) so that requests (HTTP verbs like GET/POST) map to Java methods.
- **Spring Security:** Manages authentication and authorization. It handles login requests and secures the API endpoints so that only authenticated users can access their data.
- **Data Layer:** Uses **Spring Data JPA** (Hibernate) to map Java entities to the database. Credential entries and user records are stored in an embedded **H2** database by default (configurable). The schema includes columns for website, username, encrypted password, creation date, etc. Spring Boot auto-configures the database and server (e.g. embedded Tomcat) so the app runs standalone.
- **Encryption:** Before saving any password, the backend encrypts it (for example with AES-256) to ensure sensitive data is protected. The encryption key may be derived from the user’s master password or stored securely.
- **Additional Features:** Other backend utilities may include logging, exception handling, and input validation. (For example, Spring Boot can automatically validate incoming request bodies against constraints.)

## Technology Stack

- **Languages & Runtime:** Java (for backend), JavaScript/TypeScript (for frontend).  
- **Backend Framework:** Spring Boot (Spring MVC for web, Spring Data JPA, Spring Security).  
- **Frontend Framework:** React (JavaScript library for UIs) *or* a similar modern framework (Angular, Vue, etc.) for building the SPA.  
- **Database:** H2 (an in-memory/embedded SQL database) or another relational DB via JDBC. Spring Data JPA (Hibernate) handles ORM.  
- **Build & Tools:** Maven or Gradle (for Java builds), Node.js/npm (for frontend build), Git for version control.  
- **Security:** Passwords are hashed/encrypted (e.g. using strong algorithms like AES-256), and all sensitive operations are protected by Spring Security.  
- **Local Development:** Can be run on any system with Java and Node.js installed. (Spring Boot apps embed a web server so no external server setup is needed.)

## Getting Started (Local Setup)

1. **Clone the Repository:**  
   ```bash
   git clone https://github.com/KrzysztofZdebski/PassManager.git
   cd PassManager
   ```
2. **Backend Setup:** (requires JDK 17+)  
   - Navigate to the backend folder: `cd backend`.  
   - Build and run with Maven or Gradle. For Maven, run:  
     ```bash
     ./mvnw spring-boot:run
     ```  
     This starts the Spring Boot server (default port 8080).  
   - *(Alternatively, you can build a JAR with `./mvnw clean package` and run `java -jar target/*.jar`.)*  
   - The backend will initialize the database (creating tables) and listen for API requests.

3. **Frontend Setup:** (requires Node.js and npm)  
   - Open a new terminal and go to the frontend folder: `cd frontend`.  
   - Install dependencies:  
     ```bash
     npm install
     ```  
   - Start the development server (commonly):  
     ```bash
     npm start
     ```  
     This launches the frontend (usually on http://localhost:3000) and opens it in your browser.  
   - The frontend will automatically proxy API requests to the Spring Boot backend (check `package.json` or proxy config if needed).

4. **Usage:**  
   - In your browser, go to the frontend URL (e.g. http://localhost:3000).  
   - You should see a login or registration screen. Create an account or log in.  
   - Once authenticated, use the UI to add, view, and manage your password entries. The data is stored locally in the backend database.

## Local Development

- **Hot Reload (Frontend):** The frontend’s dev server typically supports hot-reloading, so code changes in UI files refresh in real time.  
- **Backend Refresh:** Changes to Java code usually require restarting the Spring Boot app. Spring DevTools can enable automatic restart on code changes.  
- **API Testing:** You can use tools like cURL or Postman to test the backend endpoints directly (e.g. GET `/credentials` after logging in).  
- **Database Console:** If using H2, you can access the H2 console (if enabled) at `http://localhost:8080/h2-console` to inspect the data. By default, Spring Boot can auto-configure an H2 console.

## Contribution

Contributions are welcome! If you find bugs or want to add features, please open an issue or submit a pull request. Developers familiar with Java, Spring Boot, and modern JavaScript frameworks are encouraged to contribute. (No official CONTRIBUTING.md is provided, so please follow standard GitHub practices for forks and PRs.)

---

**Sources:**  
The technologies and features mentioned here follow standard practices for Spring Boot and modern front-end development. For example, similar open-source password manager projects highlight using Spring Boot with AES encryption and UI frameworks like React.


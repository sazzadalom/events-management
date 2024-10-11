# events-management
Event Management Appllication using Spring Boot.
Application Documentation
Table of Contents
1.	Overview
2.	Installation
3.	Configuration
4.	Usage
5.	API Documentation
6.	Exception Handling
7.	Testing
8.	Contributing



Description: The Event Management System is a Spring Boot application that allows users to manage events, attendees, and associated media. The application provides functionalities for creating, updating, retrieving, and deleting event records while supporting Excel file uploads for attendee management.

1.	View Events:
o	Pagination: List events in a paginated format to improve performance.
o	Total Count: Display the total number of events in the system.
o	Search: Allow users to search for events based on event name and a date range.
2.	Search for an Event:
o	Search functionality should support filtering by event name and optionally by date range (start date, end date).
3.	Add/Edit Events (Admin-Restricted):
o	Admin should be restricted to add new events or modify existing ones.
o	Role-based access control will ensure that admin don’t have the permissions.
4.	Remove Events (Admin- Restricted):
o	Admin not can delete events from the system.
o	Proper checks and balances should be in place (e.g., confirmation dialogs or audit logging) to avoid accidental deletion.
5.	User Registration/Login:
o	Users can register and log in to the system.
o	Use Spring Security with JWT for secure authentication.

Technology Stack
•	Programming Language: Java (latest version)
•	Framework: Spring Boot (Spring Security for authentication and authorization)
•	Database: MySQL
•	Documentation: Swagger for documenting the APIs

1.	Security:
o	Use Spring Security with JWT (JSON Web Tokens) for securing the application.
o	Implement role-based access control (RBAC) where users are assigned roles like USER and ADMIN.
o	Ensure that the endpoints for adding, editing, and removing events are restricted to users with the ADMIN role.
2.	Pagination and Searching:
o	Spring Data JPA provides built-in pagination and filtering features.
o	For searching by event name and date range, use query parameters in the API to filter the results.
3.	Swagger Integration:
o	Swagger should be integrated for automatic API documentation.
o	This allows developers and administrators to interact with the system via the documented endpoints.


Configure Application Properties
Update the application.properties file located in src/main/resources/ with your database and Redis connection settings:
properties
Copy code
spring.datasource.url=jdbc:mysql://localhost:3306/aurusitdb
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password

spring.redis.host=localhost
spring.redis.port=6379
Configuration
Redis Configuration
The Redis configuration is set up in RedisConfig.java:
•	Connection Factory: Uses Lettuce as the Redis connection factory.
•	Redis Template: Configures Serializers for key and value, ensuring data is stored as JSON.
Exception Handling Configuration
Global exception handling is configured in ApplicationExceptionConfiguration.java, which extends ResponseEntityExceptionHandler:
•	It captures various exceptions and returns structured responses.

The base URL for the API is http://localhost:8083/api.
Endpoints
Event Endpoints
1.	Authentication:
o	POST /api/auth/register: Register a new user.
o	POST /api/auth/login: Log in with credentials to receive a JWT token.
2.	Events:
o	GET /api/events: List all events with pagination (available for all users).
o	GET /api/events/search: Search events by name and date range.
o	POST /api/events/add: Add a new event (admin-restricted).
o	PUT /api/events/edit/{id}: Edit an existing event (admin-restricted).
o	DELETE /api/events/remove/{id}: Remove an event (admin-restricted).




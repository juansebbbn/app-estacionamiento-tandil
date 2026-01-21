APP-ESTACIONAMIENTO-TANDIL // PARKING-TANDIL-APP:

Purpose of the App:
This application is designed to simplify and improve access to paid parking zones and related features. It is a non-production project developed specifically for the purpose of improving programming skills.
The system manages the lifecycle of a parking session: from the initial CRUD of users and vehicles to the real-time tracking of parking duration. When a user is in a designated parking zone, they can start a session, and the app tracks the elapsed time. Upon termination, the system calculates and deducts the corresponding amount from the user's balance.
Technical Stack

Technologies:
Backend: Java Spring Boot, Spring Security, JWT
Frontend: React Native
Database/State: JPA/Hibernate, Context API

Features,
Current Implementation:
- Identity Management: Full CRUD for cars and users. A many-to-many relationship exists between users and vehicles.
- Session Management: Users can create an active session to authorize their parking.
- Automated Billing: Users can end a session, triggered manually or via frontend logic, which calculates and processes the payment.
- Balance Inquiry: Real-time checking of available user credit.
- Enforcement (Inspector Role): Users with the Inspector role can verify if a specific vehicle has an active session. If not, a sanction is applied.
- Security: Authentication and authorization managed via JWT and Spring Security.
- Geofencing: Uses GPS data to determine if the user's current coordinates correspond to a paid parking zone.
- Occupation Map: A visualization of all active parking sessions based on backend endpoints.
- Smart Termination: A frontend feature that monitors GPS movement to automatically suggest or trigger the end of a session.
- Global Exception Handling: Implementation of a centralized handler to manage application errors without exposing internal Java exceptions.

Future Features:
- Notification System: Alerts for users nearing an infraction (e.g., low balance) or exceeding a specific time limit.

API Endpoints

Authentication
POST /api/auth/login
POST /api/auth/register
POST /api/auth/refreshtoken

User Management
GET /api/users
GET /api/users/balance

Vehicle Management
POST /api/users/{userId}/vehicles
GET /api/users/{userId}/vehicles
DELETE /api/vehicles/{vehicleId}

Parking Operations
POST /api/parking/start/{patent}
POST /api/parking/finish/{patent}
GET /api/parking/active/{patent} (Inspector access only)
GET /api/parking/hasActiveSession

Zones and Infrastructure
POST /api/parkingzone/is_at_pz
GET /api/lines/get

Frontend Architecture (React Native),
The project follows a modular architecture within the /src directory:

api/: Axios configurations and HTTP request services.
components/: Reusable UI atoms and molecules.
screens/: High-level view containers.
navigation/: Routing logic (AuthStack and MainStack).
context/: Global state management for sessions and JWT.
hooks/: Custom hooks for GPS tracking and authentication.
constants/: Static values like city boundaries and API URLs.
utils/: Helper functions for formatting and validation.
types/: TypeScript interfaces for data consistency.

Key Learnings and Best Practices:
Data Transfer Objects (DTOs): Using DTOs when passing data across system boundaries to avoid exposing database entities for security and abstraction.
Identity Security: Avoiding the use of IDs provided by the client to identify users. Relying on verified tokens ensures users cannot manipulate data to affect other accounts.
Session Logic Refactoring: Shifting from ID-based identification to username-based identification within the secure context to prevent unauthorized payment requests.
Dynamic Business Rules: Avoiding hardcoded rates on the client side. Fetching rates from the backend ensures that price updates are immediate and do not require app store redeployments.
Data Modeling: Prioritizing a robust initial data model to prevent complex refactoring during later stages of development.

Development Roadmap:
Payment Persistence: Implement logic to create a Payment object and history record whenever a session is finalized.
Inhibiting Vehicle Deletion: Prevent users from deleting a vehicle from their profile if that specific vehicle currently has an active, unpaid parking session.

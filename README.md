# app-estacionamiento-tandil
purpose of the app:

this app is designed to simplify and improve the way people access to paid parking and other features related. its a non production app, its only for improving programming skills.
it starts with the CRUD of vehicles and user. a user can have many vehicles and also a vehicle could be related with many users. when an user is at parking zone he can start the session and the app begin to count the minutes he is parked. when the session is out the app discounts the amount related to that time. users can add credit to their accounts via mercado pago. 
those are the basics features, next im gonna give detail of all features the app have.

actual features:
1. crud for cars and users.
2. can create an active session for not being sanctioned.
3. can end this session and pay the corresponding amount.
4. can look for balance of user.
5. users with role inspector are allow to check if a car has an active session. if he is not, sanction corresponds.
6. auth with jwt and spring security.

features that will be added:
1. geolocalization. the user can ask to the app if the zone where he is corresponds to a paid one (IN PROCESS)
2. notification system. alert the user if he is near to infraction (because of the amount limit) and others notifaction.
   probably when he or she overpassed some time with a session active.
3. automatic end. an smart feature that reads the change in the gps and finish the session.
4. ocupation map. map that shows all sessions actives.
5. global expection handler for not use java excep.

technologies: 
backend: java spring boot
frontend: not define

endpoints:

auth:
POST /api/auth/login //working
POST /api/auth/register //working
POST /api/auth/refreshtoken //working

user:
GET    /api/users/{id} // working
GET    /api/users/balance/{id}  //working

vehicle:
POST   /api/users/{userId}/vehicles  //working
GET    /api/users/{userId}/vehicles  //working
DELETE /api/vehicles/{vehicleId} //working

parking:
POST   /api/parking/start/{patent} //working
POST   /api/parking/finish/{patent} //working
GET    /api/parking/active/{patent}  //working. this endpoint could only be access by inspectors.
GET    /api/parking/history/{patent} //not implemented


// this features will be added in future.
payment:
POST   /api/payments 
GET    /api/users/{userId}/payments

lines:
GET /api/lines/get //working
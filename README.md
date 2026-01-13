# app-estacionamiento-tandil
app designed for improving accesibility of parking time in tandil

technologies: 

backend: java spring boot
frontend: not define

endpoints:

auth:
POST /api/auth/login //not implemented
POST /api/auth/register //not implemented
POST /api/auth/refreshtoken //not implemented

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

payment:
POST   /api/payments 
GET    /api/users/{userId}/payments

lines:
GET /api/lines/get //working

purpose of the app:

this app is designed to simplify and improve the way people access to paid parking and other features related. its a non production app, its only for improving programming skills.
it starts with the CRUD of vehicles and user. a user can have many vehicles and also a vehicle could be related with many users. when an user is at parking zone he can start the session and the app begin to count the minutes he is parked. when the session is out the app discounts the amount related to that time. users can add credit to their accounts via mercado pago. 
the user auth is thinked with jwt arquitecture.
in future i would like to add features for look for bus lines and stationes near to the user.




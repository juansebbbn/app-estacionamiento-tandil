# app-estacionamiento-tandil
app designed for improving accesibility of parking time in tandil

endpoints:

user:
POST   /api/users/addUser //working but its only for test. in practice we have to do an auth system.
GET    /api/users/{id} // working
GET    /api/users/balance/{id}  //working

vehicle:
POST   /api/users/{userId}/vehicles
GET    /api/users/{userId}/vehicles
DELETE /api/vehicles/{vehicleId}

parking:
POST   /api/parking/start/{patent}
POST   /api/parking/finish/{patent}
GET    /api/parking/active/{patent}  //este usaria el inspector para decidir si hay infraccion o no.
GET    /api/parking/history/{patent}

payment:
POST   /api/payments
GET    /api/users/{userId}/payments

lines:
GET /api/lines/get




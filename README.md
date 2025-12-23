# app-estacionamiento-tandil
app diseñada para mejorar la accesibilidad y facilidad del pago del estacionamiento en tandil

endpoints:

user:
POST   /api/users/register
GET    /api/users/{id}
GET    /api/users/{id}/balance

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




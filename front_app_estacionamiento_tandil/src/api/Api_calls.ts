import Api_client from "./Api_client";

export const parkingService = {
  checkUserLocation: async (lat: number, lng: number) => {
    const response = await Api_client.post("/api/parkingzone/is_at_pz", {
      latitude: lat,
      longitude: lng,
    });
    return response.data;
  },

  startSession: async (patent: string, coordinates: {latitude: number, longitude: number}) => {
    const response = await Api_client.post(`api/parking/start/${patent}`, {
      latitude: coordinates.latitude,
      longitude: coordinates.longitude,
    });

    console.log("Iniciando estacionamiento para:", patent);
    return response.data;
  },

  finishSession: async (sessionId: number) => {
    const response = await Api_client.post(`api/parking/finish/${sessionId}`);
    console.log("Finalizando estacionamiento para:", sessionId);
    return response.data;
  },

  hasSessionActive: async()=>{
    const response = await Api_client.get(`api/parking/hasSession`)
    return response.data;
  }
};

export const vehicleService = {
  addVehicle: async (car: { patent: String; type: String }) => {
    const response = await Api_client.post(`/api/vehicle/add`, {
      patent: car.patent,
      type: car.type,
    });
    return response.data;
  },

  deleteVehicle: async (patent: String) => {
    const response = await Api_client.delete(
      `/api/vehicle/delete/${patent}`,
    );
    return response.data;
  },

  getAllVehicles: async () => {
    const response = await Api_client.get(`/api/vehicle/get_all`);
    return response.data;
  },
};

export const userService = {
  getByUsername: async () => {
    const response = await Api_client.get(`/api/users/getuser`);
    return response.data;
  },
};

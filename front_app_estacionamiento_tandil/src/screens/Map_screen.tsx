import React, { useEffect, useState } from "react";
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  Modal,
  Dimensions,
  ActivityIndicator,
} from "react-native";
import MapView, { Polygon, Marker, PROVIDER_GOOGLE } from "react-native-maps";
import * as Location from "expo-location";
import { MaterialCommunityIcons } from "@expo/vector-icons";
import { parkingService } from "../api/Api_calls";

export const MapZoneScreen = () => {
  const [userLocation, setUserLocation] = useState<any>(null);
  const [loading, setLoading] = useState(false);
  const [zone, setZone] = useState<boolean>(false);
  const [showModal, setShowModal] = useState(false);

  const parkingZone = [
    { latitude: -37.32587084566907, longitude: -59.14712252110594 },
    { latitude: -37.32048981938821, longitude: -59.13287026708019 },
    { latitude: -37.33137933550977, longitude: -59.12647922805995 },
    { latitude: -37.33671080217652, longitude: -59.14064160627708 },
  ];

  const fetchUserLocation = async () => {
    try {
      let { status } = await Location.requestForegroundPermissionsAsync();
      if (status !== "granted") return;
      let location = await Location.getCurrentPositionAsync({});
      setUserLocation(location.coords);
    } catch (error) {
      console.log("Error obteniendo ubicación:", error);
    }
  };

  const fetchUserZone = async () => {
    setLoading(true);
    try {
      let { status } = await Location.requestForegroundPermissionsAsync();
      if (status !== "granted") {
        alert("Se necesitan permisos de ubicación");
        return;
      }
      
      let location = await Location.getCurrentPositionAsync({});
      setUserLocation(location.coords);

      const data = await parkingService.checkUserLocation(
        location.coords.latitude,
        location.coords.longitude
      );
      
      setZone(data); 
      setShowModal(true);
    } catch (error) {
      console.error("Error al consultar zona:", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchUserLocation();
  }, []);

  return (
    <View style={styles.container}>

      <MapView
        provider={PROVIDER_GOOGLE}
        style={styles.map}
        initialRegion={{
          latitude: -37.328,
          longitude: -59.132,
          latitudeDelta: 0.02,
          longitudeDelta: 0.02,
        }}
      >
        <Polygon
          coordinates={parkingZone}
          fillColor="rgba(231, 76, 60, 0.2)"
          strokeColor="#e74c3c"
          strokeWidth={2}
        />

        {userLocation && (
          <Marker
            coordinate={userLocation}
            title="Tu ubicación"
            pinColor="blue"
          />
        )}
      </MapView>

      <View style={styles.uiContainer}>
        <TouchableOpacity
          style={[styles.button, loading && { opacity: 0.7 }]}
          onPress={fetchUserZone}
          disabled={loading}
        >
          {loading ? (
            <ActivityIndicator color="white" />
          ) : (
            <MaterialCommunityIcons name="cloud-sync" size={24} color="white" />
          )}
          <Text style={styles.buttonText}>
            {loading ? "Consultando..." : "Verificar zona"}
          </Text>
        </TouchableOpacity>
      </View>

      <Modal
        animationType="slide"
        transparent={true}
        visible={showModal}
        onRequestClose={() => setShowModal(false)}
      >
        <View style={styles.modalOverlay}>
          <View style={styles.modalContent}>
            <MaterialCommunityIcons 
              name={zone ? "check-circle" : "alert-circle"} 
              size={60} 
              color={zone ? "#2ecc71" : "#e74c3c"} 
            />
            
            <Text style={styles.modalTitle}>
              {zone ? "Zona Céntrica" : "Fuera de Radio"}
            </Text>
            
            <Text style={styles.modalDescription}>
              {zone 
                ? "Te encuentras dentro del radio de estacionamiento medido de Tandil. No olvides iniciar tu sesión." 
                : "En esta ubicación el estacionamiento es gratuito o no se encuentra regulado por el sistema."}
            </Text>

            <TouchableOpacity
              style={[styles.modalButton, { backgroundColor: zone ? "#2ecc71" : "#e74c3c" }]}
              onPress={() => setShowModal(false)}
            >
              <Text style={styles.buttonText}>Entendido</Text>
            </TouchableOpacity>
          </View>
        </View>
      </Modal>
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1 },
  map: {
    width: Dimensions.get("window").width,
    height: Dimensions.get("window").height,
  },
  uiContainer: {
    position: "absolute",
    bottom: 50,
    left: 20,
    right: 20,
    alignItems: "center",
  },
  button: {
    flexDirection: "row",
    backgroundColor: "#0055b3",
    paddingVertical: 18,
    paddingHorizontal: 30,
    borderRadius: 15,
    alignItems: "center",
    gap: 10,
    elevation: 8,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.3,
    shadowRadius: 4.65,
  },
  buttonText: { color: "white", fontWeight: "bold", fontSize: 16 },
  
  modalOverlay: {
    flex: 1,
    backgroundColor: "rgba(0,0,0,0.6)",
    justifyContent: "center",
    alignItems: "center",
  },
  modalContent: {
    width: "85%",
    backgroundColor: "white",
    borderRadius: 25,
    padding: 30,
    alignItems: "center",
    elevation: 10,
  },
  modalTitle: {
    fontSize: 24,
    fontWeight: "bold",
    marginVertical: 15,
    color: "#333",
  },
  modalDescription: {
    fontSize: 16,
    color: "#666",
    textAlign: "center",
    marginBottom: 25,
    lineHeight: 22,
  },
  modalButton: {
    paddingVertical: 14,
    borderRadius: 12,
    width: "100%",
    alignItems: "center",
  },
});
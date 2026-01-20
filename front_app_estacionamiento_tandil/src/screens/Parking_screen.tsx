import React, { useEffect, useState } from "react";
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  ScrollView,
  ActivityIndicator,
} from "react-native";
import {
  FontAwesome5,
  MaterialCommunityIcons,
  Ionicons,
} from "@expo/vector-icons";
import { userService, vehicleService, parkingService } from "../api/Api_calls";
import { useAuth } from "../context/AuthContext";
import * as Location from "expo-location";

export const ParkingScreen = ({ navigation }: any) => {
  const [vehicles, setVehicles] = useState<any[]>([]);
  const [userData, setUserData] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [zone, setZone] = useState<boolean>(false);
  const [activeSession, setParkingSession] = useState<boolean>(false);
  const [patent, setPatent] = useState<string>("");
  const [time, setTime] = useState<number>(1);
  const [sessionId, setSesionId] = useState<number>(1);

  const fetchUserData = async () => {
    try {
      const data = await userService.getByUsername();
      setUserData(data);
    } catch (error) {
      console.error(error);
    }
  };

  const fetchUserZone = async () => {
    try {
      let { status } = await Location.requestForegroundPermissionsAsync();
      if (status !== "granted") return;
      let location = await Location.getCurrentPositionAsync({});
      const data = await parkingService.checkUserLocation(
        location.coords.latitude,
        location.coords.longitude,
      );
      setZone(data);
    } catch (error) {
      console.error(error);
    }
  };

  const fetchVehiclesData = async () => {
    try {
      const data = await vehicleService.getAllVehicles();
      setVehicles(data);
      if (data.length > 0) setPatent(data[0].patent);
    } catch (error) {
      console.error(error);
    } finally {
      setLoading(false);
    }
  };

  const startParkingSession = async () => {
    try {
      let { status } = await Location.requestForegroundPermissionsAsync();
      if (status !== "granted") return;
      let location = await Location.getCurrentPositionAsync({});
      const response = await parkingService.startSession(
        patent,
        location.coords,
      );
      setSesionId(response.parkingId);
      setParkingSession(true);
    } catch (error) {
      console.log(error);
    }
  };

  const finishParkingSession = async () => {
    try {
      await parkingService.finishSession(sessionId);
      setSesionId(0);
      setParkingSession(false);
    } catch (error) {
      console.log(error);
    }
  };

  useEffect(() => {
    console.log(patent);
    console.log(zone);
    fetchVehiclesData();
    fetchUserZone();
    fetchUserData();
    console.log("session id " + sessionId);
  }, []);

  if (loading)
    return (
      <View style={styles.center}>
        <ActivityIndicator size="large" color="#EAB308" />
      </View>
    );

  return (
    <ScrollView
      style={styles.container}
      contentContainerStyle={{ paddingBottom: 40 }}
    >
      <Text style={styles.mainTitle}>Iniciar Estacionamiento</Text>

      <View style={styles.section}>
        <Text style={styles.label}>Vehículo seleccionado:</Text>
        {vehicles.map((item) => (
          <TouchableOpacity
            key={item.patent}
            style={[
              styles.whiteCard,
              patent === item.patent && styles.selectedCard,
            ]}
            onPress={() => setPatent(item.patent)}
          >
            <View style={styles.row}>
              <FontAwesome5 name="car" size={18} color="#0056b3" />
              <Text style={styles.cardTextBold}>{item.patent}</Text>
            </View>
            <Ionicons name="chevron-forward" size={20} color="#ccc" />
          </TouchableOpacity>
        ))}
      </View>

      <View style={styles.section}>
        <Text style={styles.label}>Zona detectada:</Text>
        <View style={styles.zoneCard}>
          <View style={styles.redIndicator} />
          <View style={styles.row}>
            <MaterialCommunityIcons
              name="map-marker"
              size={24}
              color="#E11D48"
            />
            <View style={styles.textColumn}>
              <Text style={styles.cardTextBold}>
                {zone ? "Zona Céntrica - Tandil" : "Fuera de Radio"}
              </Text>
              <Text style={styles.cardSubtext}>
                {zone
                  ? "Dentro de las 4 avenidas"
                  : "Estacionamiento no medido"}
              </Text>
            </View>
          </View>
        </View>
      </View>

      <View style={styles.section}>
        <Text style={styles.label}>¿Cuánto tiempo vas a estar? (Horas)</Text>
        <View style={styles.timeSelector}>
          <TouchableOpacity
            style={styles.roundBtn}
            onPress={() => setTime(Math.max(1, time - 1))}
          >
            <Text style={styles.roundBtnText}>-</Text>
          </TouchableOpacity>
          <Text style={styles.timeValue}>{time} h</Text>
          <TouchableOpacity
            style={styles.roundBtn}
            onPress={() => setTime(time + 1)}
          >
            <Text style={styles.roundBtnText}>+</Text>
          </TouchableOpacity>
        </View>
      </View>

      <View style={styles.costPanel}>
        <Text style={styles.costLabel}>Costo estimado:</Text>
        <Text style={styles.costValue}>${(time * 150).toFixed(2)}</Text>
        <Text style={styles.balanceLabel}>
          Saldo disponible:{" "}
          <Text style={styles.yellowText}>${userData?.balance || "0.00"}</Text>
        </Text>
      </View>

      <TouchableOpacity
        style={styles.mainBtn}
        onPress={activeSession ? finishParkingSession : startParkingSession}
      >
        <Text style={styles.mainBtnText}>
          {activeSession
            ? "Finalizar estacionamiento"
            : "Iniciar estacionamiento"}
        </Text>
      </TouchableOpacity>

      <View>
        {activeSession ? (
          <Text style={styles.successText}>
            Sesión iniciada para vehículo con patente: {patent}
          </Text>
        ) : (
          <Text style={styles.infoText}>
            No hay ninguna sesión activa en este momento.
          </Text>
        )}
      </View>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#FDFBF7",
    padding: 25,
    marginBottom: 50,
  },
  center: { flex: 1, justifyContent: "center", alignItems: "center" },
  mainTitle: {
    fontSize: 18,
    fontWeight: "700",
    color: "#333",
    marginBottom: 15,
    marginTop: 20,
  },
  section: { marginBottom: 25 },
  label: { fontSize: 15, fontWeight: "600", color: "#444", marginBottom: 12 },
  whiteCard: {
    backgroundColor: "#FFF",
    borderRadius: 12,
    padding: 8,
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    borderWidth: 1,
    borderColor: "#E5E7EB",
    margin: 2,
    elevation: 2,
  },
  selectedCard: { borderColor: "#0056b3", backgroundColor: "#F0F7FF" },
  zoneCard: {
    backgroundColor: "#FFF",
    borderRadius: 12,
    flexDirection: "row",
    alignItems: "center",
    overflow: "hidden", 
    borderWidth: 1,
    borderColor: "#E5E7EB",
    elevation: 2,
  },
  redIndicator: { width: 5, height: "100%", backgroundColor: "#E11D48" },
  row: { flexDirection: "row", alignItems: "center", padding: 15 },
  textColumn: { marginLeft: 12 },
  cardTextBold: {
    fontSize: 13,
    fontWeight: "bold",
    color: "#000",
    marginLeft: 8,
  },
  cardSubtext: { fontSize: 14, color: "#6B7280" },
  timeSelector: {
    flexDirection: "row",
    justifyContent: "center",
    alignItems: "center",
    marginTop: 10,
  },
  timeValue: { fontSize: 19, fontWeight: "bold", marginHorizontal: 30 },
  roundBtn: {
    width: 50,
    height: 50,
    borderRadius: 25,
    backgroundColor: "#FACC15",
    justifyContent: "center",
    alignItems: "center",
  },
  roundBtnText: { fontSize: 28, fontWeight: "bold", color: "#333" },
  costPanel: {
    backgroundColor: "#333",
    borderRadius: 15,
    padding: 25,
    alignItems: "center",
  },
  costLabel: { color: "#999", fontSize: 16 },
  costValue: {
    color: "#FFF",
    fontSize: 22,
    fontWeight: "bold",
    marginVertical: 5,
  },
  balanceLabel: { color: "#FFF", fontSize: 14 },
  yellowText: { color: "#FACC15", fontWeight: "bold" },
  mainBtn: {
    backgroundColor: "#FEDA05",
    width: "100%",
    height: 70,
    borderRadius: 15,
    justifyContent: "center",
    alignItems: "center",
    marginBottom: 20,
    elevation: 3,
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    flexDirection: "column",
    gap: 8,
    marginTop: 5,
  },
  mainBtnText: { color: "#000000", fontWeight: "bold", fontSize: 14 },
  successText:{ color: "green"},
  infoText: {color: "black"}
});

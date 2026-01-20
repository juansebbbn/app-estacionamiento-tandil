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
import * as Location from "expo-location";

export const ParkingScreen = ({ navigation }: any) => {
  const [vehicles, setVehicles] = useState<any[]>([]);
  const [userData, setUserData] = useState<any>(null);
  const [loading, setLoading] = useState(true);

  const [zone, setZone] = useState<boolean>(false);
  const [activeSession, setParkingSession] = useState<boolean>(false);
  const [patent, setPatent] = useState<string>("");
  const [activePatent, setActivePatent] = useState<string>("");
  const [time, setTime] = useState<number>(1);
  const [sessionId, setSesionId] = useState<number>(0);

  const fetchUserData = async () => {
    try {
      const data = await userService.getByUsername();
      setUserData(data);
    } catch (error) {
      console.error("Error UserData:", error);
    }
  };

  const hasSessionActive = async () => {
    try {
      const data = await parkingService.hasSessionActive();
      if (data) {
        setSesionId(data.parkingId);
        setPatent(data.patent);
        setParkingSession(true);
        setActivePatent(data.patent);
        console.log(data.parkingId);
        console.log("asd" + activeSession);
      }
    } catch (error) {
      console.log("Sin sesión activa previa");
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
      console.error("Error Zone:", error);
    }
  };

  const fetchVehiclesData = async () => {
    try {
      const data = await vehicleService.getAllVehicles();
      setVehicles(data);
      if (data.length > 0) setPatent(data[0].patent);
    } catch (error) {
      console.error("Error Vehicles:", error);
    } finally {
      setLoading(false);
    }
  };

  const startParkingSession = async () => {
    try {
      let { status } = await Location.requestForegroundPermissionsAsync();
      if (status !== "granted") return;
      if (patent !== null) {
        let location = await Location.getCurrentPositionAsync({});
        const response = await parkingService.startSession(
          patent,
          location.coords,
        );
        setSesionId(response.parkingId);
        setParkingSession(true);
        setActivePatent(patent);
        fetchUserData();
      }
    } catch (error) {
      console.log("Error Start Session:", error);
    }
  };

  const finishParkingSession = async () => {
    try {
      await parkingService.finishSession(sessionId);
      setSesionId(0);
      setParkingSession(false);
      setActivePatent("");
      fetchUserData();
    } catch (error) {
      console.log("Error Finish Session:", error);
    }
  };

  useEffect(() => {
    fetchVehiclesData();
    fetchUserZone();
    fetchUserData();
    hasSessionActive();
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
      contentContainerStyle={{ paddingBottom: 60 }}
    >
      <Text style={styles.mainTitle}>Iniciar Estacionamiento</Text>

      <View style={styles.statusSection}>
        {activeSession ? (
          <View style={[styles.statusBanner, styles.activeBanner]}>
            <MaterialCommunityIcons
              name="check-decagram"
              size={28}
              color="#065F46"
            />
            <View style={styles.statusTextContainer}>
              <Text style={styles.statusTitleActive}>
                Estacionamiento Activo
              </Text>
              <Text style={styles.statusSubtitleActive}>
                Vehículo patente:{" "}
                <Text style={{ fontWeight: "bold" }}>{activePatent}</Text>
              </Text>
            </View>
          </View>
        ) : (
          <View style={[styles.statusBanner, styles.inactiveBanner]}>
            <MaterialCommunityIcons
              name="information-outline"
              size={28}
              color="#1E3A8A"
            />
            <View style={styles.statusTextContainer}>
              <Text style={styles.statusTitleInactive}>
                Sin sesiones activas
              </Text>
              <Text style={styles.statusSubtitleInactive}>
                Seleccioná los datos para comenzar.
              </Text>
            </View>
          </View>
        )}
      </View>

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
            <View style={styles.rowItem}>
              <FontAwesome5 name="car" size={18} color="#0056b3" />
              <Text style={styles.cardTextBold}>{item.patent}</Text>
            </View>
            <Ionicons
              name={
                patent === item.patent ? "radio-button-on" : "radio-button-off"
              }
              size={20}
              color={patent === item.patent ? "#0056b3" : "#ccc"}
            />
          </TouchableOpacity>
        ))}
      </View>

      <View style={styles.section}>
        <Text style={styles.label}>Zona detectada:</Text>
        <View style={styles.zoneCard}>
          <View
            style={[
              styles.indicator,
              { backgroundColor: zone ? "#E11D48" : "#9CA3AF" },
            ]}
          />
          <View style={styles.rowPadding}>
            <MaterialCommunityIcons
              name="map-marker-radius"
              size={24}
              color={zone ? "#E11D48" : "#6B7280"}
            />
            <View style={styles.textColumn}>
              <Text style={styles.zoneTitle}>
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
        <Text style={styles.label}>¿Cuánto tiempo vas a estar?</Text>
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
        style={[
          styles.mainBtn,
          activeSession ? styles.btnDanger : styles.btnSuccess,
        ]}
        onPress={activeSession ? finishParkingSession : startParkingSession}
      >
        <MaterialCommunityIcons
          name={activeSession ? "stop-circle-outline" : "play-circle-outline"}
          size={24}
          color="black"
        />
        <Text style={styles.mainBtnText}>
          {activeSession
            ? "FINALIZAR ESTACIONAMIENTO"
            : "INICIAR ESTACIONAMIENTO"}
        </Text>
      </TouchableOpacity>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#FDFBF7", padding: 25 },
  center: { flex: 1, justifyContent: "center", alignItems: "center" },
  mainTitle: {
    fontSize: 20,
    fontWeight: "bold",
    color: "#333",
    marginVertical: 1,
  },
  section: { marginBottom: 25 },
  label: { fontSize: 16, fontWeight: "600", color: "#555", marginBottom: 10 },
  statusSection: { marginBottom: 20 },
  statusBanner: {
    flexDirection: "row",
    alignItems: "center",
    padding: 18,
    borderRadius: 15,
    borderWidth: 1,
  },
  activeBanner: { backgroundColor: "#ECFDF5", borderColor: "#A7F3D0" },
  inactiveBanner: { backgroundColor: "#F8FAFC", borderColor: "#E2E8F0" },
  statusTextContainer: { marginLeft: 12 },
  statusTitleActive: { fontSize: 16, fontWeight: "bold", color: "#065F46" },
  statusSubtitleActive: { fontSize: 14, color: "#059669" },
  statusTitleInactive: { fontSize: 16, fontWeight: "bold", color: "#475569" },
  statusSubtitleInactive: { fontSize: 14, color: "#64748B" },
  whiteCard: {
    backgroundColor: "#FFF",
    borderRadius: 12,
    padding: 15,
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
    borderWidth: 1,
    borderColor: "#E5E7EB",
    marginBottom: 8,
    elevation: 2,
  },
  selectedCard: { borderColor: "#0056b3", backgroundColor: "#F0F7FF" },
  rowItem: { flexDirection: "row", alignItems: "center" },
  cardTextBold: {
    fontSize: 18,
    fontWeight: "bold",
    color: "#333",
    marginLeft: 10,
  },
  zoneCard: {
    backgroundColor: "#FFF",
    borderRadius: 12,
    flexDirection: "row",
    overflow: "hidden",
    borderWidth: 1,
    borderColor: "#E5E7EB",
    elevation: 2,
  },
  indicator: { width: 6, height: "100%" },
  rowPadding: { flexDirection: "row", alignItems: "center", padding: 15 },
  textColumn: { marginLeft: 12 },
  zoneTitle: { fontSize: 16, fontWeight: "bold", color: "#000" },
  cardSubtext: { fontSize: 14, color: "#6B7280" },
  timeSelector: {
    flexDirection: "row",
    justifyContent: "center",
    alignItems: "center",
  },
  timeValue: { fontSize: 24, fontWeight: "bold", marginHorizontal: 25 },
  roundBtn: {
    width: 45,
    height: 45,
    borderRadius: 23,
    backgroundColor: "#FACC15",
    justifyContent: "center",
    alignItems: "center",
    elevation: 2,
  },
  roundBtnText: { fontSize: 24, fontWeight: "bold" },
  costPanel: {
    backgroundColor: "#2D2D2D",
    borderRadius: 16,
    padding: 20,
    alignItems: "center",
    marginTop: 10,
  },
  costLabel: { color: "#A0A0A0", fontSize: 14 },
  costValue: {
    color: "#FFF",
    fontSize: 32,
    fontWeight: "bold",
    marginVertical: 4,
  },
  balanceLabel: { color: "#D1D1D1", fontSize: 13 },
  yellowText: { color: "#FACC15", fontWeight: "bold" },
  mainBtn: {
    flexDirection: "row",
    height: 65,
    borderRadius: 15,
    justifyContent: "center",
    alignItems: "center",
    marginTop: 10,
    gap: 10,
    elevation: 4,
  },
  btnSuccess: { backgroundColor: "#FACC15" },
  btnDanger: { backgroundColor: "#FB7185" },
  mainBtnText: { color: "#000", fontWeight: "bold", fontSize: 15 },
});

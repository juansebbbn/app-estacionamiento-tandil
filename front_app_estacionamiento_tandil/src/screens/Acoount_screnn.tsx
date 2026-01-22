import React, { useEffect, useState } from "react";
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
} from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { MaterialIcons, FontAwesome5, Ionicons } from "@expo/vector-icons";
import { userService } from "../api/Api_calls";
import { useAuth } from '../context/AuthContext'

export const AccountScreen = ({ navigation }: any) => {
  const { signOut } = useAuth();
  const insets = useSafeAreaInsets();
  const [userData, setUserData] = useState<any>(null);

  useEffect(() => {
    fetchUserData();
  }, []);

  const fetchUserData = async () => {
    try {
      const data = await userService.getByUsername();
      setUserData(data);
    } catch (error) {
      console.error(error);
    }
  };

  return (
    <View style={[styles.container, { paddingTop: insets.top }]}>
      <ScrollView contentContainerStyle={styles.scrollContent}>
        <View style={styles.header}>
          <View style={styles.avatarContainer}>
            <MaterialIcons name="person" size={50} color="white" />
          </View>
          <Text style={styles.username}>{userData?.username}</Text>
          <Text style={styles.dniText}>DNI: {userData?.dni}</Text>
        </View>

        <View style={styles.balanceCard}>
          <Text style={styles.balanceLabel}>Saldo Disponible</Text>
          <Text style={styles.balanceAmount}>
            ${userData?.balance.toLocaleString("es-AR")}
          </Text>
          <TouchableOpacity style={styles.addFundsBtn}>
            <Ionicons name="add-circle" size={20} color="#0055b3" />
            <Text style={styles.addFundsText}>Cargar Saldo</Text>
          </TouchableOpacity>
        </View>

        <View style={styles.optionsContainer}>
          <Text style={styles.sectionTitle}>Mi Actividad</Text>

          <TouchableOpacity
            style={styles.optionItem}
            onPress={() => navigation.navigate("Vehicles")}
          >
            <FontAwesome5 name="car" size={18} color="#333" />
            <Text style={styles.optionText}>Mis Vehículos Registrados</Text>
            <Ionicons name="chevron-forward" size={18} color="#ccc" />
          </TouchableOpacity>

          <TouchableOpacity
            style={styles.optionItem}
            onPress={() => navigation.navigate("Payments")}
          >
            <MaterialIcons name="history" size={20} color="#333" />
            <Text style={styles.optionText}>Historial de Pagos</Text>
            <Ionicons name="chevron-forward" size={18} color="#ccc" />
          </TouchableOpacity>

          {/*

          <TouchableOpacity style={styles.optionItem} onPress={() => console.log("Editar")}>
            <Ionicons name="settings-outline" size={20} color="#333" />
            <Text style={styles.optionText}>Configuración de Perfil</Text>
            <Ionicons name="chevron-forward" size={18} color="#ccc" />
          </TouchableOpacity>

         */}
        </View>

        <TouchableOpacity
          style={styles.logoutButton}
          onPress={() => {
            signOut();
          }}
        >
          <MaterialIcons name="logout" size={20} color="#e74c3c" />
          <Text style={styles.logoutText}>Cerrar Sesión</Text>
        </TouchableOpacity>

        <Text style={styles.footerText}>
          Usuario desde: {userData?.signInReg}
        </Text>
      </ScrollView>
    </View>
  );
};

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: "#fbf9f0" },
  scrollContent: { padding: 20, alignItems: "center" },
  header: { alignItems: "center", marginBottom: 30 },
  avatarContainer: {
    width: 90,
    height: 90,
    borderRadius: 45,
    backgroundColor: "#0055b3",
    justifyContent: "center",
    alignItems: "center",
    marginBottom: 10,
    elevation: 4,
  },
  username: { fontSize: 22, fontWeight: "bold", color: "#333" },
  dniText: { fontSize: 14, color: "#666" },
  balanceCard: {
    width: "100%",
    backgroundColor: "#333",
    borderRadius: 20,
    padding: 25,
    alignItems: "center",
    marginBottom: 30,
    elevation: 5,
  },
  balanceLabel: { color: "#bbb", fontSize: 14, marginBottom: 5 },
  balanceAmount: { color: "#FEDA05", fontSize: 36, fontWeight: "bold" },
  addFundsBtn: {
    flexDirection: "row",
    backgroundColor: "#fff",
    paddingVertical: 8,
    paddingHorizontal: 15,
    borderRadius: 20,
    marginTop: 15,
    alignItems: "center",
    gap: 5,
  },
  addFundsText: { color: "#0055b3", fontWeight: "bold" },
  optionsContainer: { width: "100%", marginBottom: 30 },
  sectionTitle: {
    fontSize: 16,
    fontWeight: "bold",
    color: "#555",
    marginBottom: 15,
    marginLeft: 5,
  },
  optionItem: {
    flexDirection: "row",
    backgroundColor: "#fff",
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
    padding: 18,
    borderRadius: 12,
    alignItems: "center",
    marginBottom: 10,
    gap: 15,
  },
  optionText: { flex: 1, fontSize: 13, color: "#333" },
  logoutButton: {
    flexDirection: "row",
    alignItems: "center",
    gap: 10,
    padding: 15,
    marginBottom: 20,
  },
  logoutText: { color: "#e74c3c", fontSize: 16, fontWeight: "bold" },
  footerText: { color: "#999", fontSize: 12, marginBottom: 20 },
});

import React from "react";

import { SafeAreaView } from "react-native-safe-area-context";
import {
  MaterialIcons,
  FontAwesome5,
  MaterialCommunityIcons,
} from "@expo/vector-icons";

import { View, Text, StyleSheet, TouchableOpacity } from "react-native";

export const ParkingMenu = ({ navigation }: any) => {
  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.content}>
        <Text style={styles.greeting}>Menú Principal</Text>

        <TouchableOpacity
          style={styles.card}
          onPress={() => navigation.navigate("Estacionamiento")}
        >
          <MaterialCommunityIcons name="car-clock" size={32} color="black" />
          <Text style={styles.cardTitle}>Iniciar Estacionamiento</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={styles.card}
          onPress={() => navigation.navigate("Mapa")}
        >
          <MaterialIcons name="map" size={32} color="black" />
          <Text style={styles.cardTitle}>Ver mapa</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={styles.card}
          onPress={() => navigation.navigate("Autos")}
        >
          <FontAwesome5 name="car-side" size={28} color="black" />
          <Text style={styles.cardTitle}>Mis Autos</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={styles.card}
          onPress={() => navigation.navigate("Cuenta")}
        >
          <MaterialIcons name="person" size={42} color="black" />{" "}
          <Text style={styles.cardTitle}>Cuenta</Text>
        </TouchableOpacity>
      </View>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#fbf9f0",
  },
  content: {
    flex: 1,
    alignItems: "center",
    justifyContent: "center",
    paddingHorizontal: 30,
  },
  greeting: {
    fontSize: 22,
    fontWeight: "bold",
    marginBottom: 55,
  },
  card: {
    backgroundColor: "#FEDA05",
    width: "100%",
    height: 100,
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
  },
  cardTitle: {
    fontSize: 14,
    letterSpacing: 1,
    fontWeight: "500",
  },
});

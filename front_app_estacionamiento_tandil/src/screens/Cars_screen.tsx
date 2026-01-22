import React, { useState, useEffect } from "react";
import {
  View,
  Text,
  StyleSheet,
  TextInput,
  TouchableOpacity,
  FlatList,
} from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { FontAwesome5, MaterialIcons } from "@expo/vector-icons";
import { vehicleService } from "../api/Api_calls";

export const Cars_screen = () => {
  const insets = useSafeAreaInsets();
  const [patent, setPatent] = useState("");
  const [selectedType, setSelectedType] = useState<"A" | "M">("A");
  const [myVehicles, setVehicles] = useState([]);
  const [deletePatent, setDeletePatent] = useState("");

  useEffect(() => {
    fetchVehiclesData();
  }, []);

  const fetchVehiclesData = async () => {
    try {
      const data = await vehicleService.getAllVehicles();
      setVehicles(data || []);
    } catch (error) {
      console.error("Error fetching cars data:", error);
    }
  };

  const addVehicle = async () => {
    if (!patent.trim()) return;
    try {
      const vehicle = { patent: patent, type: selectedType };
      await vehicleService.addVehicle(vehicle);
      setPatent("");
      fetchVehiclesData();
    } catch (error) {
      console.error(error);
    }
  };

  const deleteVehicle = async () => {
    try {
      await vehicleService.deleteVehicle(deletePatent);
      fetchVehiclesData();
    } catch (error) {
      console.error(error);
    }
  };

  const renderVehicleItem = ({ item }: any) => (
    <View style={styles.carItem}>
      <FontAwesome5
        name={item.type === "A" ? "car" : "motorcycle"}
        size={20}
        color="black"
      />
      <Text style={styles.carText}>{item.patent}</Text>
      <TouchableOpacity
        onPress={() => {
          setDeletePatent(item.patent);
          deleteVehicle();
        }}
      >
        <MaterialIcons name="delete" size={24} color="red" />
      </TouchableOpacity>
    </View>
  );

  return (
    <View style={[styles.container, { paddingTop: insets.top }]}>
      <View style={styles.content}>
        <Text style={styles.headerTitle}>Mis autos:</Text>
        <View style={styles.listContainer}>
          <FlatList
            data={myVehicles}
            keyExtractor={(item, index) => index.toString()}
            renderItem={renderVehicleItem}
            ListEmptyComponent={
              <Text style={styles.emptyText}>
                No tenés autos agregados todavía.
              </Text>
            }
          />
        </View>

        <Text style={styles.sectionTitle}>Agregá un auto:</Text>

        <TextInput
          style={styles.input}
          placeholder="Patente"
          placeholderTextColor="#888"
          value={patent}
          onChangeText={setPatent}
          autoCapitalize="characters"
        />

        <View style={styles.selectorContainer}>
          <TouchableOpacity
            style={[
              styles.typeButton,
              selectedType === "A" && styles.typeButtonSelected,
            ]}
            onPress={() => setSelectedType("A")}
          >
            <FontAwesome5
              name="car"
              size={20}
              color={selectedType === "A" ? "white" : "black"}
            />
            <Text
              style={[
                styles.typeText,
                selectedType === "A" && styles.typeTextSelected,
              ]}
            >
              Auto
            </Text>
          </TouchableOpacity>

          <TouchableOpacity
            style={[
              styles.typeButton,
              selectedType === "M" && styles.typeButtonSelected,
            ]}
            onPress={() => setSelectedType("M")}
          >
            <FontAwesome5
              name="motorcycle"
              size={20}
              color={selectedType === "M" ? "white" : "black"}
            />
            <Text
              style={[
                styles.typeText,
                selectedType === "M" && styles.typeTextSelected,
              ]}
            >
              Moto
            </Text>
          </TouchableOpacity>
        </View>

        <TouchableOpacity style={styles.addButton} onPress={addVehicle}>
          <Text style={styles.addButtonText}>Agregar</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: "#fff",
  },
  header: {
    flexDirection: "row",
    alignItems: "center",
    paddingHorizontal: 15,
    paddingVertical: 10,
    borderBottomWidth: 1,
    borderBottomColor: "#eee",
  },
  title: {
    fontSize: 22,
    fontWeight: "500",
    marginLeft: 20,
  },
  content: {
    flex: 1,
    paddingHorizontal: 30,
    paddingTop: 20,
  },
  headerTitle: {
    fontSize: 18,
    fontWeight: "bold",
    marginBottom: 10,
  },
  listContainer: {
    height: 250,
    backgroundColor: "#ffffff",
    borderRadius: 10,
    padding: 15,
    marginBottom: 30,
    borderWidth: 1,
    borderColor: "#ccc",
    justifyContent: "center",
  },
  carItem: {
    flexDirection: "row",
    backgroundColor: "#f9f9f9",
    padding: 12,
    borderRadius: 8,
    marginBottom: 10,
    alignItems: "center",
  },
  carText: {
    fontWeight: "bold",
    flex: 1,
    marginLeft: 15,
  },
  input: {
    backgroundColor: "#E0E0E0",
    padding: 12,
    borderRadius: 5,
    textAlign: "center",
    marginBottom: 20,
    fontSize: 16,
  },
  sectionTitle: {
    textAlign: "center",
    marginBottom: 15,
    fontSize: 16,
    fontWeight: "500",
  },
  selectorContainer: {
    flexDirection: "row",
    justifyContent: "space-between",
    marginBottom: 30,
  },
  typeButton: {
    flex: 0.48,
    flexDirection: "row",
    backgroundColor: "#E0E0E0",
    padding: 12,
    borderRadius: 5,
    alignItems: "center",
    justifyContent: "center",
    gap: 10,
  },
  typeButtonSelected: {
    backgroundColor: "#0055b3",
  },
  typeText: {
    fontWeight: "bold",
    fontSize: 14,
    color: "black",
  },
  typeTextSelected: {
    color: "white",
  },
  addButton: {
    backgroundColor: "#FEDA05",
    padding: 15,
    borderRadius: 5,
    alignItems: "center",
  },
  addButtonText: {
    fontWeight: "bold",
    fontSize: 16,
  },
  emptyText: {
    textAlign: "center",
    color: "#888",
    fontSize: 16,
    lineHeight: 22,
  },
});

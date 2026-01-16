import React, { useState } from "react";
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

interface Car {
  id: string;
  patent: string;
  type: "Auto" | "Moto";
}

export const MyCarsScreen = () => {
  const insets = useSafeAreaInsets();
  const [patent, setPatent] = useState("");
  const [selectedType, setSelectedType] = useState<"Auto" | "Moto">("Auto");
  const [myCars, setMyCars] = useState<Car[]>([]);

  const addCar = () => {
    if (!patent) return;
    const newCar: Car = {
      id: Math.random().toString(),
      patent: patent.toUpperCase(),
      type: selectedType,
    };
    setMyCars([...myCars, newCar]);
    setPatent("");
  };

  return (
    <View
      style={[
        styles.container,
        { paddingTop: insets.top, paddingBottom: insets.bottom }, // Aplicamos márgenes dinámicos
      ]}
    >
      <View style={styles.content}>
        <Text style={styles.headerTitle}>Mis autos:</Text>

        <View style={styles.listContainer}>
          {myCars.length > 0 ? (
            <FlatList
              data={myCars}
              keyExtractor={(item) => item.id}
              renderItem={({ item }) => (
                <View style={styles.carItem}>
                  <FontAwesome5
                    name={item.type === "Auto" ? "car" : "motorcycle"}
                    size={20}
                    color="black"
                  />
                  <Text style={styles.carText}>{item.patent}</Text>
                  <TouchableOpacity
                    onPress={() =>
                      setMyCars(myCars.filter((c) => c.id !== item.id))
                    }
                  >
                    <MaterialIcons name="delete" size={20} color="red" />
                  </TouchableOpacity>
                </View>
              )}
            />
          ) : (
            <Text style={styles.emptyText}>
              No tenés autos agregados todavía.
            </Text>
          )}
        </View>

        <Text style={styles.sectionTitle}>Agregá un auto:</Text>

        <TextInput
          style={styles.input}
          placeholder="Patente"
          autoCapitalize="characters"
          value={patent}
          onChangeText={setPatent}
        />

        <View style={styles.selectorContainer}>

          <TouchableOpacity
            style={[
              styles.typeButton,
              selectedType === "Auto" && styles.typeButtonSelected,
            ]}
            onPress={() => setSelectedType("Auto")}
          >
            <FontAwesome5
              name="car"
              size={20}
              color={selectedType === "Auto" ? "white" : "black"}
            />
            <Text
              style={[
                styles.typeText,
                selectedType === "Auto" && styles.typeTextSelected,
              ]}
            >
              Auto
            </Text>
          </TouchableOpacity>

          <TouchableOpacity
            style={[
              styles.typeButton,
              selectedType === "Moto" && styles.typeButtonSelected,
            ]}
            onPress={() => setSelectedType("Moto")}
          >
            <FontAwesome5
              name="motorcycle"
              size={20}
              color={selectedType === "Moto" ? "white" : "black"}
            />
            <Text
              style={[
                styles.typeText,
                selectedType === "Moto" && styles.typeTextSelected,
              ]}
            >
              Moto
            </Text>
          </TouchableOpacity>
        </View>

        <TouchableOpacity style={styles.addButton} onPress={addCar}>
          <Text style={styles.addButtonText}>Agregar</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  typeText: {
    fontWeight: "bold",
    fontSize: 14,
    color: "black", 
  },
  typeTextSelected: {
    color: "white",
    fontWeight: "bold",
  },
  container: {
    flex: 1,
    backgroundColor: "#fff",
  },
  content: {
    flex: 1,
    paddingHorizontal: 30,
    paddingTop: 20,
  },
  headerTitle: { fontSize: 18, fontWeight: "bold", marginBottom: 10 },
  listContainer: {
    height: 250,
    backgroundColor: "#ffffff",
    borderRadius: 10,
    padding: 15,
    marginBottom: 30,
    borderWidth: 1,
    borderColor: "#898989",
  },
  carItem: {
    flexDirection: "row",
    backgroundColor: "white",
    padding: 10,
    borderRadius: 5,
    marginBottom: 10,
    alignItems: "center",
  },
  carText: { fontWeight: "bold", flex: 1, marginLeft: 10 },
  input: {
    backgroundColor: "#E0E0E0",
    padding: 12,
    borderRadius: 5,
    textAlign: "center",
    marginBottom: 20,
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
  typeButtonSelected: { backgroundColor: "#0055b3" },
  addButton: {
    backgroundColor: "#FEDA05",
    padding: 15,
    borderRadius: 5,
    alignItems: "center",
  },
  addButtonText: { fontWeight: "bold" },
  emptyText: { textAlign: "center", marginTop: 100, color: "#888" },
  sectionTitle: { textAlign: "center", marginBottom: 15 },
});

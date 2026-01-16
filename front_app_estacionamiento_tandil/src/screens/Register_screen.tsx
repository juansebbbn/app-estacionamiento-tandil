import React, { useState } from "react";
import {
  View,
  Text,
  StyleSheet,
  TextInput,
  Alert,
  ScrollView,
} from "react-native";
import { CustomButton } from "../components/Custom_button";

export const RegisterScreen = ({ navigation }: any) => {
  const [dni, setDni] = useState("");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");

  const handleRegister = () => {
    if (!dni || !username || !password || !confirmPassword) {
      Alert.alert("Error", "Todos los campos son obligatorios");
      return;
    }

    if (password !== confirmPassword) {
      Alert.alert("Error", "Las contraseñas no coinciden");
      return;
    }

    console.log("Registrando usuario:", { dni, username });
    // Aquí irá la llamada POST a tu endpoint de registro
    Alert.alert("Éxito", "Usuario registrado (Simulado)");
    navigation.navigate("Login");
  };

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <View style={styles.content}>
        <Text style={styles.title}>Crear Cuenta</Text>
        <Text style={styles.subtitle}>Sumate al sistema de Tandil</Text>

        <TextInput
          style={styles.input}
          placeholder="DNI (Sin puntos)"
          keyboardType="numeric"
          value={dni}
          onChangeText={setDni}
        />

        <TextInput
          style={styles.input}
          placeholder="Nombre de usuario"
          value={username}
          onChangeText={setUsername}
        />

        <TextInput
          style={styles.input}
          placeholder="Contraseña"
          secureTextEntry={true}
          value={password}
          onChangeText={setPassword}
        />

        <TextInput
          style={styles.input}
          placeholder="Confirmar Contraseña"
          secureTextEntry={true}
          value={confirmPassword}
          onChangeText={setConfirmPassword}
        />

        <CustomButton title="Registrarse" onPress={handleRegister} />

        <Text style={styles.link} onPress={() => navigation.goBack()}>
          ¿Ya tienes cuenta? Inicia sesión
        </Text>
      </View>
    </ScrollView>
  );
};

const styles = StyleSheet.create({
  container: {
    flexGrow: 1,
    padding: 20,
    justifyContent: "center",
    backgroundColor: "#fbf9f0",
  },
  content: {
    marginBottom: 140
  },
  title: {
    fontSize: 28,
    fontWeight: "bold",
    color: "#000000",
    textAlign: "center",
  },
  subtitle: {
    fontSize: 16,
    color: "#666",
    textAlign: "center",
    marginBottom: 30,
  },
  input: {
    backgroundColor: "white",
    paddingHorizontal: 25,
    paddingVertical: 12,
    borderRadius: 25,
    borderWidth: 1,
    borderColor: "#ddd",
    marginBottom: 15,
  },
  link: {
    marginTop: 20,
    textAlign: "center",
    color: "#000000",
    fontWeight: "500",
    textDecorationLine: "underline",
  },
});

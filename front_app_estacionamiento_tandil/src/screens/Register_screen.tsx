import React, { useState } from "react";
import {
  View,
  Text,
  StyleSheet,
  TextInput,
  Alert,
  ScrollView,
  ActivityIndicator,
} from "react-native";
import { CustomButton } from "../components/Custom_button";
import { useAuth } from "../context/AuthContext"; 

export const RegisterScreen = ({ navigation }: any) => {
  const [dni, setDni] = useState("");
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  const { signUp } = useAuth();

  const handleRegister = async () => {
 
    if (!dni || !username || !password || !confirmPassword) {
      Alert.alert("Error", "Todos los campos son obligatorios");
      return;
    }

    if (password !== confirmPassword) {
      Alert.alert("Error", "Las contraseñas no coinciden");
      return;
    }

    if (username.length < 8) {
      Alert.alert("Error", "El usuario debe tener al menos 8 caracteres");
      return;
    }

    if (password.length < 6) {
      Alert.alert("Error", "La contraseña debe tener al menos 6 caracteres");
      return;
    }

    setIsSubmitting(true);

    try {
      await signUp({
        username,
        password,
        dni,
      });

      Alert.alert("¡Éxito!", "Tu cuenta ha sido creada correctamente");
    } catch (error: any) {
      Alert.alert(
        "Error de registro",
        "No se pudo completar el registro. Es posible que el DNI o el usuario ya estén en uso."
      );
      console.error("Register error: ", error);
    } finally {
      setIsSubmitting(false);
    }
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
          autoCapitalize="none"
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

        {isSubmitting ? (
          <ActivityIndicator size="large" color="#0055b3" style={{ marginVertical: 20 }} />
        ) : (
          <CustomButton title="Registrarse" onPress={handleRegister} />
        )}

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
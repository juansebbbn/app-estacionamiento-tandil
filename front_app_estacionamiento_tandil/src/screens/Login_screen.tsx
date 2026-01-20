import React, { useState } from "react";
import { View, Text, StyleSheet, TextInput, Alert, ActivityIndicator } from "react-native";
import { CustomButton } from "../components/Custom_button";
import { MaterialIcons, FontAwesome5 } from '@expo/vector-icons';
import { useAuth } from "../context/AuthContext";

export const LoginScreen = ({ navigation }: any) => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  const { signIn } = useAuth();

  const handleLogin = async () => {
    if (username.trim().length === 0 || password.trim().length === 0) {
      Alert.alert("Atención", "Por favor, ingresa tus datos completos.");
      return;
    }

    setIsSubmitting(true);

    try {
      await signIn({ username, password });
    } catch (error: any) {
      Alert.alert(
        "Error de acceso", 
        "Usuario o contraseña incorrectos. Verificá tus datos e intentá nuevamente."
      );
      console.error("Error en login:", error);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <View style={styles.container}>
      <View style={styles.content}>
        <View style={styles.iconContainer}>
          <MaterialIcons name="location-on" size={80} color="black" />
          <View style={styles.row}>
            <FontAwesome5 name="car" size={50} color="black" style={styles.carIcon} />
            <FontAwesome5 name="bus" size={50} color="black" style={styles.busIcon} />
          </View>
        </View>

        <Text style={styles.title}>Ingrese sus datos</Text>

        <TextInput
          style={styles.input}
          placeholder="Username"
          keyboardType="default"
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
        
        {isSubmitting ? (
          <ActivityIndicator size="large" color="#0055b3" style={{ marginVertical: 20 }} />
        ) : (
          <CustomButton title="Ingresar" onPress={handleLogin} />
        )}

        <Text style={styles.link} onPress={() => navigation.navigate("Register")}>
          ¿No tienes cuenta? Registrate aquí
        </Text>
      </View>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    padding: 20,
    justifyContent: "center",
    backgroundColor: "#fbf9f0",
  },
  content: {
    marginBottom: 140
  },
  title: {
    fontSize: 20,
    fontWeight: "bold",
    textAlign: "center",
    marginBottom: 40,
    marginTop: 20,
    color: "#000000",
  },
  input: {
    backgroundColor: "white",
    paddingHorizontal: 25,
    paddingVertical: 10,
    borderRadius: 20,
    borderWidth: 1,
    borderColor: "#e9e9e9",
    marginBottom: 15,
  },
  link: {
    marginTop: 20,
    textAlign: "center",
    color: "#000000",
    textDecorationLine: "underline",
  },
  iconContainer: {
    alignItems: 'center',
    justifyContent: 'center',
  },
  row: {
    flexDirection: 'row',
    marginTop: -10, 
  },
  carIcon: {
    marginRight: 40,
    transform: [{ rotate: '-15deg' }], 
  },
  busIcon: {
    marginLeft: 40,
    transform: [{ rotate: '15deg' }], 
  },
});
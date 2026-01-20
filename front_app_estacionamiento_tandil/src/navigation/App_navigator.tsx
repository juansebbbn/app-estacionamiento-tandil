import React from "react";
import { createStackNavigator } from "@react-navigation/stack";
import { View, ActivityIndicator } from "react-native";
import { useAuth } from "../context/AuthContext"; 
import { LoginScreen } from "../screens/Login_screen";
import { RegisterScreen } from "../screens/Register_screen";
import { WelcomeScreen } from "../screens/Welcome_screen";
import { SelectionScreen } from "../screens/Selection_screen";
import { ParkingMenu } from "../screens/ParkingMainMenu_screen";
import { Cars_screen } from "../screens/Cars_screen";
import { ParkingScreen } from "../screens/Parking_screen";
import { AccountScreen } from "../screens/Acoount_screnn";
import { PaymentHistoryScreen } from "../screens/Payments_screen";
import { MapZoneScreen } from "../screens/Map_screen";

const Stack = createStackNavigator();

export const App_navigator = () => {
  const { user, loading } = useAuth(); 

  if (loading) {
    return (
      <View style={{ flex: 1, justifyContent: "center", alignItems: "center", backgroundColor: "#fbf9f0" }}>
        <ActivityIndicator size="large" color="#0055b3" />
      </View>
    );
  }

  return (
    <Stack.Navigator>
      {user ? (
        <>
          <Stack.Screen
            name="Selection"
            component={SelectionScreen}
            options={{ title: "Selección" }}
          />
          <Stack.Screen
            name="ParkingMenu"
            component={ParkingMenu}
            options={{ title: "Menú Principal" }}
          />
          <Stack.Screen
            name="Autos"
            component={Cars_screen}
            options={{ title: "Mis Vehículos" }}
          />
          <Stack.Screen
            name="Estacionamiento"
            component={ParkingScreen}
            options={{ title: "Estacionar" }}
          />
          <Stack.Screen
            name="Cuenta"
            component={AccountScreen}
            options={{ title: "Mi Cuenta" }}
          />
          <Stack.Screen
            name="Pagos"
            component={PaymentHistoryScreen}
            options={{ title: "Historial de Pagos" }}
          />
          <Stack.Screen
            name="Mapa"
            component={MapZoneScreen}
            options={{ title: "Zonas de Estacionamiento" }}
          />
        </>
      ) : (
        <>
          <Stack.Screen
            name="Welcome"
            component={WelcomeScreen}
            options={{ title: "Welcome", headerShown: false }}
          />
          <Stack.Screen
            name="Login"
            component={LoginScreen}
            options={{ title: "Iniciar Sesión" }}
          />
          <Stack.Screen
            name="Register"
            component={RegisterScreen}
            options={{ title: "Registro" }}
          />
        </>
      )}
    </Stack.Navigator>
  );
};
import React from "react";
import { createStackNavigator } from "@react-navigation/stack";
import { LoginScreen } from "../screens/Login_screen";
import { RegisterScreen } from "../screens/Register_screen";
import { WelcomeScreen } from "../screens/Welcome_screen";
import { SelectionScreen } from "../screens/Selection_screen";
import { ParkingMenu } from "../screens/ParkingMainMenu_screen";
import { MyCarsScreen } from "../screens/Cars_screen";
import { ParkingScreen } from "../screens/Parking_screen";


const Stack = createStackNavigator();

export const App_navigator = () => {
  return (
    <Stack.Navigator initialRouteName="Welcome">
      <Stack.Screen
        name="Welcome"
        component={WelcomeScreen}
        options={{ title: "Welcome" }}
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
      <Stack.Screen
        name="Selection"
        component={SelectionScreen}
      />
      <Stack.Screen
        name="ParkingMenu"
        component={ParkingMenu}
      />
      <Stack.Screen
        name="Autos"
        component={MyCarsScreen}
      />
      <Stack.Screen
        name="Estacionamiento"
        component={ParkingScreen}
      />
    </Stack.Navigator>
  );
};

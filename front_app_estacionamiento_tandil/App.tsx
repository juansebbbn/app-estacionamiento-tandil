import React from "react";
import { NavigationContainer } from "@react-navigation/native";
import { App_navigator } from "./src/navigation/App_navigator";
import { AuthProvider } from "./src/context/AuthContext";

export default function App() {
  return (
    <AuthProvider>
      <NavigationContainer>
        <App_navigator />
      </NavigationContainer>
    </AuthProvider>
  );
}
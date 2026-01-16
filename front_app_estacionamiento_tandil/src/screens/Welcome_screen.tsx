import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { MaterialIcons, FontAwesome5 } from '@expo/vector-icons';
import { CustomButton } from '../components/Custom_button';
import { SafeAreaProvider } from 'react-native-safe-area-context';

export const WelcomeScreen = ({ navigation }: any) => {
  return (
    <SafeAreaProvider style={styles.container}>
      <View style={styles.content}>
        
        <Text style={styles.title}>Te damos la bienvenida a SUMO Tandil</Text>

        <View style={styles.iconContainer}>
          <MaterialIcons name="location-on" size={80} color="black" />
          <View style={styles.row}>
            <FontAwesome5 name="car" size={50} color="black" style={styles.carIcon} />
            <FontAwesome5 name="bus" size={50} color="black" style={styles.busIcon} />
          </View>
        </View>

        <Text style={styles.description}>
          Estacioná fácil y viajá en colectivo, todo desde un mismo lugar
        </Text>

        <View style={styles.buttonContainer}>
          <CustomButton 
            title="Registrarse" 
            onPress={() => navigation.navigate('Register')} 
          />
          <View style={{ height: 10 }} /> 
          <CustomButton 
            title="Iniciar sesión" 
            onPress={() => navigation.navigate('Login')} 
          />
        </View>

      </View>
    </SafeAreaProvider>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fbf9f0',
  },
  content: {
    flex: 1,
    paddingHorizontal: 30,
    alignItems: 'center',
    justifyContent: 'space-around', 
  },
  title: {
    fontSize: 18,
    fontWeight: 'bold',
    textAlign: 'center',
    marginTop: 10,

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
  description: {
    fontSize: 16,
    color: '#333',
    textAlign: 'center',
    paddingHorizontal: 20,
  },
  buttonContainer: {
    width: '100%',
    marginBottom: 80,
  },
});
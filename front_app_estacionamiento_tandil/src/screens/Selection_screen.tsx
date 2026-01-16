import React from 'react';
import { View, Text, StyleSheet, TouchableOpacity } from 'react-native';
import { SafeAreaProvider } from 'react-native-safe-area-context';

export const SelectionScreen = ({ navigation }: any) => {
  return (
    <SafeAreaProvider style={styles.container}>
      <View style={styles.content}>
        
        <Text style={styles.greeting}>¡Hola!</Text>
        <Text style={styles.question}>¿Cómo te ayudamos hoy?</Text>

        <TouchableOpacity 
          style={[styles.card, styles.yellow]} 
          onPress={() => navigation.navigate('ParkingMenu')}
        >
          <Text style={styles.cardTitle}>Estacionamiento</Text>
        </TouchableOpacity>

        <TouchableOpacity 
          style={[styles.card, styles.blue]} 
          onPress={() => console.log("Ir a Transporte")}
        >
          <Text style={styles.cardTitle}>Transporte</Text>
        </TouchableOpacity>

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
    alignItems: 'center',
    justifyContent: 'center',
    paddingHorizontal: 30,
  },
  greeting: {
    fontSize: 22,
    fontWeight: 'bold',
    marginBottom: 5,
  },
  question: {
    fontSize: 18,
    color: '#333',
    marginBottom: 40,
  },
  card: {
    
    width: '100%',
    height: 110,
    borderRadius: 15,
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: 20,
    elevation: 3,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
  },
  yellow: {
    backgroundColor: '#FEDA05', 
  },
  blue: {
    backgroundColor: '#0082CA', 
  },
  cardTitle: {
    fontSize: 18,
    fontWeight: 'bold',
    letterSpacing: 1,
  },

});
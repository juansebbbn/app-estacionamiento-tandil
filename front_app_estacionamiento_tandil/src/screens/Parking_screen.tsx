import React, { useState } from 'react';
import { View, Text, StyleSheet, TouchableOpacity, ScrollView, Alert } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { FontAwesome5, MaterialCommunityIcons, Ionicons } from '@expo/vector-icons';

export const ParkingScreen = ({ navigation }: any) => {
  const insets = useSafeAreaInsets();
  
  // Estados para la selección
  const [selectedCar, setSelectedCar] = useState('AF 123 BK');
  const [hours, setHours] = useState(1);

  const handleConfirm = () => {
    // Aquí dispararás el POST a Spring Boot en el futuro
    Alert.alert(
      "Estacionamiento Iniciado",
      `Vehículo: ${selectedCar}\nTiempo: ${hours}hs\nZona: Microcentro`,
      [{ text: "OK", onPress: () => navigation.navigate('ParkingMenu') }]
    );
  };

  return (
    <View style={[styles.container, { paddingTop: insets.top, paddingBottom: insets.bottom }]}>
      <ScrollView contentContainerStyle={styles.content}>
        
        <Text style={styles.title}>Iniciar Estacionamiento</Text>

        {/* Sección: Selección de Auto */}
        <View style={styles.section}>
          <Text style={styles.label}>Vehículo seleccionado:</Text>
          <TouchableOpacity 
            style={styles.selector} 
          >
            <FontAwesome5 name="car" size={20} color="#0055b3" />
            <Text style={styles.selectorText}>{selectedCar}</Text>
            <Ionicons name="chevron-forward" size={20} color="#888" />
          </TouchableOpacity>
        </View>

        {/* Sección: Zona Detectada */}
        <View style={styles.section}>
          <Text style={styles.label}>Zona detectada:</Text>
          <View style={styles.infoBox}>
            <MaterialCommunityIcons name="map-marker-radius" size={24} color="#e74c3c" />
            <View>
              <Text style={styles.infoTitle}>Zona Céntrica - Tandil</Text>
              <Text style={styles.infoSubtitle}>Dentro de las 4 avenidas</Text>
            </View>
          </View>
        </View>

        {/* Sección: Contador de Tiempo */}
        <View style={styles.section}>
          <Text style={styles.label}>¿Cuánto tiempo vas a estar? (Horas)</Text>
          <View style={styles.counterContainer}>
            <TouchableOpacity 
                style={styles.counterBtn} 
                onPress={() => setHours(Math.max(1, hours - 1))}
            >
              <Ionicons name="remove" size={24} color="black" />
            </TouchableOpacity>
            
            <Text style={styles.counterText}>{hours} h</Text>
            
            <TouchableOpacity 
                style={styles.counterBtn} 
                onPress={() => setHours(hours + 1)}
            >
              <Ionicons name="add" size={24} color="black" />
            </TouchableOpacity>
          </View>
        </View>

        {/* Resumen de Pago */}
        <View style={styles.summaryCard}>
          <Text style={styles.summaryLabel}>Costo estimado:</Text>
          <Text style={styles.price}>${hours * 150}.00</Text> 
          <Text style={styles.balanceInfo}>Saldo disponible: $2.450,00</Text>
        </View>

        {/* Botón de Confirmación */}
        <TouchableOpacity 
          style={styles.mainButton}
          onPress={handleConfirm}
        >
          <Text style={styles.mainButtonText}>Confirmar Estacionamiento</Text>
        </TouchableOpacity>

      </ScrollView> 
    </View>
  );
};

const styles = StyleSheet.create({
  container: { 
    flex: 1, 
    backgroundColor: '#fbf9f0' 
  },
  content: { 
    padding: 25,
    paddingBottom: 40 
  },
  title: { 
    fontSize: 20, 
    fontWeight: 'bold', 
    marginBottom: 30, 
    color: '#333', 
    textAlign: 'center' 
  },
  section: { 
    marginBottom: 25 
  },
  label: { 
    fontSize: 16, 
    fontWeight: '600', 
    color: '#555', 
    marginBottom: 10 
  },
  selector: {
    flexDirection: 'row',
    backgroundColor: '#fff',
    padding: 15,
    borderRadius: 12,
    alignItems: 'center',
    elevation: 2,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
  },
  selectorText: { 
    flex: 1, 
    marginLeft: 15, 
    fontSize: 16, 
    fontWeight: 'bold' 
  },
  infoBox: {
    flexDirection: 'row',
    backgroundColor: '#fff',
    padding: 15,
    borderRadius: 12,
    alignItems: 'center',
    gap: 15,
    borderLeftWidth: 5,
    borderLeftColor: '#e74c3c'
  },
  infoTitle: { 
    fontWeight: 'bold', 
    fontSize: 16 
  },
  infoSubtitle: { 
    color: '#666', 
    fontSize: 14 
  },
  counterContainer: {
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    gap: 30,
    marginTop: 10
  },
  counterBtn: {
    backgroundColor: '#FEDA05',
    width: 50,
    height: 50,
    borderRadius: 25,
    justifyContent: 'center',
    alignItems: 'center',
    elevation: 3
  },
  counterText: { 
    fontSize: 22, 
    fontWeight: 'bold' 
  },
  summaryCard: {
    backgroundColor: '#333',
    padding: 20,
    borderRadius: 15,
    alignItems: 'center',
    marginTop: 20,
    marginBottom: 30
  },
  summaryLabel: { 
    color: '#bbb', 
    fontSize: 14 
  },
  price: { 
    color: '#fff', 
    fontSize: 32, 
    fontWeight: 'bold', 
    marginVertical: 5 
  },
  balanceInfo: { 
    color: '#FEDA05', 
    fontSize: 12 
  },
  mainButton: {
    backgroundColor: '#FEDA05',
    padding: 18,
    borderRadius: 12,
    alignItems: 'center'
  },
  mainButtonText: { 
    color: '#000000', 
    fontSize: 14, 
    textAlign: 'justify',
    fontWeight: 'bold' 
  }
});
import React from 'react';
import { View, Text, StyleSheet, FlatList, SafeAreaView } from 'react-native';
import { useSafeAreaInsets } from 'react-native-safe-area-context';
import { MaterialIcons } from '@expo/vector-icons';

interface Payment {
  id: string;
  paymentDate: string; 
  amount: number;      
}

export const PaymentHistoryScreen = () => {
  const insets = useSafeAreaInsets();

  const payments: Payment[] = [
    { id: '1', paymentDate: '2026-01-15T10:30:00', amount: 1500.00 },
    { id: '2', paymentDate: '2026-01-14T18:45:00', amount: 500.00 },
    { id: '3', paymentDate: '2026-01-12T09:15:00', amount: 2000.00 },
    { id: '4', paymentDate: '2026-01-10T14:20:00', amount: 150.00 },
  ];

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('es-AR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  };

  return (
    <View style={[styles.container, { paddingTop: insets.top }]}>
      <View style={styles.header}>
        <Text style={styles.title}>Historial de Pagos</Text>
        <Text style={styles.subtitle}>Tus últimas recargas y consumos</Text>
      </View>

      <FlatList
        data={payments}
        keyExtractor={(item) => item.id}
        contentContainerStyle={styles.listContent}
        renderItem={({ item }) => (
          <View style={styles.paymentCard}>
            <View style={styles.iconContainer}>
              <MaterialIcons 
                name={item.amount > 200 ? "add-circle-outline" : "local-parking"} 
                size={24} 
                color={item.amount > 200 ? "#27ae60" : "#333"} 
              />
            </View>
            
            <View style={styles.detailsContainer}>
              <Text style={styles.dateText}>{formatDate(item.paymentDate)}</Text>
              <Text style={styles.typeText}>
                {item.amount > 200 ? "Recarga de Saldo" : "Uso de Estacionamiento"}
              </Text>
            </View>

            <View style={styles.amountContainer}>
              <Text style={[
                styles.amountText, 
                { color: item.amount > 200 ? "#27ae60" : "#333" }
              ]}>
                {item.amount > 200 ? "+" : "-"}${item.amount.toFixed(2)}
              </Text>
            </View>
          </View>
        )}
        ListEmptyComponent={
          <Text style={styles.emptyText}>No registras movimientos de pago todavía.</Text>
        }
      />
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fbf9f0',
  },
  header: {
    padding: 25,
    backgroundColor: '#fff',
    borderBottomWidth: 1,
    borderBottomColor: '#eee',
  },
  title: {
    fontSize: 22,
    fontWeight: 'bold',
    color: '#333',
  },
  subtitle: {
    fontSize: 14,
    color: '#888',
    marginTop: 5,
  },
  listContent: {
    padding: 20,
  },
  paymentCard: {
    flexDirection: 'row',
    backgroundColor: '#fff',
    padding: 15,
    borderRadius: 12,
    alignItems: 'center',
    marginBottom: 12,
    elevation: 2,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.1,
    shadowRadius: 2,
  },
  iconContainer: {
    width: 45,
    height: 45,
    borderRadius: 22.5,
    backgroundColor: '#f0f0f0',
    justifyContent: 'center',
    alignItems: 'center',
  },
  detailsContainer: {
    flex: 1,
    marginLeft: 15,
  },
  dateText: {
    fontSize: 12,
    color: '#888',
  },
  typeText: {
    fontSize: 15,
    fontWeight: '600',
    color: '#333',
    marginTop: 2,
  },
  amountContainer: {
    alignItems: 'flex-end',
  },
  amountText: {
    fontSize: 16,
    fontWeight: 'bold',
  },
  emptyText: {
    textAlign: 'center',
    marginTop: 50,
    color: '#999',
  },
});
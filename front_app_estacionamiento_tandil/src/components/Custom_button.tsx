import { TouchableOpacity, Text, StyleSheet, GestureResponderEvent } from 'react-native';

interface ButtonProps {
  title: string;
  onPress: (event: GestureResponderEvent) => void;
}

export const CustomButton = ({ title, onPress }: ButtonProps) => (
  <TouchableOpacity style={styles.button} onPress={onPress}>
    <Text style={styles.text}>{title}</Text>
  </TouchableOpacity>
);

const styles = StyleSheet.create({
  button: { 
    backgroundColor: '#FEDA05', 
    padding: 15, 
    borderRadius: 30, 
    alignItems: 'center',
    marginVertical: 10 
  },
  text: { 
    color: 'black', 
    fontWeight: 'bold',
    fontSize: 16
  }
});
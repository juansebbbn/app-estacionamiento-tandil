import { NavigationContainer } from '@react-navigation/native';
import { App_navigator } from './src/navigation/App_navigator';

export default function App() {
  return (
    <NavigationContainer>
      <App_navigator />
    </NavigationContainer>
  );
}
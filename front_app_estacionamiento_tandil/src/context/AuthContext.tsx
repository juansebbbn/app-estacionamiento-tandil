import React, { createContext, useState, useEffect, useContext, ReactNode } from 'react';
import * as SecureStore from 'expo-secure-store';
import Api_client from '../api/Api_client'; // Asegúrate de que la ruta sea correcta

interface LoginData {
  username: string;
  password: string;
}

interface RegisterData {
  username: string;
  password: string;
  dni: string;
}

interface AuthContextData {
  user: any;
  loading: boolean;
  signIn: (data: LoginData) => Promise<void>;
  signUp: (data: RegisterData) => Promise<void>;
  signOut: () => void;
}

const AuthContext = createContext<AuthContextData>({} as AuthContextData);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<any>(null);
  const [loading, setLoading] = useState(true);


  useEffect(() => {
    async function loadStoredData() {
      try {
        const token = await SecureStore.getItemAsync('userToken');
        const username = await SecureStore.getItemAsync('username');

        if (token && username) {
    
          setUser(JSON.parse(username));
        }
      } catch (e) {
        console.error("Error recuperando datos persistentes", e);
      } finally {
        setLoading(false);
      }
    }
    loadStoredData();
  }, []);

  const signIn = async (data: LoginData) => {
    try {
      const response = await Api_client.post('/auth/login', data);

      console.log(response.data)

      const token = response.data.accessToken;
      const username = response.data.username;

      await SecureStore.setItemAsync('userToken', String(token)); 
      
      const userJson = JSON.stringify(username);
      await SecureStore.setItemAsync('username', userJson);

      setUser(username); 
    } catch (error) {
      console.error("Error en signIn:", error);
      throw error;
    }
  };

  const signUp = async (data: RegisterData) => {
    try {
      const response = await Api_client.post('/auth/register', data);
      console.log(response.data)

      const token = response.data.accessToken;
      const username = response.data.username;
      await SecureStore.setItemAsync('userToken', String(token)); 
  
      const userJson = JSON.stringify(username);
      await SecureStore.setItemAsync('username', userJson);

      setUser(username);
    } catch (error) {
      console.error("Error en signUp:", error);
      throw error;
    }
  };

  const signOut = async () => {
    await SecureStore.deleteItemAsync('userToken');
    await SecureStore.deleteItemAsync('username');
    setUser(null);
  };

  
  // useEffect(() => {
  //   signOut();
  // }, []);

  return (
    <AuthContext.Provider value={{ user, loading, signIn, signUp, signOut }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth debe ser usado dentro de un AuthProvider');
  }
  return context;
};
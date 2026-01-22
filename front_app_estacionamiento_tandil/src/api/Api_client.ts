import axios from 'axios';
import * as SecureStore from 'expo-secure-store';

const Api_client = axios.create({
  baseURL: 'http://192.168.0.120:8080', 
  headers: {
    'Content-Type': 'application/json',
  },
});

Api_client.interceptors.request.use(
  async (config) => {
    const token = await SecureStore.getItemAsync('userToken');

    if (token && token !== 'undefined' && token !== 'null') {
      config.headers.Authorization = `Bearer ${token}`;
    } else {
      delete config.headers.Authorization;
    }

    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

Api_client.interceptors.response.use(
  (response) => {
    return response;
  },
  async (error) => {
    if (error.response && error.response.status === 401) {
      console.warn("Session expired or token invalidad detected by interceptor");
      await SecureStore.deleteItemAsync('userToken');
      await SecureStore.deleteItemAsync('userData');
    }
    return Promise.reject(error);
  }
);

export default Api_client;

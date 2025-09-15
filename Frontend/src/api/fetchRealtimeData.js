import axios from 'axios';

export const fetchRealtimeData = async (token) => {
  try {
    const response = await axios.get('http://localhost:8000/realtime-weather-simulator/latest?limit=120', {
      headers: {
        'Authorization': `Bearer ${token}`,
      },
    });
    return response.data;
  } catch (error) {
    console.error('Ошибка при получении данных:', error);
    return null;
  }
};

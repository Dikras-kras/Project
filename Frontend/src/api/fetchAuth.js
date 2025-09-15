export const loginUser = async (credentials) => {
    try {
      const response = await fetch('http://localhost:8000/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(credentials),
      });
  
      const data = await response.json();
  
      if (!response.ok) {
        throw new Error(data.message || 'Неверный логин или пароль');
      }
  
      return data;
    } catch (error) {
      throw new Error(error.message || 'Ошибка при авторизации');
    }
  };
  
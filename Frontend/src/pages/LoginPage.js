import React from 'react';
import { Form, Input, Button, message } from 'antd';
import { useNavigate } from 'react-router-dom';
import { loginUser } from '../api/fetchAuth'; // Импорт функции API

const LoginPage = () => {
  const navigate = useNavigate();

  const onFinish = async (values) => {
    try {
      const data = await loginUser(values);
      localStorage.setItem('token', data.token);
      message.success('Авторизация успешна!');
      navigate('/main');
    } catch (error) {
      message.error(error.message);
    }
  };

  return (
    <div style={{ maxWidth: 400, margin: '50px auto', padding: 20, border: '1px solid #ddd', borderRadius: 8 }}>
      <h2 style={{ textAlign: 'center' }}>Авторизация</h2>
      <Form
        name="login"
        layout="vertical"
        onFinish={onFinish}
      >
        <Form.Item
          label="Логин"
          name="login"
          rules={[{ required: true, message: 'Введите логин!' }]}
        >
          <Input />
        </Form.Item>

        <Form.Item
          label="Пароль"
          name="password"
          rules={[{ required: true, message: 'Введите пароль!' }]}
        >
          <Input.Password />
        </Form.Item>

        <Form.Item>
          <Button type="primary" htmlType="submit" block>
            Войти
          </Button>
        </Form.Item>
      </Form>
    </div>
  );
};

export default LoginPage;

import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom'; // Используем Routes вместо Switch
import MainPage from './pages/MainPage';
import LoggPage from './pages/LoggPage';
import AdminPage from './pages/AdminPage';

const App = () => {
  return (
    <Router>
      <Routes> {/* Заменили Switch на Routes */}
        <Route path="/" element={<LoggPage />} />
        <Route path="/main" element={<MainPage />} />
        <Route path="/adminPage" element={<AdminPage />} />
      </Routes>
    </Router>
  );
};

export default App;

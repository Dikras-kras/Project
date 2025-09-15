import React, { useState, useEffect } from "react";
import AdminPage from '../pages/AdminPage';
import MainPage from "./MainPage";

const API_URL = "http://localhost:8000/api";

export default function App() {
  const [token, setToken] = useState(localStorage.getItem("token") || "");
  const [role, setRole] = useState(localStorage.getItem("role") || "");
  const [login, setLogin] = useState(localStorage.getItem("login") || "");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split(".")[1]));
        setRole(payload.role || "");
        setLogin(payload.sub || "");
        localStorage.setItem("role", payload.role || "");
        localStorage.setItem("login", payload.sub || "");
      } catch (e) {
        console.error("Ошибка при разборе токена:", e);
        handleLogout();
      }
    }
  }, [token]);

  const handleLogin = async (e) => {
    e.preventDefault();
    setError("");
    try {
      const res = await fetch(`${API_URL}/auth/login`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ login, password }),
      });
      if (res.ok) {
        const data = await res.json();
        setToken(data.token);
        localStorage.setItem("token", data.token);
        // login и role установятся в useEffect
      } else {
        setError("Неверный логин или пароль");
      }
    } catch {
      setError("Ошибка сервера");
    }
  };

  const handleLogout = () => {
    setToken("");
    setRole("");
    setLogin("");
    setPassword("");
    localStorage.removeItem("token");
    localStorage.removeItem("role");
    localStorage.removeItem("login");
  };

  if (!token) {
    return (
      <div style={{ maxWidth: 300, margin: "auto", marginTop: 50 }}>
        <h3>Вход</h3>
        <form onSubmit={handleLogin}>
          <input
            type="text"
            placeholder="Логин"
            value={login}
            onChange={(e) => setLogin(e.target.value)}
            required
          />
          <br />
          <input
            type="password"
            placeholder="Пароль"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
          <br />
          <button type="submit">Войти</button>
          {error && <p style={{ color: "red" }}>{error}</p>}
        </form>
      </div>
    );
  }

  return (
    <div>
      <div style={{ float: "right", margin: "10px" }}>
        Привет, {login}!{" "}
        <button onClick={handleLogout} style={{ marginLeft: "10px" }}>
          Выйти
        </button>
      </div>

      <div style={{ padding: "20px" }}>
        {role === "ADMIN" ? (
          <AdminPage token={token} />
        ) : (
          <MainPage login={login} />
        )}
      </div>

    </div>
  );
}

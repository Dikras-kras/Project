import React, { useEffect, useState } from "react";

const API_URL = "http://localhost:8000/api/admin/users";

const roles = [
  { value: "USER", label: "Пользователь" },
  { value: "ADMIN", label: "Администратор" },
];

export default function AdminPage({ token }) {
  const [users, setUsers] = useState([]);
  const [error, setError] = useState("");
  const [editingUserId, setEditingUserId] = useState(null);
  const [formUser, setFormUser] = useState({
    login: "",
    email: "",
    fullName: "",
    password: "",
    role: "USER",
  });
  const [newUser, setNewUser] = useState({
    login: "",
    email: "",
    fullName: "",
    password: "",
    role: "USER",
  });
  const [loading, setLoading] = useState(false);

  // Загрузка пользователей
  const fetchUsers = () => {
    setLoading(true);
    fetch(API_URL, {
      headers: { Authorization: `Bearer ${token}` },
    })
      .then((res) => {
        if (!res.ok) throw new Error("Ошибка загрузки пользователей");
        return res.json();
      })
      .then((data) => {
        setUsers(data);
        setError("");
      })
      .catch((err) => {
        console.error(err);
        setError("Не удалось загрузить пользователей");
      })
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchUsers();
  }, [token]);

  // Начать редактирование пользователя
  const startEdit = (user) => {
    setEditingUserId(user.id);
    setFormUser({
      login: user.login,
      email: user.email,
      fullName: user.fullName,
      password: "",
      role: user.role || "USER",
    });
  };

  // Отмена редактирования
  const cancelEdit = () => {
    setEditingUserId(null);
    setFormUser({
      login: "",
      email: "",
      fullName: "",
      password: "",
      role: "USER",
    });
  };

  // Сохранить изменения пользователя (PUT)
  const saveUser = () => {
    fetch(`${API_URL}/${editingUserId}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(formUser),
    })
      .then((res) => {
        if (!res.ok) throw new Error("Ошибка обновления пользователя");
        return res.json();
      })
      .then(() => {
        fetchUsers();
        cancelEdit();
      })
      .catch((err) => {
        alert(err.message);
      });
  };

  // Удалить пользователя (DELETE)
  const deleteUser = (id) => {
    if (!window.confirm("Удалить пользователя?")) return;
    fetch(`${API_URL}/${id}`, {
      method: "DELETE",
      headers: { Authorization: `Bearer ${token}` },
    })
      .then((res) => {
        if (!res.ok) throw new Error("Ошибка удаления пользователя");
        fetchUsers();
      })
      .catch((err) => alert(err.message));
  };

  // Добавить нового пользователя (POST)
  const addUser = () => {
    if (!newUser.login || !newUser.password) {
      alert("Логин и пароль обязательны");
      return;
    }
    fetch(API_URL, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(newUser),
    })
      .then((res) => {
        if (!res.ok) {
          return res.text().then((text) => {
            throw new Error(text || "Ошибка добавления пользователя");
          });
        }
        return res.json();
      })
      .then(() => {
        setNewUser({ login: "", email: "", fullName: "", password: "", role: "USER" });
        fetchUsers();
      })
      .catch((err) => alert(err.message));
  };

  return (
    <div style={{ maxWidth: 900, margin: "auto" }}>
      <h2>Управление пользователями (ADMIN)</h2>
      {error && <p style={{ color: "red" }}>{error}</p>}
      {loading && <p>Загрузка...</p>}

      <table border="1" cellPadding="5" style={{ width: "100%", marginBottom: 20 }}>
        <thead>
          <tr>
            <th>ID</th>
            <th>Логин</th>
            <th>Почта</th>
            <th>Полное имя</th>
            <th>Пароль</th>
            <th>Роль</th>
            <th>Действия</th>
          </tr>
        </thead>
        <tbody>
          {users.map((u) => (
            <tr key={u.id}>
              <td>{u.id}</td>
              <td>
                {editingUserId === u.id ? (
                  <input
                    type="text"
                    value={formUser.login}
                    onChange={(e) => setFormUser({ ...formUser, login: e.target.value })}
                  />
                ) : (
                  u.login
                )}
              </td>
              <td>
                {editingUserId === u.id ? (
                  <input
                    type="email"
                    value={formUser.email}
                    onChange={(e) => setFormUser({ ...formUser, email: e.target.value })}
                  />
                ) : (
                  u.email
                )}
              </td>
              <td>
                {editingUserId === u.id ? (
                  <input
                    type="text"
                    value={formUser.fullName}
                    onChange={(e) => setFormUser({ ...formUser, fullName: e.target.value })}
                  />
                ) : (
                  u.fullName
                )}
              </td>
              <td>
                {editingUserId === u.id ? (
                  <input
                    type="password"
                    placeholder="Новый пароль"
                    value={formUser.password}
                    onChange={(e) => setFormUser({ ...formUser, password: e.target.value })}
                  />
                ) : (
                  "********"
                )}
              </td>
              <td>
                {editingUserId === u.id ? (
                  <select
                    value={formUser.role}
                    onChange={(e) => setFormUser({ ...formUser, role: e.target.value })}
                  >
                    {roles.map((r) => (
                      <option key={r.value} value={r.value}>
                        {r.label}
                      </option>
                    ))}
                  </select>
                ) : (
                  roles.find((r) => r.value === u.role)?.label || u.role
                )}
              </td>
              <td>
                {editingUserId === u.id ? (
                  <>
                    <button onClick={saveUser}>Сохранить</button>{" "}
                    <button onClick={cancelEdit}>Отмена</button>
                  </>
                ) : (
                  <>
                    <button onClick={() => startEdit(u)}>Редактировать</button>{" "}
                    <button onClick={() => deleteUser(u.id)}>Удалить</button>
                  </>
                )}
              </td>
            </tr>
          ))}
          <tr>
            <td>Новый</td>
            <td>
              <input
                type="text"
                placeholder="Логин"
                value={newUser.login}
                onChange={(e) => setNewUser({ ...newUser, login: e.target.value })}
              />
            </td>
            <td>
              <input
                type="email"
                placeholder="Почта"
                value={newUser.email}
                onChange={(e) => setNewUser({ ...newUser, email: e.target.value })}
              />
            </td>
            <td>
              <input
                type="text"
                placeholder="Полное имя"
                value={newUser.fullName}
                onChange={(e) => setNewUser({ ...newUser, fullName: e.target.value })}
              />
            </td>
            <td>
              <input
                type="password"
                placeholder="Пароль"
                value={newUser.password}
                onChange={(e) => setNewUser({ ...newUser, password: e.target.value })}
              />
            </td>
            <td>
              <select
                value={newUser.role}
                onChange={(e) => setNewUser({ ...newUser, role: e.target.value })}
              >
                {roles.map((r) => (
                  <option key={r.value} value={r.value}>
                    {r.label}
                  </option>
                ))}
              </select>
            </td>
            <td>
              <button onClick={addUser}>Добавить</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  );
}

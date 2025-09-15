# Используем официальный образ Node.js
FROM node:22-alpine

# Устанавливаем рабочую директорию
WORKDIR /app

# Копируем package.json и package-lock.json перед установкой зависимостей
COPY package*.json ./

# Устанавливаем зависимости
RUN npm install

# Копируем остальные файлы
COPY . .

# Собираем приложение
RUN npm run build

# Используем Nginx для раздачи статики
FROM nginx:alpine
COPY --from=0 /app/build /usr/share/nginx/html

# Открываем порт
EXPOSE 80

# Запускаем Nginx
CMD ["nginx", "-g", "daemon off;"]
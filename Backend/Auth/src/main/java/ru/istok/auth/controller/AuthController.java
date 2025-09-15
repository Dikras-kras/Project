package ru.istok.auth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.istok.auth.dto.LoginRequest;
import ru.istok.auth.service.AuthService;


import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String userLogin = request.getLogin();
        String password = request.getPassword();
        logger.info("Попытка логина пользователя: {}", userLogin);

        Optional<String> tokenOpt = authService.login(userLogin, password);
        if (tokenOpt.isPresent()) {
            logger.info("Пользователь {} успешно вошёл", userLogin);
            return ResponseEntity.ok(Map.of("token", tokenOpt.get()));
        } else {
            logger.warn("Неудачная попытка входа для пользователя: {}", userLogin);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Неверный логин или пароль");
        }
    }

}

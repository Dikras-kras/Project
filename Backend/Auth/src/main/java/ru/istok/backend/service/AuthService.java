package ru.istok.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.istok.backend.model.User;
import ru.istok.backend.repository.UserRepository;
import ru.istok.backend.security.JwtUtil;

import java.util.Optional;
import java.util.UUID;


@Service
public class AuthService {


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public Optional<String> login(String username, String password) {
        Optional<User> userOpt = userRepository.findByLogin(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                // Теперь кладём в токен username, а не UUID
                String token = jwtUtil.generateToken(user.getLogin(), user.getRole());
                return Optional.of(token);
            }
        }
        return Optional.empty();
    }

}

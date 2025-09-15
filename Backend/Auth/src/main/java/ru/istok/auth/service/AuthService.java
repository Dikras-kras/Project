package ru.istok.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.istok.auth.model.User;
import ru.istok.auth.repository.UserRepository;
import ru.istok.auth.security.JwtUtil;

import java.util.Optional;


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

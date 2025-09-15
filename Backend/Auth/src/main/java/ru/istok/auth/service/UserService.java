package ru.istok.auth.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.istok.auth.model.User;
import ru.istok.auth.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    private final List<String> validRoles = List.of("USER", "ADMIN");

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(UUID id) {
        return userRepository.findById(id);
    }

    public User createUser(User user) {
        if (userRepository.existsByLogin(user.getLogin())) {
            throw new IllegalArgumentException("Пользователь с таким логином уже существует");
        }

        validateRole(user.getRole());

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    public User updateUser(UUID id, User updatedUser) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (!existingUser.getLogin().equals(updatedUser.getLogin())) {
            if (userRepository.existsByLogin(updatedUser.getLogin())) {
                throw new IllegalArgumentException("Пользователь с таким логином уже существует");
            }
            existingUser.setLogin(updatedUser.getLogin());
        }

        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
        }

        existingUser.setFullName(updatedUser.getFullName());
        existingUser.setEmail(updatedUser.getEmail());

        if (updatedUser.getRole() != null) {
            validateRole(updatedUser.getRole());
            existingUser.setRole(updatedUser.getRole());
        }

        return userRepository.save(existingUser);
    }

    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

    private void validateRole(String role) {
        if (role == null || !validRoles.contains(role)) {
            throw new IllegalArgumentException("Недопустимая роль: " + role);
        }
    }
}

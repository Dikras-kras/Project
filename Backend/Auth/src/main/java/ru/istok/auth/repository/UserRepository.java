package ru.istok.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.istok.auth.model.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByLogin(String login);
    boolean existsByLogin(String login);
}

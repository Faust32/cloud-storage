package ru.faust.cloudstorage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.faust.cloudstorage.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}

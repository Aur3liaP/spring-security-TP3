package fr.diginamic.spring_security.repository;

import fr.diginamic.spring_security.entity.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAppRepository extends JpaRepository<UserApp, Integer> {
    Optional<UserApp> findByEmail(String email);
    boolean existsByEmail(String email);
}

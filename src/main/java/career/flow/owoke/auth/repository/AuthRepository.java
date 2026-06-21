package career.flow.owoke.auth.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import career.flow.owoke.auth.entity.AuthUser;

public interface AuthRepository extends JpaRepository<AuthUser, String> {

    Optional<AuthUser> findByEmail(String email);

    boolean existsByEmail(String email);
}

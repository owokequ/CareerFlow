package career.flow.owoke.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import career.flow.owoke.user.entity.User;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByAuthId(String auth_id);

    boolean existsByEmail(String email);

    boolean existsByEmailAndAuthIdNot(String email, String authId);

    boolean existsByEmailAndIdNot(String email, String id);

    Optional<User> findByAuthId(String authId);
}

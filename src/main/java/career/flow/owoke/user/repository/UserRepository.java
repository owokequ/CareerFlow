package career.flow.owoke.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import career.flow.owoke.user.entity.User;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByAuthId(String auth_id);

    boolean existsByEmail(String email);
}

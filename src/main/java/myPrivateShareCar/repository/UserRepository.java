package myPrivateShareCar.repository;

import myPrivateShareCar.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    Optional<User> findById(int id);
    void deleteByEmail(String email);
    void deleteById(int id);
}

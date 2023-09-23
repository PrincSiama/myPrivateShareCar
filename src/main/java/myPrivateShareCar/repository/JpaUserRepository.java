package myPrivateShareCar.repository;

import myPrivateShareCar.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserRepository extends JpaRepository<User, Integer> {
}

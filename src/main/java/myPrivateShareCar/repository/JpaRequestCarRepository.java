package myPrivateShareCar.repository;

import myPrivateShareCar.model.RequestCar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaRequestCarRepository extends JpaRepository<RequestCar, Integer> {
}

package myPrivateShareCar.repository;

import myPrivateShareCar.model.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByCar_IdOrderByWriteDateAsc(int carId, Pageable pageable);
}

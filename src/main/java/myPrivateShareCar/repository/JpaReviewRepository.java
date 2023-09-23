package myPrivateShareCar.repository;

import myPrivateShareCar.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByIdCar (Integer id);
}

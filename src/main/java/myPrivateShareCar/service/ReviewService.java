package myPrivateShareCar.service;

import myPrivateShareCar.dto.ReviewDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewService {
    ReviewDto addReview(int carId, String text);
    List<ReviewDto> getReviewByCar(int carId, Pageable pageable);
}

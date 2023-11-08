package myPrivateShareCar.service;

import myPrivateShareCar.dto.ReviewDto;

import java.util.List;

public interface ReviewService {
    ReviewDto addReview(int carId, int userId, String text);
    List<ReviewDto> getReviewByCar(int carId, int page, int size);
}

package myPrivateShareCar.controller;

import lombok.RequiredArgsConstructor;
import myPrivateShareCar.dto.AddReviewDto;
import myPrivateShareCar.dto.ReviewDto;
import myPrivateShareCar.service.ReviewService;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cars/{carId}/review")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ReviewDto addReview(@PathVariable int carId,
                               @RequestBody AddReviewDto addReviewDto) {
        return reviewService.addReview(carId, addReviewDto.getText());
    }

    @GetMapping
    public List<ReviewDto> getReviewByCar(@PathVariable int carId,
                                          @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                          @RequestParam(value = "size", required = false, defaultValue = "5") int size) {
        return reviewService.getReviewByCar(carId, PageRequest.of(page, size));
    }

}

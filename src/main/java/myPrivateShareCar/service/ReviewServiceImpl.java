package myPrivateShareCar.service;

import lombok.AllArgsConstructor;
import myPrivateShareCar.dto.ReviewDto;
import myPrivateShareCar.exception.NotFoundException;
import myPrivateShareCar.model.BookingStatus;
import myPrivateShareCar.model.Car;
import myPrivateShareCar.model.Review;
import myPrivateShareCar.model.User;
import myPrivateShareCar.repository.BookingRepository;
import myPrivateShareCar.repository.CarRepository;
import myPrivateShareCar.repository.ReviewRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final CarRepository carRepository;
    private final ModelMapper mapper;

    @Override
    public List<ReviewDto> getReviewByCar(int carId, Pageable pageable) {
        if (carRepository.existsById(carId)) {
            return reviewRepository.findByCar_IdOrderByWriteDateAsc(carId, pageable).stream()
                    .map(review -> mapper.map(review, ReviewDto.class)).collect(Collectors.toList());
        }
        throw new NotFoundException("Невозможно получить отзывы по автомобилю. " +
                "Автомобиль с id " + carId + " не найден");
    }

    @Override
    public ReviewDto addReview(int carId, String text) {
        User user = getUserFromAuth();
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new NotFoundException("Невозможно добавить отзыв пользователя с id " + user.getId() +
                        " к автомобилю с id " + carId + ". У пользователя отсутствуют подтвержденные или завершенные" +
                        " бронирования для данного автомобиля"));

        if (!bookingRepository.findAllByUserIdAndCarIdAndBookingStatusIn(user.getId(), carId,
                List.of(BookingStatus.APPROVED, BookingStatus.FINISHED)).isEmpty()) {
            return mapper.map(reviewRepository.save(new Review(car, user, text, LocalDate.now())), ReviewDto.class);
        } else {
            throw new NotFoundException("Невозможно добавить отзыв пользователя с id " + user.getId() +
                    " к автомобилю с id " + carId + ". У пользователя отсутствуют подтвержденные или завершенные" +
                    " бронирования для данного автомобиля");
        }
    }

    private User getUserFromAuth() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}

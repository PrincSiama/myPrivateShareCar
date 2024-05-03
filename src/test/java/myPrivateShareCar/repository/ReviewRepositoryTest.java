package myPrivateShareCar.repository;

import myPrivateShareCar.dto.CreateCarDto;
import myPrivateShareCar.dto.CreateUserDto;
import myPrivateShareCar.model.Car;
import myPrivateShareCar.model.Passport;
import myPrivateShareCar.model.Review;
import myPrivateShareCar.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class ReviewRepositoryTest {
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private UserRepository userRepository;
    private final ModelMapper mapper = new ModelMapper();

    User user;
    Car car;
    Review review;

    @BeforeEach
    void setUp() {
        CreateUserDto createUserDto = new CreateUserDto("Пётр", "Петров", "petr@petrov.ru",
                LocalDate.of(2005, 5, 5), new Passport("2345", "456789",
                LocalDate.of(2019, 8, 3), "МВД №5"),
                null, "password1");
        User testUser = mapper.map(createUserDto, User.class);
        user = userRepository.save(testUser);

        CreateCarDto createCarDto = new CreateCarDto("BMW", "530",
                2019, "white", "1277 507800",
                "А111АА147", 2500);
        Car testCar = mapper.map(createCarDto, Car.class);
        car = carRepository.save(testCar);

        Review createReview = new Review(car, user, "Good car!", LocalDate.now());
        review = reviewRepository.save(createReview);
    }

    @Test
    @DisplayName("Получение списка отзывов по id автомобиля")
    void findByCar_IdOrderByWriteDateAsc() {
        List<Review> allReview = reviewRepository.findAll();
        assertEquals(1, allReview.size());

        int id = car.getId();

        List<Review> reviewList = reviewRepository.findByCar_IdOrderByWriteDateAsc(id,
                PageRequest.of(0, 5));

        assertEquals(1, reviewList.size());
        assertEquals(review.getText(), reviewList.get(0).getText());
    }
}
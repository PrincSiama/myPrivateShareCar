package myPrivateShareCar.service;

import myPrivateShareCar.dto.CreateUserDto;
import myPrivateShareCar.dto.ReviewDto;
import myPrivateShareCar.exception.NotCreatedException;
import myPrivateShareCar.exception.NotFoundException;
import myPrivateShareCar.model.*;
import myPrivateShareCar.repository.BookingRepository;
import myPrivateShareCar.repository.CarRepository;
import myPrivateShareCar.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {
    private ReviewService reviewService;
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CarRepository carRepository;
    @Mock
    private UserPrincipalService userPrincipalService;
    private final ModelMapper modelMapper = new ModelMapper();
    User testUser;

    @BeforeEach
    public void setUp() {
        reviewService = new ReviewServiceImpl(reviewRepository, bookingRepository, carRepository,
                userPrincipalService, modelMapper);
    }

    @Test
    @DisplayName("Добавление отзыва")
    void addReviewTest() {
        int testUserId = 11;
        int customCarId = 31;
        String reviewText = "Good car!";

        createTestUser(testUserId);

        Principal mockPrincipal = Mockito.mock(Principal.class);

        when(carRepository.findById(anyInt())).thenReturn(Optional.of(new Car()));

        when(bookingRepository.findAllByUserIdAndCarIdAndBookingStatusIn(
                anyInt(), anyInt(), Mockito.anyList())).thenReturn(List.of(new Booking()));

        when(reviewRepository.save(Mockito.any(Review.class))).then(returnsFirstArg());

        ReviewDto reviewDto = reviewService.addReview(customCarId, reviewText, mockPrincipal);

        assertNotNull(reviewDto);
        assertEquals(reviewText, reviewDto.getText());

    }

    @Test
    @DisplayName("Добавление отзыва без бронирований")
    void addReviewWithoutBookingTest() {
        int testUserId = 13;
        int customCarId = 33;
        String reviewText = "Good car!";

        createTestUser(testUserId);

        Principal mockPrincipal = Mockito.mock(Principal.class);

        when(carRepository.findById(anyInt())).thenReturn(Optional.of(new Car()));

        when(bookingRepository.findAllByUserIdAndCarIdAndBookingStatusIn(
                anyInt(), anyInt(), Mockito.anyList())).thenReturn(List.of());

        assertThrows(NotCreatedException.class, () -> reviewService.addReview(customCarId, reviewText, mockPrincipal));
    }

    @Test
    @DisplayName("Добавление отзыва на несуществующий авто")
    void addReviewForFakeCarTest() {
        int testUserId = 15;
        int customCarId = 35;

        createTestUser(testUserId);

        Principal mockPrincipal = Mockito.mock(Principal.class);

        when(carRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> reviewService.addReview(customCarId, null, mockPrincipal));
    }

    @Test
    @DisplayName("Получение отзывов по автомобилю")
    void getReviewByCarTest() {
        int customCarId = 37;

        when(carRepository.existsById(anyInt())).thenReturn(true);

        when(reviewRepository.findByCar_IdOrderByWriteDateAsc(anyInt(), Mockito.any(Pageable.class)))
                .thenReturn(List.of(new Review(), new Review()));

        List<ReviewDto> reviewDtoList = reviewService.getReviewByCar(customCarId, Pageable.unpaged());

        assertFalse(reviewDtoList.isEmpty());
        assertEquals(2, reviewDtoList.size());
    }

    @Test
    @DisplayName("Получение отзывов по несуществующему автомобилю")
    void getReviewByFakeCarTest() {
        int customCarId = 39;

        when(carRepository.existsById(anyInt())).thenReturn(false);

        assertThrows(NotFoundException.class, () -> reviewService.getReviewByCar(customCarId, Pageable.unpaged()));
    }

    private void createTestUser(int id) {
        CreateUserDto createUserDto = new CreateUserDto("Иван", "Иванов", "ivan@ivanov.ru",
                LocalDate.of(2000, 10, 1), new Passport("1234", "123456",
                LocalDate.of(2014, 5, 15), "МВД №1"),
                null, "password1");
        testUser = modelMapper.map(createUserDto, User.class);
        testUser.setId(id);

        when(userPrincipalService.getUserFromPrincipal(Mockito.any(Principal.class))).thenReturn(testUser);
    }
}
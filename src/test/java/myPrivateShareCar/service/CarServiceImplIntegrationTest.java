package myPrivateShareCar.service;

import myPrivateShareCar.dto.CarDto;
import myPrivateShareCar.dto.CreateCarDto;
import myPrivateShareCar.dto.CreateUserDto;
import myPrivateShareCar.exception.NotFoundException;
import myPrivateShareCar.model.*;
import myPrivateShareCar.repository.BookingRepository;
import myPrivateShareCar.repository.CarRepository;
import myPrivateShareCar.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CarServiceImplIntegrationTest {
    @Autowired
    private CarService carService;
    @Autowired
    private UserService userService;
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;

    @BeforeEach
    public void setUp() {
        carService = new CarServiceImpl(carRepository, userRepository, bookingRepository, modelMapper);
    }

    @Test
    @DirtiesContext
    @DisplayName("Корректное создание автомобиля")
    public void createCarTest() {
        int ownerId = createUser();
        Car testCar = createCar(ownerId);
        assertNotNull(testCar);
        assertEquals(ownerId, testCar.getOwnerId());
    }

    @Test
    @DirtiesContext
    @DisplayName("Корректное удаление автомобиля")
    public void deleteCarTest() {
        int ownerId = createUser();
        Car car = createCar(ownerId);
        assertNotNull(car);
        int carId = car.getId();
        carService.delete(ownerId, carId);
        assertThrows(NotFoundException.class, () -> {
            carService.getById(carId);
        });
    }

    @Test
    @DisplayName("Удаление несуществующего автомобиля")
    public void deleteCarWithNotExistCarTest() {
        assertThrows(NotFoundException.class, () -> {
            carService.delete(5, 577);
        });
    }

    @Test
    @DirtiesContext
    @DisplayName("Корректное получение автомобиля")
    public void getCarByIdTest() {
        int ownerId = createUser();
        Car car = createCar(ownerId);
        assertEquals(ownerId, car.getOwnerId());
        CarDto carDto = carService.getById(car.getId());
        assertNotNull(car);
        assertNotNull(carDto);
    }

    @Test
    @DisplayName("Получение несуществующего автомобиля")
    public void getCarByIdWithNotExistCarTest() {
        assertThrows(NotFoundException.class, () -> {
            carService.getById(578);
        });
    }

    @Test
    @DirtiesContext
    @DisplayName("Получение всех автомобилей владельца")
    public void getOwnerCarsTest() {
        int ownerId = createUser();

        List<CarDto> testList1 = carService.getOwnerCars(ownerId, 0, 5);

        assertTrue(testList1.isEmpty());

        Car car = createCar(ownerId);

        List<CarDto> testList2 = carService.getOwnerCars(ownerId, 0, 5);

        assertFalse(testList2.isEmpty());
        assertEquals(car.getId(), testList2.get(0).getId());

        CreateCarDto createCarDto2 = new CreateCarDto("BMW", "730",
                2020, "white", "2202 887900", "А002АА155");
        CreateCarDto createCarDto3 = new CreateCarDto("Lada", "2106",
                1997, "eggplant", "0010 350780", "В666АХ06");
        Car car2 = carService.create(ownerId, createCarDto2);
        int owner2Id = userService.create(new CreateUserDto("Вася", "Васильков", "vasya@vv.ru",
                LocalDate.of(1996, 7, 12), new Passport("2345", "234567",
                LocalDate.of(2019, 4, 20), "МВД №15"))).getId();
        Car car3 = carService.create(ownerId + 1, createCarDto3);

        List<CarDto> testList3 = carService.getOwnerCars(ownerId, 0, 5);

        assertEquals(2, testList3.size());
        assertTrue(testList3.stream().map(CarDto::getId).collect(Collectors.toList())
                .containsAll(List.of(car.getId(), car2.getId())));
    }

    @Test
    @DisplayName("Поиск авто по ключевым словам и датам бронирования")
    public void specificationsSearchTest() {
        int customBooking1Id = 1;
        int customBooking2Id = 2;
        int customBooking3Id = 3;
        String text = "bm";
        LocalDate startRent = LocalDate.now().plusDays(5);
        LocalDate endRent = LocalDate.now().plusDays(7);

        User owner = userService.create(new CreateUserDto("Иван", "Иванов", "ivan@ivanov.ru",
                LocalDate.of(2000, 10, 1), new Passport("1234", "123456",
                LocalDate.of(2014, 5, 15), "МВД №1")));
        User user = userService.create(new CreateUserDto("Вася", "Васильков", "vasya@vv.ru",
                LocalDate.of(1996, 7, 12), new Passport("2345", "234567",
                LocalDate.of(2019, 4, 20), "МВД №15")));

        Car car1 = carService.create(owner.getId(), new CreateCarDto("BMW", "530",
                2019, "white", "2201 887900", "А001АА155"));
        Car car2 = carService.create(owner.getId(), new CreateCarDto("Lada", "2106",
                1997, "eggplant", "0010 350780", "В666АХ06"));

        Booking booking1 = new Booking(customBooking1Id, user, car1, LocalDate.now().plusDays(5),
                2, BookingStatus.APPROVED, LocalDate.now().plusDays(7));
        bookingRepository.save(booking1);
        Booking booking2 = new Booking(customBooking2Id, user, car2, LocalDate.now().plusDays(10),
                3, BookingStatus.APPROVED, LocalDate.now().plusDays(13));
        bookingRepository.save(booking2);
        Booking booking3 = new Booking(customBooking3Id, user, car2, LocalDate.now().plusDays(15),
                3, BookingStatus.WAITING, LocalDate.now().plusDays(18));
        bookingRepository.save(booking3);

        List<CarDto> carDtoList1 = carService.search(text, null, null, 0, 5);
        assertFalse(carDtoList1.isEmpty());
        assertEquals(1, carDtoList1.size());
        assertEquals(car1.getId(), carDtoList1.get(0).getId());

        List<CarDto> carDtoList2 = carService.search(null, startRent, endRent, 0, 5);
        assertFalse(carDtoList2.isEmpty());
        assertEquals(1, carDtoList2.size());
        assertEquals(car2.getId(), carDtoList2.get(0).getId());
        // todo дописать для текста и дат, для всего нулл

    }

    private Integer createUser() {
        CreateUserDto createUserDto = new CreateUserDto("Иван", "Иванов", "ivan@ivanov.ru",
                LocalDate.of(2000, 10, 1), new Passport("1234", "123456",
                LocalDate.of(2014, 5, 15), "МВД №1"));
        User testUser = userService.create(createUserDto);
        return testUser.getId();
    }

    private Car createCar(int ownerId) {
        CreateCarDto createCarDto = new CreateCarDto("BMW", "530",
                2019, "white", "2201 887900", "А001АА155");
        return carService.create(ownerId, createCarDto);
    }
}
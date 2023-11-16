package myPrivateShareCar.service;

import myPrivateShareCar.dto.CarDto;
import myPrivateShareCar.dto.CreateCarDto;
import myPrivateShareCar.exception.NotFoundException;
import myPrivateShareCar.model.Car;
import myPrivateShareCar.repository.BookingRepository;
import myPrivateShareCar.repository.CarRepository;
import myPrivateShareCar.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class CarServiceImplIntegrationTest {
    private CarService carService;
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        carService = new CarServiceImpl(carRepository, userRepository, bookingRepository, new ModelMapper());
    }

    @Test
    @DirtiesContext
    @DisplayName("Корректное создание автомобиля")
    public void createCarTest() {
        when(userRepository.existsById(Mockito.anyInt())).thenReturn(true);
        int ownerId = 3;
        CreateCarDto createCarDto = new CreateCarDto("BMW", "530",
                2019, "white", "2201 887900", "А001АА155");
        Car testCar = carService.create(ownerId, createCarDto);
        assertNotNull(testCar);
        assertEquals(ownerId, testCar.getOwnerId());
    }

    @Test
    @DirtiesContext
    @DisplayName("Корректное удаление автомобиля")
    public void deleteCarTest() {
        int ownerId = 3;
        CreateCarDto createCarDto = new CreateCarDto("BMW", "530",
                2019, "white", "2201 887900", "А001АА155");

        when(userRepository.existsById(Mockito.anyInt())).thenReturn(true);

        Car car = carService.create(ownerId, createCarDto);

        carService.delete(ownerId, car.getId());

        assertNotNull(car);
    }

    @Test
    @DisplayName("Удаление несуществующего автомобиля")
    public void deleteCarWithNotExistCarTest() {

        assertThrows(NotFoundException.class, () -> {
            carService.delete(5, 77);
        });
    }

    @Test
    @DirtiesContext
    @DisplayName("Корректное получение автомобиля")
    public void getCarByIdTest() {
        int ownerId = 3;
        CreateCarDto createCarDto = new CreateCarDto("BMW", "530",
                2019, "white", "2201 887900", "А001АА155");

        when(userRepository.existsById(Mockito.anyInt())).thenReturn(true);

        Car car = carService.create(ownerId, createCarDto);

        CarDto carDto = carService.getById(car.getId());

        assertNotNull(car);
        assertNotNull(carDto);
    }
    @Test
    @DisplayName("Получение несуществующего автомобиля")
    public void getCarByIdWithNotExistCarTest() {
        assertThrows(NotFoundException.class, () -> {
            carService.getById(77);
        });
    }

    @Test
    @DirtiesContext
    @DisplayName("Получение всех автомобилей владельца")
    public void getOwnerCarsTest() {
        when(userRepository.existsById(Mockito.anyInt())).thenReturn(true);
        int ownerId = 3;

        List<CarDto> testList1 = carService.getOwnerCars(ownerId, 0, 5);

        assertTrue(testList1.isEmpty());

        CreateCarDto createCarDto = new CreateCarDto("BMW", "530",
                2019, "white", "2201 887900", "А001АА155");
        Car car = carService.create(ownerId, createCarDto);

        List<CarDto> testList2 = carService.getOwnerCars(ownerId, 0, 5);

        assertFalse(testList2.isEmpty());
        assertEquals(car.getId(), testList2.get(0).getId());

        CreateCarDto createCarDto2 = new CreateCarDto("BMW", "730",
                2020, "white", "2202 887900", "А002АА155");
        CreateCarDto createCarDto3 = new CreateCarDto("Lada", "2106",
                1997, "white", "0010 350780", "Н666НН99");
        Car car2 = carService.create(ownerId, createCarDto2);
        Car car3 = carService.create(ownerId + 1, createCarDto3);

        List<CarDto> testList3 = carService.getOwnerCars(ownerId, 0, 5);

        assertEquals(2, testList3.size());
        assertTrue(testList3.stream().map(CarDto::getId).collect(Collectors.toList())
                .containsAll(List.of(car.getId(), car2.getId())));
    }

}
package myPrivateShareCar.service;

import myPrivateShareCar.dto.CreateCarDto;
import myPrivateShareCar.model.Car;
import myPrivateShareCar.repository.CarRepository;
import myPrivateShareCar.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {

    private CarService carService;
    @Mock
    private CarRepository carRepository;
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        carService = new CarServiceImpl(carRepository, userRepository, new ModelMapper());
    }

    @Test
    void create() {
        when(userRepository.existsById(Mockito.anyInt())).thenReturn(true);
        CreateCarDto createCarDto = new CreateCarDto("BMW", "530",
                2019, "white", "2201 887900", "А001АА155");
        Car testCar = new ModelMapper().map(createCarDto, Car.class);

        when(carRepository.save(Mockito.any(Car.class))).thenReturn(testCar);

        int ownerId = 1;
        Car car = carService.create(ownerId, createCarDto);

        verify(carRepository).save(Mockito.any(Car.class));

        assertEquals(ownerId, car.getOwnerId());
        assertEquals(createCarDto.getBrand(), car.getBrand());
        assertEquals(createCarDto.getModel(), car.getModel());
        assertEquals(createCarDto.getColor(), car.getColor());
        assertEquals(createCarDto.getDocumentNumber(), car.getDocumentNumber());
        assertEquals(createCarDto.getRegistrationNumber(), car.getRegistrationNumber());
        assertEquals(0, car.getPricePerDay());
        assertTrue(car.getReviews().isEmpty());
    }

    @Test
    void delete() {
    }

    @Test
    void getById() {
    }

    @Test
    void getOwnerCars() {
    }

    @Test
    void search() {
    }

    @Test
    void updatePrice() {
    }
}
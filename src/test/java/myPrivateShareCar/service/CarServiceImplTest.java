package myPrivateShareCar.service;

import myPrivateShareCar.dto.CreateCarDto;
import myPrivateShareCar.exception.NotCreateException;
import myPrivateShareCar.model.Car;
import myPrivateShareCar.repository.BookingRepository;
import myPrivateShareCar.repository.CarRepository;
import myPrivateShareCar.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {

    private CarService carService;
    @Mock
    private CarRepository carRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;

    @BeforeEach
    public void setUp() {
        carService = new CarServiceImpl(carRepository, userRepository, bookingRepository, new ModelMapper());
    }

    @Test
    void createCar() {
        int ownerId = 55;
        when(userRepository.existsById(Mockito.anyInt())).thenReturn(true);
        CreateCarDto createCarDto = new CreateCarDto("BMW", "530",
                2019, "white", "2201 887900", "А001АА155");
        Car testCar = new ModelMapper().map(createCarDto, Car.class);
        testCar.setOwnerId(ownerId);

        when(carRepository.save(Mockito.any(Car.class))).thenReturn(testCar);

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
    void createCarWithNotExistUser() {
        int ownerId = 55;
        when(userRepository.existsById(Mockito.anyInt())).thenReturn(false);
        CreateCarDto createCarDto = new CreateCarDto("BMW", "530",
                2019, "white", "2201 887900", "А001АА155");

        assertThrows(NotCreateException.class, () -> {
            carService.create(ownerId, createCarDto);
        });

        verify(userRepository).existsById(Mockito.any(Integer.class));
    }

    @Test
    void deleteCar() {
        int ownerId = 55;
        int customCarId = 10;
        Car car = new Car(customCarId, "BMW", "530", 2019, "white",
                "2201 887900", "А001АА155", ownerId, 5000, new ArrayList<>());

        when(carRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(car));

        // todo разобраться, почему ошибка. Интежер не подходит
        doNothing().when(carRepository.deleteById(customCarId));

        carService.delete(ownerId, customCarId);

        verify(carRepository).findById(Mockito.any(Integer.class));
        verify(carRepository).deleteById(Mockito.any(Integer.class));
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
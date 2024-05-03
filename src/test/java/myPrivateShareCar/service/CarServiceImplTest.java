
package myPrivateShareCar.service;

import myPrivateShareCar.dto.CarDto;
import myPrivateShareCar.dto.CreateCarDto;
import myPrivateShareCar.dto.CreateUserDto;
import myPrivateShareCar.exception.NotFoundException;
import myPrivateShareCar.exception.PermissionDeniedException;
import myPrivateShareCar.model.Car;
import myPrivateShareCar.model.Passport;
import myPrivateShareCar.model.User;
import myPrivateShareCar.repository.BookingRepository;
import myPrivateShareCar.repository.CarRepository;
import myPrivateShareCar.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {
    private CarService carService;
    @Mock
    private UserPrincipalService userPrincipalService;
    @Mock
    private CarRepository carRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    private final ModelMapper modelMapper = new ModelMapper();

    Car car;

    @BeforeEach
    public void setUp() {
        carService = new CarServiceImpl(carRepository, userRepository, bookingRepository,
                userPrincipalService, modelMapper);
    }

    @Test
    @DisplayName("Корректное создание автомобиля")
    void createCarTest() {
        int ownerId = 55;

        createTestUser(ownerId);

        Principal mockPrincipal = Mockito.mock(Principal.class);

        CreateCarDto createCarDto = new CreateCarDto("BMW", "530",
                2019, "white", "2201 887900",
                "А001АА155", 3500);

        when(carRepository.save(Mockito.any(Car.class))).then(returnsFirstArg());

        Car car = carService.create(createCarDto, mockPrincipal);

        verify(carRepository).save(Mockito.any(Car.class));
        assertNotNull(car);
        assertEquals(ownerId, car.getOwnerId());
        assertEquals(createCarDto.getBrand(), car.getBrand());
        assertEquals(createCarDto.getModel(), car.getModel());
        assertEquals(createCarDto.getColor(), car.getColor());
        assertEquals(createCarDto.getDocumentNumber(), car.getDocumentNumber());
        assertEquals(createCarDto.getRegistrationNumber(), car.getRegistrationNumber());
        assertEquals(createCarDto.getPricePerDay(), car.getPricePerDay());
        assertTrue(car.getReviews().isEmpty());
    }


    @Test
    @DisplayName("Корректное удаление автомобиля")
    void deleteCarTest() {
        int ownerId = 57;
        int customCarId = 11;

        createTestUser(ownerId);

        Principal mockPrincipal = Mockito.mock(Principal.class);

        createTestCar(customCarId, ownerId);

        when(carRepository.findById(anyInt())).thenReturn(Optional.of(car));

        doNothing().when(carRepository).deleteById(customCarId);

        carService.delete(customCarId, mockPrincipal);

        verify(carRepository).findById(anyInt());
        verify(carRepository).deleteById(anyInt());
    }

    @Test
    @DisplayName("Удаление несуществующего автомобиля")
    void deleteCarWithNotExistCarTest() {
        int customCarId = 15;

        when(carRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> carService.delete(customCarId, null));

        verify(carRepository, never()).deleteById(anyInt());
    }

    @Test
    @DisplayName("Удаление автомобиля не владельцем")
    void deleteCarByNotOwnerTest() {
        int ownerId = 59;
        int customCarId = 17;

        createTestUser(ownerId);

        Principal mockPrincipal = Mockito.mock(Principal.class);

        createTestCar(customCarId, ownerId + 1);

        when(carRepository.findById(anyInt())).thenReturn(Optional.of(car));

        assertThrows(PermissionDeniedException.class, () -> carService.delete(customCarId, mockPrincipal));

        verify(carRepository, never()).deleteById(anyInt());
    }

    @Test
    @DisplayName("Корректное получение автомобиля по id")
    void getCarByIdTest() {
        int ownerId = 55;
        int customCarId = 19;

        createTestCar(customCarId, ownerId);

        when(carRepository.findById(anyInt())).thenReturn(Optional.of(car));

        CarDto carDto = carService.getById(customCarId);

        verify(carRepository).findById(anyInt());
        assertEquals(customCarId, carDto.getId());
        assertEquals(car.getBrand(), carDto.getBrand());
        assertEquals(car.getModel(), carDto.getModel());
        assertEquals(car.getYearOfManufacture(), carDto.getYearOfManufacture());
        assertEquals(car.getColor(), carDto.getColor());
        assertEquals(car.getPricePerDay(), carDto.getPricePerDay());
    }

    @Test
    @DisplayName("Получение по id несуществующего автомобиля")
    void getCarByIdWithNotExistCarTest() {
        int customCarId = 20;

        when(carRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> carService.getById(customCarId));
    }

    @Test
    @DisplayName("Корректное обновление цены владельцем")
    void correctUpdatePriceFromOwnerTest() {
        int ownerId = 57;
        int customCarId = 21;
        int newPrice = 5500;

        createTestUser(ownerId);

        Principal mockPrincipal = Mockito.mock(Principal.class);

        createTestCar(customCarId, ownerId);

        when(carRepository.findById(customCarId)).thenReturn(Optional.of(car));

        carService.updatePrice(customCarId, newPrice, mockPrincipal);

        verify(carRepository).save(Mockito.any(Car.class));
    }

    @Test
    @DisplayName("Обновление цены несуществующего автомобиля")
    void incorrectUpdatePriceFromOwnerTest() {
        int customCarId = 23;

        when(carRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> carService.updatePrice(customCarId, 0, null));
    }

    @Test
    @DisplayName("Обновление цены не владельцем")
    void correctUpdatePriceFromNotOwnerTest() {
        int ownerId = 59;
        int customCarId = 25;
        int newPrice = 5500;

        createTestUser(ownerId);

        Principal mockPrincipal = Mockito.mock(Principal.class);

        createTestCar(customCarId, ownerId + 1);

        when(carRepository.findById(customCarId)).thenReturn(Optional.of(car));

        assertThrows(PermissionDeniedException.class, () -> carService
                .updatePrice(customCarId, newPrice, mockPrincipal));

        verify(carRepository, never()).save(Mockito.any(Car.class));
    }

    private void createTestUser(int id) {
        CreateUserDto createUserDto = new CreateUserDto("Иван", "Иванов", "ivan@ivanov.ru",
                LocalDate.of(2000, 10, 1), new Passport("1234", "123456",
                LocalDate.of(2014, 5, 15), "МВД №1"),
                null, "password1");
        User testUser = modelMapper.map(createUserDto, User.class);
        testUser.setId(id);

        when(userPrincipalService.getUserFromPrincipal(Mockito.any(Principal.class))).thenReturn(testUser);
    }

    private void createTestCar(int customCarId, int ownerId) {
        car = new Car(customCarId, "BMW", "530", 2019, "white",
                "2201 887900", "А001АА155", ownerId, 5000, new ArrayList<>());
    }
}

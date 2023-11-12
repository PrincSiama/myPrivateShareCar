package myPrivateShareCar.service;

import myPrivateShareCar.dto.CreateCarDto;
import myPrivateShareCar.model.Car;
import myPrivateShareCar.repository.BookingRepository;
import myPrivateShareCar.repository.CarRepository;
import myPrivateShareCar.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    public void createCarTest() {
        when(userRepository.existsById(Mockito.anyInt())).thenReturn(true);
        int ownerId = 3;
        CreateCarDto createCarDto = new CreateCarDto("BMW", "530",
                2019, "white", "2201 887900", "А001АА155");
        Car testCar = carService.create(ownerId, createCarDto);
        assertNotNull(testCar);
        assertEquals(ownerId, testCar.getOwnerId());
    }
}
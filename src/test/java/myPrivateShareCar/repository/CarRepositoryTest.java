package myPrivateShareCar.repository;

import myPrivateShareCar.dto.CreateCarDto;
import myPrivateShareCar.model.Car;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class CarRepositoryTest {
    @Autowired
    private CarRepository carRepository;
    private final ModelMapper mapper = new ModelMapper();

    @Test
    @DirtiesContext
    @DisplayName("Поиск автомобиля по id владельца")
    public void findByOwnerIdTest() {
        int customOwnerId = 5;
        CreateCarDto createCarDto = new CreateCarDto("BMW", "530",
                2019, "white", "1277 507800",
                "А111АА147", 2500);
        Car car = mapper.map(createCarDto, Car.class);
        car.setOwnerId(customOwnerId);

        assertEquals(createCarDto.getBrand(), car.getBrand());
        assertEquals(createCarDto.getModel(), car.getModel());
        assertEquals(createCarDto.getYearOfManufacture(), car.getYearOfManufacture());
        assertEquals(createCarDto.getColor(), car.getColor());
        assertEquals(createCarDto.getDocumentNumber(), car.getDocumentNumber());
        assertEquals(createCarDto.getRegistrationNumber(), car.getRegistrationNumber());
        assertEquals(customOwnerId, car.getOwnerId());

        Car carFromRepository = carRepository.save(car);
        int carId = carFromRepository.getId();
        int ownerId = carFromRepository.getOwnerId();

        assertEquals(customOwnerId, ownerId);

        List<Car> carsFromRepository = carRepository.findByOwnerId(ownerId, Pageable.unpaged()); //Pageable.ofSize(5)

        assertEquals(1, carsFromRepository.size());
        assertEquals(carId, carsFromRepository.get(0).getId());
    }

    @Test
    @DirtiesContext
    @DisplayName("Поиск автомобилей по id владельца")
    public void testFindByOwnerIdWithTwoCar() {
        int customOwnerId = 5;
        CreateCarDto createCarDto1 = new CreateCarDto("BMW", "530",
                2019, "white", "1277 507800",
                "А111АА147", 2500);
        Car car1 = mapper.map(createCarDto1, Car.class);
        car1.setOwnerId(customOwnerId);
        CreateCarDto createCarDto2 = new CreateCarDto("AUDI", "A6",
                2020, "red", "1344 168003",
                "А006АА47", 3000);
        Car car2 = mapper.map(createCarDto2, Car.class);
        car2.setOwnerId(customOwnerId);
        CreateCarDto createCarDto3 = new CreateCarDto("LADA", "2106",
                1995, "green", "0418 120087",
                "В210АХ06", 500);
        Car car3 = mapper.map(createCarDto3, Car.class);
        car3.setOwnerId(3);

        carRepository.save(car1);
        carRepository.save(car2);
        carRepository.save(car3);

        List<Car> carsFromRepository = carRepository.findByOwnerId(customOwnerId, Pageable.unpaged());

        assertEquals(2, carsFromRepository.size());
        assertTrue(carsFromRepository.containsAll(List.of(car1, car2)));

        List<Car> emptyList = carRepository.findByOwnerId(2, Pageable.unpaged());
        assertTrue(emptyList.isEmpty());
    }
}


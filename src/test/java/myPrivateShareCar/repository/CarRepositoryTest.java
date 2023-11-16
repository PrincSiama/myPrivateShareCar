package myPrivateShareCar.repository;

import myPrivateShareCar.dto.CreateCarDto;
import myPrivateShareCar.model.Car;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class CarRepositoryTest {

    @Autowired
    private CarRepository carRepository;
    private final ModelMapper mapper = new ModelMapper();

    @Test
    @DisplayName("Поиск автомобиля по id владельца")
    public void findByOwnerIdTest() {
        int testOwnerId = 5;
        CreateCarDto createCarDto1 = new CreateCarDto("BMW", "530",
                2019, "white", "1277 507800", "А111АА147");
        Car car1 = mapper.map(createCarDto1, Car.class);
        car1.setOwnerId(testOwnerId);

        assertEquals(createCarDto1.getBrand(), car1.getBrand());
        assertEquals(createCarDto1.getModel(), car1.getModel());
        assertEquals(createCarDto1.getYearOfManufacture(), car1.getYearOfManufacture());
        assertEquals(createCarDto1.getColor(), car1.getColor());
        assertEquals(createCarDto1.getDocumentNumber(), car1.getDocumentNumber());
        assertEquals(createCarDto1.getRegistrationNumber(), car1.getRegistrationNumber());
        assertEquals(testOwnerId, car1.getOwnerId());

        Car car1FromRepository = carRepository.save(car1);
        int car1Id = car1FromRepository.getId();
        int ownerId = car1FromRepository.getOwnerId();

        assertEquals(testOwnerId, ownerId);

        List<Car> carsFromRepository = carRepository.findByOwnerId(ownerId, Pageable.unpaged()); //Pageable.ofSize(5)

        assertEquals(1, carsFromRepository.size());
        assertEquals(car1Id, carsFromRepository.get(0).getId());
    }

    @Test
    @DisplayName("Поиск автомобилей по id владельца")
    public void testFindByOwnerIdWithTwoCar() {
        int ownerId = 5;
        CreateCarDto createCarDto1 = new CreateCarDto("BMW", "530",
                2019, "white", "1277 507800", "А111АА147");
        Car car1 = mapper.map(createCarDto1, Car.class);
        car1.setOwnerId(ownerId);
        CreateCarDto createCarDto2 = new CreateCarDto("AUDI", "A6",
                2020, "red", "1344 168003", "А006АА47");
        Car car2 = mapper.map(createCarDto2, Car.class);
        car2.setOwnerId(ownerId);
        CreateCarDto createCarDto3 = new CreateCarDto("LADA", "2106",
                1995, "green", "0418 120087", "В210АХ06");
        Car car3 = mapper.map(createCarDto3, Car.class);
        car3.setOwnerId(3);

        carRepository.save(car1);
        carRepository.save(car2);
        carRepository.save(car3);

        List<Car> carsFromRepository = carRepository.findByOwnerId(ownerId, Pageable.unpaged()); //Pageable.ofSize(5)

        assertEquals(2, carsFromRepository.size());
        assertTrue(carsFromRepository.containsAll(List.of(car1, car2)));

        List<Car> emptyList = carRepository.findByOwnerId(2, Pageable.unpaged());
        assertTrue(emptyList.isEmpty());
    }

    // если реализовать поиск с помощью спецификации, то эти методы не нужны и тестировать их не надо?
    @Test
    public void findAllContainingText() {
        int ownerId = 5;
        CreateCarDto createCarDto1 = new CreateCarDto("BMW", "530",
                2019, "white", "1277 507800", "А111АА147");
        Car car1 = mapper.map(createCarDto1, Car.class);
        car1.setOwnerId(ownerId);
        CreateCarDto createCarDto2 = new CreateCarDto("AUDI", "A6",
                2020, "red", "1344 168003", "А006АА47");
        Car car2 = mapper.map(createCarDto2, Car.class);
        car2.setOwnerId(ownerId);

        int car1Id = carRepository.save(car1).getId();
        int car2Id = carRepository.save(car2).getId();

        List<Car> findCarByContainingTextInColor = carRepository.findAllContainingText("whi", Pageable.unpaged());

        assertEquals(1, findCarByContainingTextInColor.size());
        assertEquals(car1Id, findCarByContainingTextInColor.get(0).getId());

        List<Car> findCarByContainingTextInBrand = carRepository.findAllContainingText("aud", Pageable.unpaged());
        assertEquals(1, findCarByContainingTextInBrand.size());
        assertEquals(car2Id, findCarByContainingTextInBrand.get(0).getId());

        List<Car> emptyList = carRepository.findAllContainingText("mercedes", Pageable.unpaged());
        assertTrue(emptyList.isEmpty());
    }

}


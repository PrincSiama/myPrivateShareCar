package myPrivateShareCar.service;

import lombok.AllArgsConstructor;
import myPrivateShareCar.dto.CarDto;
import myPrivateShareCar.dto.CreateCarDto;
import myPrivateShareCar.exception.NotCreatedException;
import myPrivateShareCar.exception.NotFoundException;
import myPrivateShareCar.exception.PermissionDeniedException;
import myPrivateShareCar.model.Car;
import myPrivateShareCar.repository.BookingRepository;
import myPrivateShareCar.repository.CarRepository;
import myPrivateShareCar.repository.UserRepository;
import myPrivateShareCar.specification.CarSpecification;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ModelMapper mapper;

    @Override
    public Car create(int ownerId, CreateCarDto createCarDto) {
        if (userRepository.existsById(ownerId)) {
            Car car = mapper.map(createCarDto, Car.class);
            car.setOwnerId(ownerId);
            return carRepository.save(car);
        }
        throw new NotCreatedException("Невозможно создать автомобиль. Владелец с id " + ownerId + " не найден");
    }

    @Override
    public void delete(int ownerId, int carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new NotFoundException("Невозможно удалить автомобиль. Автомобиль с id " + carId +
                        " не найден"));

        if (ownerId == car.getOwnerId()) {
            carRepository.deleteById(carId);
        } else {
            throw new PermissionDeniedException("Невозможно удалить автомобиль с id " + carId +
                    ". Удалить может только владелец. Пользователь с id " + ownerId + " не является владельцем" +
                    " автомобиля с id " + carId);
        }
    }

    @Override
    public CarDto getById(int id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Невозможно получить автомобиль. Автомобиль с id " + id +
                        " не найден"));

        return mapper.map(car, CarDto.class);
    }

    @Override
    public List<CarDto> getOwnerCars(int ownerId, int page, int size) {
        if (userRepository.existsById(ownerId)) {
            return carRepository.findByOwnerId(ownerId, PageRequest.of(page, size)).stream()
                    .map(car -> mapper.map(car, CarDto.class)).collect(Collectors.toList());
        }
        throw new NotFoundException("Невозможно получить автомобили пользователя. Пользователь с id "
                + ownerId + " не найден");
    }

    @Override
    public List<CarDto> search(String text, LocalDate startRent, LocalDate endRent, int page, int size) {
        CarSpecification carSpecification = new CarSpecification(bookingRepository);
        List<Specification<Car>> specifications = carSpecification
                .searchParametersToSpecifications(text, startRent, endRent);

        return carRepository.findAll((specifications.stream().reduce(Specification::and)
                                .orElse((root, query, criteriaBuilder) -> criteriaBuilder.conjunction())),
                        PageRequest.of(page, size)).stream().map(car -> mapper.map(car, CarDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public void updatePrice(int carId, int ownerId, int pricePerDay) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new NotFoundException("Автомобиль с id " + carId + " не найден"));
        if (ownerId == car.getOwnerId()) {
            car.setPricePerDay(pricePerDay);
            carRepository.save(car);
        } else {
            throw new PermissionDeniedException("Невозможно установить цену аренды на автомобиль с id " + carId +
                    ". Установить цену может только владелец. Пользователь с id " + ownerId + " не является владельцем"
                    + " автомобиля с id " + carId);
        }
    }
}

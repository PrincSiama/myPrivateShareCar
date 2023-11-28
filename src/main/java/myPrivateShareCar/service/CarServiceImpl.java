package myPrivateShareCar.service;

import lombok.AllArgsConstructor;
import myPrivateShareCar.dto.CarDto;
import myPrivateShareCar.dto.CreateCarDto;
import myPrivateShareCar.exception.NotCreateException;
import myPrivateShareCar.exception.NotFoundException;
import myPrivateShareCar.exception.PermissionDeniedException;
import myPrivateShareCar.model.Booking;
import myPrivateShareCar.model.BookingStatus;
import myPrivateShareCar.model.Car;
import myPrivateShareCar.repository.BookingRepository;
import myPrivateShareCar.repository.CarRepository;
import myPrivateShareCar.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final UserRepository userRepository;
    //todo исключить bookingRepository
    private final BookingRepository bookingRepository;
    private final ModelMapper mapper;

    @Override
    public Car create(int ownerId, CreateCarDto createCarDto) {
        if (userRepository.existsById(ownerId)) {
            Car car = mapper.map(createCarDto, Car.class);
            car.setOwnerId(ownerId);
            return carRepository.save(car);
        }
        throw new NotCreateException("Невозможно создать автомобиль. Владелец с id " + ownerId + " не найден");
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
        List<Specification<Car>> specifications = searchParametersToSpecifications(text, startRent, endRent);

        return carRepository.findAll((specifications.stream().reduce(Specification::and)
                                .orElse((root, query, criteriaBuilder) -> criteriaBuilder.conjunction())),
                        PageRequest.of(page, size)).stream().map(car -> mapper.map(car, CarDto.class))
                .collect(Collectors.toList());
    }

    private Specification<Car> findText(String text) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("brand")), "%" + text.toLowerCase() + "%"),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("model")), "%" + text.toLowerCase() + "%"),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("color")), "%" + text.toLowerCase() + "%"));
    }

    private Specification<Booking> findBookingByDate(LocalDate startRent, LocalDate endRent) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.equal(root.get("bookingStatus"), BookingStatus.APPROVED),
                criteriaBuilder.or(
                        criteriaBuilder.and(
                                criteriaBuilder.greaterThanOrEqualTo(root.get("endRent"), startRent),
                                criteriaBuilder.lessThanOrEqualTo(root.get("startRent"), endRent)),
                        criteriaBuilder.and(
                                criteriaBuilder.and(
                                        criteriaBuilder.lessThanOrEqualTo(root.get("startRent"), startRent),
                                        criteriaBuilder.greaterThanOrEqualTo(root.get("endRent"), startRent)),
                                criteriaBuilder.and(
                                        criteriaBuilder.lessThanOrEqualTo(root.get("startRent"), endRent),
                                        criteriaBuilder.greaterThanOrEqualTo(root.get("endRent"), endRent))))));
    }

    // todo где-то здесь проблема
    //  приходит пустой список при запросе с датами, когда обе машины свободны
    //  но если выбирать даты когда одна машина занята, то вторая машина возвращается в списке правильно
    private Specification<Car> rentDate(LocalDate startRent, LocalDate endRent) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.not(criteriaBuilder.in(root.get("id"))
                .value(bookingRepository.findAll(findBookingByDate(startRent, endRent))
                        .stream().map(booking -> booking.getCar().getId()).collect(Collectors.toList())));
    }

    private List<Specification<Car>> searchParametersToSpecifications(String text,
                                                                      LocalDate startRent, LocalDate endRent) {
        List<Specification<Car>> specifications = new ArrayList<>();

        specifications.add(text == null ? null : findText(text));
        specifications.add((startRent == null && endRent == null) ? null : rentDate(startRent, endRent));

        return specifications.stream().filter(Objects::nonNull).collect(Collectors.toList());
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
                    ". Установить цену может только владелец. Пользователь с id " + ownerId + " не является владельцем" +
                    " автомобиля с id " + carId);
        }
    }
}

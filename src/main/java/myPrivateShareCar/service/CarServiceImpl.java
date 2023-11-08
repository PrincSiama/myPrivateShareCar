package myPrivateShareCar.service;

import lombok.AllArgsConstructor;
import myPrivateShareCar.dto.CarDto;
import myPrivateShareCar.dto.CreateCarDto;
import myPrivateShareCar.exception.NotCreateException;
import myPrivateShareCar.exception.NotFoundException;
import myPrivateShareCar.exception.PermissionDeniedException;
import myPrivateShareCar.model.Car;
import myPrivateShareCar.repository.CarRepository;
import myPrivateShareCar.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class CarServiceImpl implements CarService {
    private CarRepository carRepository;
    private UserRepository userRepository;
    private ModelMapper mapper;

    // REVIEW: кажется, что работу с отзывами лучше вынести в отдельный сервис. Для меня зависимость car-service от
    // booking репозитория выглядит нелогично. Как будто бы связь должна быть в другую сторону: бронирование зависит
    // от машины, но не наоборот


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

    /*@Override
    public List<CarDto> find(String text, int page, int size) {
        return carRepository.findAllContainingText(text, PageRequest.of(page, size)).stream()
                .map(car -> mapper.map(car, CarDto.class)).collect(Collectors.toList());
    }*/

    /*@Override
    public List<CarDto> search(String text, LocalDate startRent, LocalDate endRent, int page, int size) {
        if (text != null && startRent == null && endRent == null) {
            return carRepository.findAllContainingText(text, PageRequest.of(page, size)).stream()
                    .map(car -> mapper.map(car, CarDto.class)).collect(Collectors.toList());
        } else if (text == null && startRent != null && endRent != null) {
            return carRepository.carsByRentDate(startRent, endRent, PageRequest.of(page, size)).stream()
                    .map(car -> mapper.map(car, CarDto.class)).collect(Collectors.toList());
        } else if (text != null && startRent != null && endRent != null) {
            return carRepository.carsByRentDateAndContainingText(text, startRent, endRent,
                            PageRequest.of(page, size)).stream()
                    .map(car -> mapper.map(car, CarDto.class)).collect(Collectors.toList());
        } else {
            return carRepository.findAll().stream()
                    .map(car -> mapper.map(car, CarDto.class)).collect(Collectors.toList());
        }
    }*/

    @Override
    public List<CarDto> search(String text, LocalDate startRent, LocalDate endRent, int page, int size) {
        List<Specification<Car>> specifications = searchParametersToSpecifications(text, startRent, endRent);

        return carRepository.findAll((specifications.stream().reduce(Specification::and)
                                .orElse((root, query, criteriaBuilder) -> criteriaBuilder.conjunction())),
                        PageRequest.of(page, size)).stream().map(car -> mapper.map(car, CarDto.class))
                .collect(Collectors.toList());
    }

    private Specification<Car> textInBrand(String text) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(
                root.get("brand")), "%" + text.toLowerCase() + "%");
    }

    private Specification<Car> textInModel(String text) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(
                root.get("model")), "%" + text.toLowerCase() + "%");
    }

    private Specification<Car> textInColor(String text) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(
                root.get("color")), "%" + text.toLowerCase() + "%");
    }

    // todo поиск по тексту работает, с датами проблема, не могу сджойнить
    private Specification<Car> startRent(LocalDate startRent) {

        return (new Specification<Car>() {
            @Override
            public Predicate toPredicate(Root<Car> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

//                Join<Car, Booking>  carBookingJoin = root.join();

                return criteriaBuilder.not(criteriaBuilder.greaterThanOrEqualTo(
                        root.join("car_id", JoinType.LEFT), startRent));
            }
        });
    }

    private Specification<Car> endRent(LocalDate endRent) {
        return ((root, query, criteriaBuilder) -> criteriaBuilder.not(criteriaBuilder.lessThanOrEqualTo(
                root.join("car_id", JoinType.LEFT), endRent)));
    }

    private List<Specification<Car>> searchParametersToSpecifications(String text,
                                                                      LocalDate startRent, LocalDate endRent) {
        List<Specification<Car>> specifications = new ArrayList<>();
        if (text != null) {
            Specification<Car> findIn = Stream.of(textInBrand(text), textInModel(text), textInColor(text))
                    .reduce(Specification::or).get();
            specifications.add(findIn);
        }
        specifications.add(startRent == null ? null : startRent(startRent));
        specifications.add(endRent == null ? null : endRent(endRent));

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

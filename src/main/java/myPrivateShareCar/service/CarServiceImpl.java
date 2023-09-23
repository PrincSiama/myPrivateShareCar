package myPrivateShareCar.service;

import lombok.RequiredArgsConstructor;
import myPrivateShareCar.dto.CreateCarDto;
import myPrivateShareCar.dto.GetCarDto;
import myPrivateShareCar.dto.GetReviewDto;
import myPrivateShareCar.exception.NotFoundException;
import myPrivateShareCar.exception.NotOwnerException;
import myPrivateShareCar.model.BookingStatus;
import myPrivateShareCar.model.Car;
import myPrivateShareCar.model.Review;
import myPrivateShareCar.repository.JpaBookingRepository;
import myPrivateShareCar.repository.JpaCarRepository;
import myPrivateShareCar.repository.JpaReviewRepository;
import myPrivateShareCar.repository.JpaUserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final JpaCarRepository carRepository;
    private final JpaUserRepository userRepository;
    private final JpaReviewRepository reviewRepository;
    private final JpaBookingRepository bookingRepository;
    private final ModelMapper mapper;

    //@Transactional настроить?
    @Override
    public Car create(Integer ownerId, CreateCarDto createCarDto) {
        if (userRepository.existsById(ownerId)) {
            Car car = mapper.map(createCarDto, Car.class);
            car.setOwnerId(ownerId);
            return carRepository.save(car);
        }
        throw new NotFoundException("Невозможно создать автомобиль. Владелец с id " + ownerId + " не найден");
    }

    //@Transactional
    @Override
    public void delete(Integer ownerId, Integer carId) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new NotFoundException("Невозможно удалить автомобиль. Автомобиль с id " + carId + " не найден"));

        if (ownerId.equals(car.getOwnerId())) {
            carRepository.deleteById(carId);
        } else {
            throw new NotOwnerException("Невозможно удалить автомобиль. Удалить может только владелец");
        }
    }

    @Override
    public GetCarDto getById(Integer id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Невозможно получить автомобиль. Автомобиль с id " + id + " не найден"));

        return mapper.map(car, GetCarDto.class);
    }

    //@Transactional
    @Override
    public List<Car> getOwnerCars(Integer ownerId) {
        if (userRepository.existsById(ownerId)) {
            return carRepository.findByOwnerId(ownerId);
        }
        throw new NotFoundException("Невозможно получить автомобили пользователя. Пользователь с id "
                + ownerId + " не найден");
    }

    @Override
    public List<Car> getAll() {
        return carRepository.findAll(PageRequest.of(0, 5)).stream().collect(Collectors.toList());
    }

    @Override
    public List<Car> find(String text) {
        return carRepository.findAllContainingText(text);
    }

    //@Transactional
    @Override
    public List<GetReviewDto> getReviewByCar(Integer carId) {
        if (carRepository.existsById(carId)) {
            List<GetReviewDto> reviewList = new ArrayList<>();
            for (Review review : reviewRepository.findByIdCar(carId)) {
                GetReviewDto getReviewDto = mapper.map(review, GetReviewDto.class);
                reviewList.add(getReviewDto);
            }
            return reviewList;
        }
        throw new NotFoundException("Невозможно получить отзывы по автомобилю. " +
                "Автомобиль с id " + carId + " не найден");
    }

    //@Transactional
    @Override
    public void addReview(Integer carId, Integer userId, String text) {
        if (carRepository.existsById(carId) && userRepository.existsById(userId)) {
            if (!bookingRepository.findByUserIdAndCarIdAndBookingStatusOrderByStartRentAsc(userId, carId,
                            BookingStatus.FINISHED).isEmpty()) {
                reviewRepository.save(new Review(carId, userId, text, LocalDate.now()));
            } else {
                throw new NotFoundException("Невозможно добавить отзыв пользователя с id " + userId +
                        " к автомобилю с id " + carId +
                        ". У пользователя отсутствуют завершенные бронирования для данного автомобиля");
            }
        } else {
            throw new NotFoundException("Введены неверные данные");
        }
    }
}

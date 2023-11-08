package myPrivateShareCar.service;

import lombok.AllArgsConstructor;
import myPrivateShareCar.dto.BookingDto;
import myPrivateShareCar.exception.NotCreateException;
import myPrivateShareCar.exception.NotFoundException;
import myPrivateShareCar.exception.PermissionDeniedException;
import myPrivateShareCar.model.Booking;
import myPrivateShareCar.model.BookingStatus;
import myPrivateShareCar.model.Car;
import myPrivateShareCar.model.User;
import myPrivateShareCar.repository.BookingRepository;
import myPrivateShareCar.repository.CarRepository;
import myPrivateShareCar.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ModelMapper mapper;

    @Override
    public BookingDto create(int userId, int carId, LocalDate startRent, int durationRent) {
        LocalDate endRent = startRent.plusDays(durationRent);
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new NotFoundException("Невозможно создать бронирование. " +
                        "Автомобиль с id " + carId + " не найден"));
        User user = userRepository.findById(userId).filter(user1 -> user1.getDriverLicense().getSeries() != null)
                .orElseThrow(() -> new NotCreateException("Невозможно создать бронирование автомобиля." +
                        " У пользователь с id " + userId + " отсутствует информация о водительском удостоверении"));

        if (bookingRepository.bookingByRentDate(carId, startRent, endRent).isEmpty()) {
            return mapper.map(bookingRepository.save(new Booking(user, car, startRent, durationRent, endRent)),
                    BookingDto.class);
        }
        throw new NotCreateException("Невозможно создать бронирование автомобиля с id " + carId + "." +
                " Автомобиль уже забронирован на указанные даты");
    }

    @Override
    public BookingDto updateStatus(int ownerId, int bookingId, BookingStatus status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Невозможно подтвердить бронирование. " +
                        "Бронирование с id " + bookingId + " не найдено"));
        Car car = carRepository.findById(booking.getCar().getId())
                .orElseThrow(() -> new NotFoundException("Невозможно подтвердить бронирование. " +
                        "Автомобиль с id " + booking.getCar().getId() + " не найден"));
        if (ownerId == car.getOwnerId()) {
            booking.setBookingStatus(status);
            return mapper.map(bookingRepository.save(booking), BookingDto.class);
        }
        throw new PermissionDeniedException("Невозможно подтвердить бронирование с id " + bookingId + ". " +
                "Подтвердить может только владелец. Пользователь с id " + ownerId + " не является владельцем автомобиля" +
                " с id " + car.getId());
    }

    @Override
    public BookingDto getBookingById(int bookingId, int userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Невозможно получить информацию о бронировании. " +
                        "Бронирование с id " + bookingId + " не найдено"));
        if (userId == booking.getUser().getId() || userId == booking.getCar().getOwnerId()) {
            return mapper.map(booking, BookingDto.class);
        }
        throw new PermissionDeniedException("Невозможно получить информацию о бронировании с id " + bookingId +
                ". Получить информацию может создатель бронирования или владелец автомобиля");
    }

    @Override
    public List<BookingDto> getUserBookings(int userId, BookingStatus status) {
        List<Booking> bookings;
        if (userRepository.existsById(userId)) {
            if (status == null) {
                bookings = bookingRepository.findByUser_IdOrderByStartRentAsc(userId);
            } else {
                bookings = bookingRepository
                        .findAllByUser_IdAndBookingStatusOrderByStartRentAsc(userId, status);
            }
        } else {
            throw new NotFoundException("Невозможно получить список бронирований. " +
                    "Пользователь с id " + userId + " не найден");
        }
        return bookings.stream()
                .map(booking -> mapper.map(booking, BookingDto.class)).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getOwnerBookings(int ownerId, BookingStatus status) {
        List<Booking> bookings;
        if (userRepository.existsById(ownerId)) {
            if (!carRepository.findByOwnerId(ownerId, PageRequest.of(0, 5)).isEmpty()) {
                if (status == null) {
                    bookings = bookingRepository.findByOwnerId(ownerId);
                } else {
                    bookings = bookingRepository.findByOwnerIdAndStatus(ownerId, status);
                }
            } else {
                throw new PermissionDeniedException("Невозможно получить список бронирований. " +
                        "У пользователя с id " + ownerId + " отсутствуют автомобили для аренды");
            }
        } else {
            throw new NotFoundException("Невозможно получить список бронирований. " +
                    "Пользователь с id " + ownerId + " не найден");
        }
        return bookings.stream()
                .map(booking -> mapper.map(booking, BookingDto.class)).collect(Collectors.toList());
    }

}

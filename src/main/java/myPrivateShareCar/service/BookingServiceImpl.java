package myPrivateShareCar.service;

import lombok.RequiredArgsConstructor;
import myPrivateShareCar.dto.GetBookingDto;
import myPrivateShareCar.exception.NotFoundException;
import myPrivateShareCar.exception.NotOwnerException;
import myPrivateShareCar.model.Booking;
import myPrivateShareCar.model.BookingStatus;
import myPrivateShareCar.model.Car;
import myPrivateShareCar.repository.JpaBookingRepository;
import myPrivateShareCar.repository.JpaCarRepository;
import myPrivateShareCar.repository.JpaUserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final JpaCarRepository carRepository;
    private final JpaUserRepository userRepository;
    private final JpaBookingRepository bookingRepository;
    private final ModelMapper mapper;

    @Override
    public Booking create(Integer userId, Integer carId, LocalDate startRent, Integer durationRent) {
        if (userRepository.existsById(userId) && carRepository.existsById(carId)) {
            return bookingRepository.save(new Booking(userId, carId, startRent, durationRent));
        }
        throw new NotFoundException("Невозможно создать бронирование. Пользователь или автомобиль не найдены");
    }

    @Override
    public Booking approved(Integer ownerId, Integer bookingId, boolean isApprove) {
        Booking booking = bookingRepository.findById(bookingId) // todo можно получить за один запрос с join
                // но как сохранять полученный результат, чтобы получить из него ownerId? и нам все равно нужен booking
                // чтобы сохранить его в базу с обновленным статусом
                .orElseThrow(() -> new NotFoundException("Невозможно подтвердить бронирование. " +
                        "Бронирование с id " + bookingId + " не найдено"));
        Car car = carRepository.findById(booking.getCarId())
                .orElseThrow(() -> new NotFoundException("Невозможно подтвердить бронирование. " +
                        "Автомобиль с id " + booking.getCarId() + " не найден"));
        if (ownerId.equals(car.getOwnerId())) {
            if (isApprove) {
                booking.setBookingStatus(BookingStatus.APPROVED);
            } else {
                booking.setBookingStatus(BookingStatus.REJECTED);
            }
            return bookingRepository.save(booking);
        }
        throw new NotOwnerException("Невозможно подтвердить бронирование. Подтвердить может только владелец");
    }

    @Override
    public GetBookingDto getBookingById(Integer bookingId, Integer userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Невозможно получить информацию о бронировании. " +
                        "Бронирование с id " + bookingId + " не найдено"));
        Car car = carRepository.findById(booking.getCarId())
                .orElseThrow(() -> new NotFoundException("Невозможно получить информацию о бронировании. " +
                        "Автомобиль с id " + booking.getCarId() + " не найден"));
        if (userId.equals(booking.getUserId()) || userId.equals(car.getOwnerId())) {
            return mapper.map(booking, GetBookingDto.class);
        }
        throw new NotOwnerException("Невозможно получить информацию о бронировании. " +
                "Получить информацию может создатель бронирования или владелец автомобиля");
    }

    @Override
    public List<GetBookingDto> getAllUserBookings(Integer userId, String status) {
        List<Booking> bookings;
        if (userRepository.existsById(userId)) {
            if (status.equals("ALL")) {
                bookings = bookingRepository.findByUserIdOrderByStartRentAsc(userId);
            } else if (status.equals(BookingStatus.WAITING.name())
                    || status.equals(BookingStatus.APPROVED.name())
                    || status.equals(BookingStatus.REJECTED.name())
                    || status.equals(BookingStatus.FINISHED.name())) {
                bookings = bookingRepository
                        .findAllByUserIdAndBookingStatusOrderByStartRentAsc(userId, BookingStatus.valueOf(status));
            } else {
                throw new NotFoundException("Неизвестный статус " + status +
                        ". Можно получить информацию о бронированиях по статусам: " +
                        "all, waiting, approved, rejected, finished");
            }
        } else {
            throw new NotFoundException("Невозможно получить список бронирований. " +
                    "Пользователь с id " + userId + " не найден");
        }
        List<GetBookingDto> bookingDto = new ArrayList<>();
        for (Booking booking : bookings) {
            GetBookingDto getBookingDto = mapper.map(booking, GetBookingDto.class);
            bookingDto.add(getBookingDto);
        }
        return bookingDto;
    }

    @Override
    public List<GetBookingDto> getAllOwnerCarBookings(Integer userId, String status) {
        List<Booking> bookings;
        if (bookingRepository.existsById(userId)) {
            if (!carRepository.findByOwnerId(userId).isEmpty()) {
                if (status.equals("ALL")) {
                    bookings = bookingRepository.findByOwnerId(userId);
                } else if (status.equals(BookingStatus.WAITING.name())
                        || status.equals(BookingStatus.APPROVED.name())
                        || status.equals(BookingStatus.REJECTED.name())
                        || status.equals(BookingStatus.FINISHED.name())) {
                    bookings = bookingRepository.findByOwnerIdAndStatus(userId, BookingStatus.valueOf(status));
                } else {
                    throw new NotFoundException("Неизвестный статус " + status +
                            ". Можно получить информацию о бронированиях по статусам: " +
                            "all, waiting, approved, rejected, finished");
                }
            } else {
                throw new NotOwnerException("Невозможно получить список бронирований. " +
                        "У пользователя с id " + userId + " отсутствуют автомобили для аренды");
            }
        } else {
            throw new NotFoundException("Невозможно получить список бронирований. " +
                    "Пользователь с id " + userId + " не найден");
        }
        List<GetBookingDto> bookingDto = new ArrayList<>();
        for (Booking booking : bookings) {
            GetBookingDto getBookingDto = mapper.map(booking, GetBookingDto.class);
            bookingDto.add(getBookingDto);
        }
        return bookingDto;
    }

}

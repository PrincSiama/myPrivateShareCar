package myPrivateShareCar.service;

import lombok.AllArgsConstructor;
import myPrivateShareCar.dto.BookingDto;
import myPrivateShareCar.dto.CreateBookingDto;
import myPrivateShareCar.exception.NotCreatedException;
import myPrivateShareCar.exception.NotFoundException;
import myPrivateShareCar.exception.PermissionDeniedException;
import myPrivateShareCar.model.Booking;
import myPrivateShareCar.model.BookingStatus;
import myPrivateShareCar.model.Car;
import myPrivateShareCar.model.User;
import myPrivateShareCar.repository.BookingRepository;
import myPrivateShareCar.repository.CarRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final CarRepository carRepository;
    private final BookingRepository bookingRepository;
    private final UserPrincipalService userPrincipalService;
    private final ModelMapper mapper;

    @Override
    public BookingDto create(CreateBookingDto createBookingDto, Principal principal) {
        Car car = carRepository.findById(createBookingDto.getCarId())
                .orElseThrow(() -> new NotFoundException("Невозможно создать бронирование. " +
                        "Автомобиль с id " + createBookingDto.getCarId() + " не найден"));

        User user = userPrincipalService.getUserFromPrincipal(principal);
        if (user.getDriverLicense() == null || user.getDriverLicense().getSeries() == null) {
            throw new NotCreatedException("Невозможно создать бронирование автомобиля." +
                    " У пользователь с id " + user.getId() + " отсутствует информация о водительском удостоверении");
        }

        LocalDate endRent = createBookingDto.getStartRent().plusDays(createBookingDto.getDurationRentInDays());
        if (bookingRepository.bookingByRentDate(car.getId(), createBookingDto.getStartRent(), endRent).isEmpty()) {
            return mapper.map(bookingRepository.save(new Booking(user, car, createBookingDto.getStartRent(),
                    createBookingDto.getDurationRentInDays(), endRent)), BookingDto.class);
        }
        throw new NotCreatedException("Невозможно создать бронирование автомобиля с id " + car.getId() + "." +
                " Автомобиль уже забронирован на указанные даты");
    }

    @Override
    public BookingDto changeStatus(int bookingId, BookingStatus status, Principal principal) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Невозможно подтвердить бронирование. " +
                        "Бронирование с id " + bookingId + " не найдено"));
        Car car = carRepository.findById(booking.getCar().getId())
                .orElseThrow(() -> new NotFoundException("Невозможно подтвердить бронирование. " +
                        "Автомобиль с id " + booking.getCar().getId() + " не найден"));
        User user = userPrincipalService.getUserFromPrincipal(principal);
        if (user.getId() == car.getOwnerId()) {
            booking.setBookingStatus(status);
            return mapper.map(bookingRepository.save(booking), BookingDto.class);
        }
        throw new PermissionDeniedException("Невозможно подтвердить бронирование с id " + bookingId + ". " +
                "Подтвердить может только владелец. Пользователь с id " + user.getId() + " не является владельцем"
                + " автомобиля с id " + car.getId());
    }

    @Override
    public BookingDto getBookingById(int bookingId, Principal principal) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Невозможно получить информацию о бронировании. " +
                        "Бронирование с id " + bookingId + " не найдено"));
        User user = userPrincipalService.getUserFromPrincipal(principal);
        if (user.getId() == booking.getUser().getId() || user.getId() == booking.getCar().getOwnerId()) {
            return mapper.map(booking, BookingDto.class);
        }
        throw new PermissionDeniedException("Невозможно получить информацию о бронировании с id " + bookingId +
                ". Получить информацию может создатель бронирования или владелец автомобиля");
    }

    @Override
    public List<BookingDto> getUserBookings(BookingStatus status, Principal principal) {
        List<Booking> bookings;
        User user = userPrincipalService.getUserFromPrincipal(principal);
        if (status == null) {
            bookings = bookingRepository.findByUser_IdOrderByStartRentAsc(user.getId());
        } else {
            bookings = bookingRepository
                    .findAllByUser_IdAndBookingStatusOrderByStartRentAsc(user.getId(), status);
        }
        return bookings.stream()
                .map(booking -> mapper.map(booking, BookingDto.class)).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getOwnerBookings(BookingStatus status, Principal principal) {
        List<Booking> bookings;
        User user = userPrincipalService.getUserFromPrincipal(principal);
        if (!carRepository.findByOwnerId(user.getId(), Pageable.unpaged()).isEmpty()) {
            if (status == null) {
                bookings = bookingRepository.findAllByCar_OwnerIdOrderByStartRentAsc(user.getId());
            } else {
                bookings = bookingRepository.findAllByCar_OwnerIdAndBookingStatusOrderByStartRentAsc(user.getId(),
                        status);
            }
        } else {
            throw new PermissionDeniedException("Невозможно получить список бронирований. " +
                    "У пользователя с id " + user.getId() + " отсутствуют автомобили для аренды");
        }
        return bookings.stream()
                .map(booking -> mapper.map(booking, BookingDto.class)).collect(Collectors.toList());
    }
}

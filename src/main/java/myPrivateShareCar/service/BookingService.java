package myPrivateShareCar.service;

import myPrivateShareCar.dto.BookingDto;
import myPrivateShareCar.model.BookingStatus;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {
    BookingDto create(int userId, int carId, LocalDate startRent, int durationRent);
    BookingDto updateStatus(int ownerId, int bookingId, BookingStatus status);
    BookingDto getBookingById(int bookingId, int userId);
    List<BookingDto> getUserBookings(int userId, BookingStatus status);
    List<BookingDto> getOwnerBookings(int ownerId, BookingStatus status);

}

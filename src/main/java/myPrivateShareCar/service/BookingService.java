package myPrivateShareCar.service;

import myPrivateShareCar.dto.BookingDto;
import myPrivateShareCar.dto.CreateBookingDto;
import myPrivateShareCar.model.BookingStatus;

import java.util.List;

public interface BookingService {
    BookingDto create(CreateBookingDto createBookingDto);
    BookingDto changeStatus(int bookingId, BookingStatus status);
    BookingDto getBookingById(int bookingId);
    List<BookingDto> getUserBookings(BookingStatus status);
    List<BookingDto> getOwnerBookings(BookingStatus status);

}

package myPrivateShareCar.service;

import myPrivateShareCar.dto.BookingDto;
import myPrivateShareCar.dto.CreateBookingDto;
import myPrivateShareCar.model.BookingStatus;

import java.security.Principal;
import java.util.List;

public interface BookingService {
    BookingDto create(CreateBookingDto createBookingDto, Principal principal);
    BookingDto changeStatus(int bookingId, BookingStatus status, Principal principal);
    BookingDto getBookingById(int bookingId, Principal principal);
    List<BookingDto> getUserBookings(BookingStatus status, Principal principal);
    List<BookingDto> getOwnerBookings(BookingStatus status, Principal principal);

}

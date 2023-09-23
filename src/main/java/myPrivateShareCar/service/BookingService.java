package myPrivateShareCar.service;

import myPrivateShareCar.dto.GetBookingDto;
import myPrivateShareCar.model.Booking;

import java.time.LocalDate;
import java.util.List;

public interface BookingService {
    Booking create(Integer userId, Integer carId, LocalDate startRent, Integer durationRent);
    Booking approved(Integer ownerId, Integer bookingId, boolean isApprove);
    GetBookingDto getBookingById(Integer bookingId, Integer userId);
    List<GetBookingDto> getAllUserBookings(Integer userId, String status);
    List<GetBookingDto> getAllOwnerCarBookings(Integer userId, String status);

}

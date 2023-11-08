package myPrivateShareCar.dto;

import lombok.Data;
import myPrivateShareCar.model.BookingStatus;

import java.time.LocalDate;

@Data
public class BookingDto {
    private UserDto user;
    private CarDto car;
    private LocalDate startRent;
    private LocalDate endRent;
    private BookingStatus bookingStatus;
}

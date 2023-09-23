package myPrivateShareCar.dto;

import lombok.Data;
import myPrivateShareCar.model.BookingStatus;

import java.time.LocalDate;

@Data
public class GetBookingDto {
    private Integer userId;
    private Integer carId;
    private LocalDate startRent;
    private LocalDate endRent;
    private BookingStatus bookingStatus;
}

package myPrivateShareCar.dto;

import lombok.Data;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Positive;
import java.time.LocalDate;

@Data
public class CreateBookingDto {
    @Positive
    private int carId;
    @FutureOrPresent
    private LocalDate startRent;
    @Positive
    private int durationRentInDays;

}

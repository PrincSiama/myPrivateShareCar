package myPrivateShareCar.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Data;

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

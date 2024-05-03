package myPrivateShareCar.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingDto {
    @Positive
    private int carId;
    @FutureOrPresent
    private LocalDate startRent;
    @Positive
    private int durationRentInDays;

}

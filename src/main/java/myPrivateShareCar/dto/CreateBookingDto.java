package myPrivateShareCar.dto;

import lombok.Data;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class CreateBookingDto {
    @NotNull
    private Integer carId;
    @FutureOrPresent
    private LocalDate startRent;
    @NotNull
    private Integer durationRent;

}

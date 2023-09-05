package myPrivateShareCar.book;

import lombok.Data;
import myPrivateShareCar.model.Car;
import myPrivateShareCar.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDate;

@Data
public class Booking {
    @NotNull
    public User user;
    @NotNull
    public Car car;
    @NotBlank
    public LocalDate startRent;
    @NotBlank
    public Duration durationRent;
    public boolean ownerApprove;
    public LocalDate endRent = startRent.plus(durationRent);
}

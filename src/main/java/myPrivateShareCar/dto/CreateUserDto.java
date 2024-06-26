package myPrivateShareCar.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import myPrivateShareCar.model.DriverLicense;
import myPrivateShareCar.model.Passport;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class CreateUserDto {
    @NotBlank
    private String firstname;
    @NotBlank
    private String lastname;
    @NotBlank
    @Email
    private String email;
    @Past
    private LocalDate birthday;
    @NotNull
    @Valid
    private Passport passport;
    @Valid
    private DriverLicense driverLicense;
    @NotBlank
    @Size(min = 8)
    private String password;

}

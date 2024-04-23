package myPrivateShareCar.dto;

import lombok.Data;
import myPrivateShareCar.model.DriverLicense;
import myPrivateShareCar.model.Passport;
import myPrivateShareCar.model.Role;

import java.time.LocalDate;

@Data
public class FullUserDto {
    private int id;
    private String firstname;
    private String lastname;
    private String email;
    private LocalDate birthday;
    private Role role;
    private Passport passport;
    private DriverLicense driverLicense;
    private LocalDate registrationDate;
}

package myPrivateShareCar.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
// REVIEW: не понимаю, зачем этот класс нужен. В сервисе напишу подробнее
public class UpdateUserDto {
    private int id;
    private String firstname;
    private String lastname;
    private String email;
    private LocalDate birthday;
    private PassportDto passport;
    private DriverLicenseDto driverLicense;
    private LocalDate registrationDate;
}

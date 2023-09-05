package myPrivateShareCar.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class GetUserDto {
    private String id;
    private final String name;
    private final String surname;
    private final String email;
    private LocalDate registrationDate;
}

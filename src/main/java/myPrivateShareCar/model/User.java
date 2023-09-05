package myPrivateShareCar.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
public class User {
    private String id;
    @NotBlank
    private String name;
    @NotBlank
    private String surname;
    @NotBlank
    @Email
    private String email;
    @Past
    private LocalDate birthday;
    private String numberDriverLicense; // отдельный объект со всей информацией
    @NotBlank
    private String numberPassport; // отдельный объект с всей информацией из паспорта // добавить проверку уникальности
    private LocalDate registrationDate;

}

package myPrivateShareCar.dto;

import lombok.Data;
import myPrivateShareCar.model.Passport;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
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

}

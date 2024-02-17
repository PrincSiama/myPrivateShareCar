package myPrivateShareCar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import myPrivateShareCar.model.Passport;

import javax.validation.Valid;
import javax.validation.constraints.*;
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
    @NotBlank
    @Size(min = 8)
    private String password;

}

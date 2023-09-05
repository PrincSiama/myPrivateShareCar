package myPrivateShareCar.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UpdateUserDto {
    @NotBlank
    private String name;
    @NotBlank
    private String surname;
    private String numberDriverLicense;
    private String numberPassport;
}

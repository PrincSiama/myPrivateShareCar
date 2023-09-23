package myPrivateShareCar.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class CreateCarDto {
    @NotBlank
    private String brand;
    @NotBlank
    private String model;
    @NotNull
    private Integer yearOfManufacture;
    private String color;
    @NotBlank
    private String documentNumber;
    @Pattern(regexp = "^(?ui:[АВЕКМНОРСТУХ])\\d{3}(?<!000)(?ui:[АВЕКМНОРСТУХ]){2}\\d{2,3}$",
            message = "Некорректный формат номера")
    private String registrationNumber;

}

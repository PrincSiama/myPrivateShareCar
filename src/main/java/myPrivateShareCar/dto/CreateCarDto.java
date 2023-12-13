package myPrivateShareCar.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCarDto {
    @NotBlank
    private String brand;
    @NotBlank
    private String model;
    @Min(1900)
    private int yearOfManufacture;
    private String color;
    @NotBlank
    private String documentNumber;
    @Pattern(regexp = "^(?ui:[АВЕКМНОРСТУХ])\\d{3}(?<!000)(?ui:[АВЕКМНОРСТУХ]){2}\\d{2,3}$",
            message = "Некорректный формат номера")
    private String registrationNumber;

}

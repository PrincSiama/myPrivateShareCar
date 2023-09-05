package myPrivateShareCar.dto;

import lombok.Data;

@Data
public class FindCarDto {
    private final String brand;
    private final String model;
    private final Integer yearOfManufacture;
    private final String color;
}

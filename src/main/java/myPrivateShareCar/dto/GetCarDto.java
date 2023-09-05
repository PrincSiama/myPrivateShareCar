package myPrivateShareCar.dto;

import lombok.Data;
@Data
public class GetCarDto {
    private String brand;
    private String model;
    private Integer yearOfManufacture;
    private String color;
    private double pricePerDay;
}

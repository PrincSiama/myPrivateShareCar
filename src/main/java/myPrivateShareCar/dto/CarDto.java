package myPrivateShareCar.dto;

import lombok.Data;
@Data
public class CarDto {
    private int id;
    private String brand;
    private String model;
    private int yearOfManufacture;
    private String color;
    private int pricePerDay;
}

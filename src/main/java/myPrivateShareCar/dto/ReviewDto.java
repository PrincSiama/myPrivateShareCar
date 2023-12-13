package myPrivateShareCar.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ReviewDto {
    private CarDto car;
    private UserDto user;
    private String text;
    private LocalDate writeDate;
}

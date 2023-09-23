package myPrivateShareCar.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class GetReviewDto {
    private String text;
    private LocalDate writeDate;
}

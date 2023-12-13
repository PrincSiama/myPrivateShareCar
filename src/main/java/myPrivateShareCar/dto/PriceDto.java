package myPrivateShareCar.dto;

import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class PriceDto {
    @Min(0)
    private int pricePerDay;
}

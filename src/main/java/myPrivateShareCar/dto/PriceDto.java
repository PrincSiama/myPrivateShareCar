package myPrivateShareCar.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;


@Data
public class PriceDto {
    @Min(0)
    private int pricePerDay;
}

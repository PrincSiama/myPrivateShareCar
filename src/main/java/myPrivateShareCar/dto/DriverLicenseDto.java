package myPrivateShareCar.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class DriverLicenseDto {
    private int id;
    private String series;
    private String number;
    private LocalDate dateOfIssue;
    private String issuedBy;
}

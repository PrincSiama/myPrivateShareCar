package myPrivateShareCar.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "driver_licenses")
public class DriverLicense {
    @Id
    @Column(unique = true)
    private int id;

    @JoinColumn(name = "id")
    @MapsId
    @OneToOne
    @JsonIgnore
    private User user;

    @Pattern(regexp = "^[0-9]{4}$",
            message = "Некорректный формат серии водительского удостоверения. Серия должна содержать 4 цифры")
    @Column(name = "driver_license_series")
    private String series;

    @Pattern(regexp = "^[0-9]{6}$",
            message = "Некорректный формат номера водительского удостоверения. Номер должен содержать 6 цифр")
    @Column(name = "driver_license_number")
    private String number;

    @PastOrPresent
    @Column(name = "date_of_issue")
    private LocalDate dateOfIssue;

    @NotBlank
    @Column(name = "issued_by")
    private String issuedBy;
}

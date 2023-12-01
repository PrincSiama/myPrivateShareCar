package myPrivateShareCar.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "driver_license")
public class DriverLicense {
    @Id
    @Column(unique = true)
    private int id;

    @JoinColumn(name = "id")
    @MapsId
    @OneToOne
    @JsonIgnore
    private User user;

    // REVIEW: а эти паттерны в какой момент проверяются? При сохранении в базу данных?
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

    /*public DriverLicense(String series, String number, LocalDate dateOfIssue, String issuedBy) {
        this.series = series;
        this.number = number;
        this.dateOfIssue = dateOfIssue;
        this.issuedBy = issuedBy;
    }*/
}

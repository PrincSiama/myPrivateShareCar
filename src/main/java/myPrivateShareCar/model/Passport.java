package myPrivateShareCar.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "passport")
public class Passport {
    @Id
    @Column(unique = true, nullable = false)
    private int id;

    @JoinColumn(name = "id")
    @MapsId
    @OneToOne
    @JsonIgnore
    private User user;

    @Pattern(regexp = "^[0-9]{4}$",
            message = "Некорректный формат серии паспорта. Серия должна содержать 4 цифры")
    @Column(name = "passport_series")
    private String series;

    @Pattern(regexp = "^[0-9]{6}$",
            message = "Некорректный формат номера паспорта. Номер должен содержать 6 цифр")
    @Column(name = "passport_number")
    private String number;

    @PastOrPresent
    @Column(name = "date_of_issue")
    private LocalDate dateOfIssue;

    @NotBlank
    @Column(name = "issued_by")
    private String issuedBy;


}

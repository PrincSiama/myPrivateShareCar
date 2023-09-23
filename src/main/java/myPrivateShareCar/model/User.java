package myPrivateShareCar.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@RequiredArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String surname;
    private String email;
    private LocalDate birthday;
    @Column(name = "number_passport")
    private String numberPassport; // отдельный объект с всей информацией из паспорта // добавить проверку уникальности
    @Column(name = "number_driver_license")
    private String numberDriverLicense; // отдельный объект со всей информацией
    @Column(name = "registration_date")
    private LocalDate registrationDate;

}

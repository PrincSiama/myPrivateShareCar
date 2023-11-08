package myPrivateShareCar.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private int id;

    private String firstname;
    private String lastname;
    private String email;
    private LocalDate birthday;

    @PrimaryKeyJoinColumn
    @OneToOne(/*optional = false,*/ mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Passport passport;

    @PrimaryKeyJoinColumn
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DriverLicense driverLicense;

    @Column(name = "registration_date")
    private LocalDate registrationDate;

    public void setPassport(Passport passport) {
        this.passport = passport;
        this.passport.setUser(this);
    }

    public void setDriverLicense(DriverLicense driverLicense) {
        if (driverLicense != null) {
            this.driverLicense = driverLicense;
            this.driverLicense.setUser(this);
        }
    }
}

package myPrivateShareCar.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private int id;

    private String firstname;

    private String lastname;

    private String email;

    private LocalDate birthday;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @PrimaryKeyJoinColumn
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Passport passport;

    @PrimaryKeyJoinColumn
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DriverLicense driverLicense;

    @Column(name = "registration_date")
    private LocalDate registrationDate;

    public void setPassport(Passport passport) {
        if (passport != null) {
            this.passport = passport;
            this.passport.setUser(this);
        }
    }

    public void setDriverLicense(DriverLicense driverLicense) {
        if (driverLicense != null) {
            this.driverLicense = driverLicense;
            this.driverLicense.setUser(this);
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", birthday=" + birthday +
                ", password='" + password + '\'' +
                ", role=" + role + '\'' +
                ", passport=" + passport.getSeries() + " " + passport.getNumber() + " " + passport.getDateOfIssue() +
                " " + passport.getIssuedBy() + '\'' +
                ", driverLicense=" + driverLicense.getSeries() + " " + driverLicense.getNumber() +
                " " + driverLicense.getDateOfIssue() + " " + driverLicense.getIssuedBy() + '\'' +
                ", registrationDate=" + registrationDate +
                '}';
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Stream.of(getRole().toString())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return email;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return password;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }
}

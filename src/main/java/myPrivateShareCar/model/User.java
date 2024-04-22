package myPrivateShareCar.model;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
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
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Stream.of(getRole().toString())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

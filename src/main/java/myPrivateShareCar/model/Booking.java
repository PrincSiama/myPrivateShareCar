package myPrivateShareCar.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "car_id")
    private Car car;

    @Column(name = "start_rent")
    private LocalDate startRent;

    @Column(name = "duration")
    private int durationRentInDays;

    @Column(name = "booking_status")
    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;

    @Column(name = "end_rent")
    private LocalDate endRent;

    public Booking(User user, Car car, LocalDate startRent, int durationRentInDays, LocalDate endRent) {
        this.user = user;
        this.car = car;
        this.startRent = startRent;
        this.durationRentInDays = durationRentInDays;
        this.bookingStatus = BookingStatus.WAITING;
        this.endRent = endRent;
    }
}

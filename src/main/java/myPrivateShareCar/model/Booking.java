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
@Table(name = "booking")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "user_id")
    private Integer userId;
    @Column(name = "car_id")
    private Integer carId;
    @Column(name = "start_rent")
    private LocalDate startRent;
    @Column(name = "duration")
    private Integer durationRent;
    @Column(name = "booking_status")
    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;
    @Column(name = "end_rent")
    private LocalDate endRent; //startRent.plus(durationRent);

    public Booking(Integer userId, Integer carId, LocalDate startRent, Integer durationRent) {
        this.userId = userId;
        this.carId = carId;
        this.startRent = startRent;
        this.durationRent = durationRent;
        this.bookingStatus = BookingStatus.WAITING;
        this.endRent = startRent.plusDays(durationRent);
    }
}

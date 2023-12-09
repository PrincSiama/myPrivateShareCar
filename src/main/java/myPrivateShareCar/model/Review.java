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
@Table(name = "review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int reviewId;

    @ManyToOne(optional = false)
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name="car_id")
    private Car car;

    @Column(name = "text")
    private String text;

    @Column(name = "write_date")
    private LocalDate writeDate;

    public Review(Car car, User user, String text, LocalDate writeDate) {
        this.car = car;
        this.user = user;
        this.text = text;
        this.writeDate = writeDate;
    }

}

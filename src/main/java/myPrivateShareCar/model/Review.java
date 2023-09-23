package myPrivateShareCar.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "review")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer idReview;
    @Column(name = "car_id")
    private Integer idCar;
    @Column(name = "user_id")
    private Integer idUser;
    @Column(name = "text")
    private String text;
    @Column(name = "write_date")
    private LocalDate writeDate;
    /*@ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;*/

    public Review(Integer idCar, Integer idUser, String text, LocalDate writeDate) {
        this.idCar = idCar;
        this.idUser = idUser;
        this.text = text;
        this.writeDate = writeDate;
    }

}

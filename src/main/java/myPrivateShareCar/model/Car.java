package myPrivateShareCar.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
@Entity
@Table(name = "cars")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String brand;
    private String model;
    @Column(name = "year_of_manufacture")
    private Integer yearOfManufacture;
    private String color;
    @Column(name = "document_number")
    private String documentNumber;
    @Column(name = "registration_number")
    //@Pattern(regexp = "^(?ui:[АВЕКМНОРСТУХ])\\d{3}(?<!000)(?ui:[АВЕКМНОРСТУХ]){2}\\d{2,3}$")
    /*@Pattern(regexp = "^(?ui:[АВЕКМНОРСТУХ])\\d{3}(?<!000)(?ui:[АВЕКМНОРСТУХ]){2}\\d{2,3}$",
            message = "Некорректный формат номера")*/
    private String registrationNumber;
    @Column(name = "owner_id")
    private Integer ownerId;
    @Column(name = "price_per_day")
    private int pricePerDay; // добавить в будущем динамическое ценообразование
    @OneToMany
    @JoinColumn(name = "car_id")
    private final List<Review> reviews = new ArrayList<>();
    //private int rentCounter = 0; // счетчик количества аренд

}

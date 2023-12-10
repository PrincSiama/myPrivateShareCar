package myPrivateShareCar.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "cars")
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String brand;

    private String model;

    @Column(name = "year_of_manufacture")
    private int yearOfManufacture;

    private String color;

    @Column(name = "document_number")
    private String documentNumber;

    @Column(name = "registration_number")
    private String registrationNumber;

    @Column(name = "owner_id")
    private int ownerId;

    // добавить в будущем динамическое ценообразование
    @Column(name = "price_per_day")
    private int pricePerDay;

    @OneToMany
    @JoinColumn(name = "car_id")
    private List<Review> reviews = new ArrayList<>();
}

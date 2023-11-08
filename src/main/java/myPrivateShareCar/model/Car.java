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
    @Column(name = "price_per_day")
    private int pricePerDay; // добавить в будущем динамическое ценообразование
    @OneToMany
    @JoinColumn(name = "car_id")
    private List<Review> reviews = new ArrayList<>();
    //private int rentCounter = 0; // счетчик количества аренд

}

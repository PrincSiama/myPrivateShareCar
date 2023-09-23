package myPrivateShareCar.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "request_car")
public class RequestCar {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String brand;
    private String model;
    @Column(name = "year_of_manufacture")
    private String yearOfManufacture; // todo сделать дто из кар но поменять тип поля?
    private String color;
}

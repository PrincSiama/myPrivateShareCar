package myPrivateShareCar.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class Car {
    private String id;
    @NotBlank
    private String brand;
    @NotBlank
    private String model;
    @NotBlank
    private Integer yearOfManufacture;
    private String color;
    @NotBlank
    private String documentNumber;
    @NotBlank
    @Pattern(regexp = "^[А]$",
            message = "Некорректный формат номера")
    private String registrationNumber;
    private double pricePerDay; // добавить в будущем динамическое ценообразование
    private String ownerId;
    private final Map<String, List<String>> reviews = new HashMap<>(); // <id пользователя, отзыв> // создать объект review и добавить список объектов
    //private int rentCounter = 0; // счетчик количества аренд

    public void addReview(String userId, String review) {
        if (reviews.containsKey(userId)) {
            reviews.get(userId).add(review);
        } else {
            reviews.put(userId, List.of(review));
        }
    }

    public Collection<List<String>> getReviewByCar() {
        return reviews.values();
    }


}

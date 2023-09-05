package myPrivateShareCar.repository;

import myPrivateShareCar.model.Car;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class CarRepositoryImpl implements CarRepository {

    private final Map<String, Car> cars = new HashMap<>(); // <id, car>

    @Override
    public Car create(Car car) {
        car.setId(UUID.randomUUID().toString());
        cars.put(car.getId(), car);
        return car;
    }

    @Override
    public Car update(Car car) {
        // todo придумать нормальную логику обновления. Чтобы обновлялись только поля, которые отличаются
        cars.put(car.getId(), car);
        return car;
    }

    @Override
    public void delete(String id) {
        cars.remove(id);
    }

    @Override
    public Car getById(String id) {
        return cars.get(id);
    }

    @Override
    public Collection<Car> getOwnerCars(String id) {
        return cars.values().stream().filter(car -> car.getOwnerId().equals(id)).collect(Collectors.toList());
    }

    @Override
    public Collection<Car> getAll() {
        return cars.values();
    }

    @Override
    public Collection<Car> find(String text) {
        String textLowerCase = text.toLowerCase();
        return cars.values().stream()
                .filter(car -> car.getModel().toLowerCase().contains(textLowerCase)
                        || car.getBrand().toLowerCase().contains(textLowerCase)
                        || car.getColor().toLowerCase().contains(textLowerCase))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isCorrect(String id) {
        return cars.containsKey(id);
    }
}

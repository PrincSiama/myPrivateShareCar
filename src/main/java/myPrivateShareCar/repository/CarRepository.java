package myPrivateShareCar.repository;

import myPrivateShareCar.model.Car;

import java.util.Collection;

public interface CarRepository {
    Car create(Car car);
    Car update(Car car);
    void delete(String id);
    Car getById(String id);
    Collection<Car> getOwnerCars(String id);
    Collection<Car> getAll();
    Collection<Car> find(String text);
    boolean isCorrect(String id);
}

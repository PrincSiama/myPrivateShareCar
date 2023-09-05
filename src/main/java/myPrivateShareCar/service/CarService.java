package myPrivateShareCar.service;

import myPrivateShareCar.dto.CreateCarDto;
import myPrivateShareCar.dto.GetCarDto;
import myPrivateShareCar.model.Car;

import java.util.Collection;

public interface CarService {
    Car create(String ownerId, CreateCarDto createCarDto);
    void delete(String ownerId, String id);
    GetCarDto getById(String id);
    Collection<Car> getOwnerCars(String id);
    Collection<Car> getAll();
    Collection<Car> find(String text);
}

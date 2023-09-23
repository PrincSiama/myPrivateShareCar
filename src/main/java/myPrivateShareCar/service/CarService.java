package myPrivateShareCar.service;

import myPrivateShareCar.dto.CreateCarDto;
import myPrivateShareCar.dto.GetCarDto;
import myPrivateShareCar.dto.GetReviewDto;
import myPrivateShareCar.model.Car;

import java.util.List;

public interface CarService {
    Car create(Integer ownerId, CreateCarDto createCarDto);
    void delete(Integer ownerId, Integer id);
    GetCarDto getById(Integer id);
    List<Car> getOwnerCars(Integer id);
    List<Car> getAll();
    List<Car> find(String text);
    void addReview(Integer carId, Integer userId, String text);
    List<GetReviewDto> getReviewByCar(Integer carId);
}

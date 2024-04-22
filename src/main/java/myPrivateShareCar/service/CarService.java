package myPrivateShareCar.service;

import myPrivateShareCar.dto.CarDto;
import myPrivateShareCar.dto.CreateCarDto;
import myPrivateShareCar.model.Car;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface CarService {
    Car create(CreateCarDto createCarDto);
    void delete(int id);
    CarDto getById(int id);
    List<CarDto> getOwnerCars(Pageable pageable);
    List<CarDto> search(String text, LocalDate startRent, LocalDate endRent, Pageable pageable);
    void updatePrice(int carId, int pricePerDay);
}

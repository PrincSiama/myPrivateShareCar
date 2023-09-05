package myPrivateShareCar.service;

import lombok.RequiredArgsConstructor;
import myPrivateShareCar.dto.CreateCarDto;
import myPrivateShareCar.dto.GetCarDto;
import myPrivateShareCar.exception.NotFoundException;
import myPrivateShareCar.exception.NotOwnerException;
import myPrivateShareCar.model.Car;
import myPrivateShareCar.repository.CarRepository;
import myPrivateShareCar.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class CarServiceImpl implements CarService {
    private final CarRepository carRepository;
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    @Override
    public Car create(String ownerId, CreateCarDto createCarDto) {
        if (userRepository.isCorrect(ownerId)) {
            Car car = mapper.map(createCarDto, Car.class);
            car.setOwnerId(ownerId);
            return carRepository.create(car);
        }
        throw new NotFoundException("Невозможно создать автомобиль. Владелец с id " + ownerId + " не найден");
    }

    /*@Override
    public Car update(String ownerId, Car car) { // проверка владельца по id из существующей сar
        if (ownerId.equals(car.getOwnerId())) {
            if (carRepository.isCorrect(car.getId())) {
                return carRepository.update(car);
            }
            throw new NotFoundException("Невозможно обновить. Автомобиль с id " + car.getId() + " не найден");
        } else {
            throw new NotOwnerException("Невозможно отредактировать автомобиль. Редактировать может только владелец");
        }
    }*/

    @Override
    public void delete(String ownerId, String carId) {
        if (carRepository.isCorrect(carId)) {
            if (ownerId.equals(carRepository.getById(carId).getOwnerId())) {
                carRepository.delete(carId);
            } else {
                throw new NotOwnerException("Невозможно удалить автомобиль. Удалить может только владелец");
            }
        } else {
            throw new NotFoundException("Невозможно удалить автомобиль. Автомобиль с id " + carId + " не найден");
        }
    }

    @Override
    public GetCarDto getById(String id) {
        if (carRepository.isCorrect(id)) {
            return mapper.map(carRepository.getById(id), GetCarDto.class);
        }
        throw new NotFoundException("Невозможно получить автомобиль. Автомобиль с id " + id + " не найден");
    }

    @Override
    public Collection<Car> getOwnerCars(String ownerId) {
        if (userRepository.isCorrect(ownerId)) {
            return carRepository.getOwnerCars(ownerId);
        }
        throw new NotFoundException("Невозможно получить автомобили пользователя. Пользователь с id "
                + ownerId + " не найден");
    }

    @Override
    public Collection<Car> getAll() {
        return carRepository.getAll();
    }

    @Override
    public Collection<Car> find(String text) {
        return carRepository.find(text);
    }
}

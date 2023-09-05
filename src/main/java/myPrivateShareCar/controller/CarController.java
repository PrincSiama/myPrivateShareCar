package myPrivateShareCar.controller;

import lombok.RequiredArgsConstructor;
import myPrivateShareCar.dto.CreateCarDto;
import myPrivateShareCar.dto.GetCarDto;
import myPrivateShareCar.model.Car;
import myPrivateShareCar.service.CarService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cars")
public class CarController {

    private final CarService carService;

    @PostMapping
    public Car create(@Valid @RequestBody CreateCarDto createCarDto,
                      @RequestHeader(value = "X-Owner-Id") String ownerId) {
        return carService.create(ownerId, createCarDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id,
                       @RequestHeader(value = "X-Owner-Id") String ownerId) { // только владелец
        carService.delete(ownerId, id);
    }

    @GetMapping("/{id}")
    public GetCarDto getById(@PathVariable String id) {
        return carService.getById(id);
    }

    @GetMapping
    public Collection<Car> getCars(@RequestHeader(value = "X-Owner-Id", required = false) String ownerId) {
        if (ownerId != null) {
            return carService.getOwnerCars(ownerId);
        } else {
            return carService.getAll();
        }
    } // добавить постраничную выдачу авто

    @GetMapping("/search")
    public Collection<Car> find(@RequestParam("text") String findText) {
        return carService.find(findText);
    }
}

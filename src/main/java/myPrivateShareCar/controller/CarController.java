package myPrivateShareCar.controller;

import lombok.RequiredArgsConstructor;
import myPrivateShareCar.dto.CarDto;
import myPrivateShareCar.dto.CreateCarDto;
import myPrivateShareCar.dto.PriceDto;
import myPrivateShareCar.model.Car;
import myPrivateShareCar.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cars")
public class CarController {
    @Autowired
    private final CarService carService;

    @PostMapping
    public Car create(@RequestBody @Valid CreateCarDto createCarDto,
                      @RequestHeader(value = "X-Owner-Id") int ownerId) {
        return carService.create(ownerId, createCarDto);
    }

    @DeleteMapping("/{carId}")
    public void delete(@PathVariable int carId,
                       @RequestHeader(value = "X-Owner-Id") int ownerId) {
        carService.delete(ownerId, carId);
    }

    @GetMapping("/{carId}")
    public CarDto getById(@PathVariable int carId) {
        return carService.getById(carId);
    }

    @GetMapping
    public List<CarDto> getOwnerCars(@RequestHeader(value = "X-Owner-Id") int ownerId,
                                     @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                     @RequestParam(value = "size", required = false, defaultValue = "5") int size) {
        return carService.getOwnerCars(ownerId, page, size);
    }

    // todo без параметров - все машины, в параметрах поиск по тексту и/или датам
    // паттерн спецификация

    @GetMapping("/search") //?page={page} ?size={size}
    public List<CarDto> search(@RequestParam(value = "text", required = false) String findText,
                               @RequestParam(value = "startRent", required = false)
                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startRent,
                               @RequestParam(value = "endRent", required = false)
                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endRent,
                               @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                               @RequestParam(value = "size", required = false, defaultValue = "5") int size) {
        return carService.search(findText, startRent, endRent, page, size);
    }

    // todo цену передаем в теле, сделать PriceDto +
    @PutMapping("/{carId}")
    public void updatePrice(@RequestBody @Valid PriceDto priceDto,
                            @PathVariable int carId,
                            @RequestHeader(value = "X-Owner-Id") int ownerId) {
        carService.updatePrice(carId, ownerId, priceDto.getPricePerDay());
    }

}

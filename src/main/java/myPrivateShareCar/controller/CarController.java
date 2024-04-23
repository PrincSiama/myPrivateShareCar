package myPrivateShareCar.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.RequiredArgsConstructor;
import myPrivateShareCar.dto.CarDto;
import myPrivateShareCar.dto.CreateCarDto;
import myPrivateShareCar.dto.PriceDto;
import myPrivateShareCar.model.Car;
import myPrivateShareCar.service.CarService;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cars")
public class CarController {
    private final CarService carService;

    @PostMapping
    public Car create(@RequestBody @Valid CreateCarDto createCarDto) {
        return carService.create(createCarDto);
    }

    @DeleteMapping("/{carId}")
    public void delete(@PathVariable int carId) {
        carService.delete(carId);
    }

    @GetMapping("/{carId}")
    public CarDto getById(@PathVariable int carId) {
        return carService.getById(carId);
    }

    @GetMapping
    public List<CarDto> getOwnerCars(@RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                     @RequestParam(value = "size", required = false, defaultValue = "5") int size) {
        return carService.getOwnerCars(PageRequest.of(page, size));
    }

    @GetMapping("/search")
    public List<CarDto> search(@RequestParam(value = "text", required = false) String findText,
                               @RequestParam(value = "startRent", required = false)
                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @FutureOrPresent LocalDate startRent,
                               @RequestParam(value = "endRent", required = false)
                               @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Future LocalDate endRent,
                               @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                               @RequestParam(value = "size", required = false, defaultValue = "5") int size) {
        return carService.search(findText, startRent, endRent, PageRequest.of(page, size));
    }

    @PutMapping("/{carId}")
    public void updatePrice(@RequestBody @Valid PriceDto priceDto,
                            @PathVariable int carId) {
        carService.updatePrice(carId, priceDto.getPricePerDay());
    }

}

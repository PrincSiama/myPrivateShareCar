package myPrivateShareCar.controller;

import lombok.RequiredArgsConstructor;
import myPrivateShareCar.dto.AddReviewDto;
import myPrivateShareCar.dto.CreateCarDto;
import myPrivateShareCar.dto.GetCarDto;
import myPrivateShareCar.dto.GetReviewDto;
import myPrivateShareCar.model.Car;
import myPrivateShareCar.service.CarService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cars")
public class CarController {

    private final CarService carService;

    @PostMapping
    public Car create(@RequestBody @Valid CreateCarDto createCarDto,
                      @RequestHeader(value = "X-Owner-Id") Integer ownerId) {
        return carService.create(ownerId, createCarDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id,
                       @RequestHeader(value = "X-Owner-Id") Integer ownerId) {
        carService.delete(ownerId, id);
    }

    @GetMapping("/{id}")
    public GetCarDto getById(@PathVariable Integer id) {
        return carService.getById(id);
    }

    @GetMapping
    public List<Car> getCars(@RequestHeader(value = "X-Owner-Id", required = false) Integer ownerId) {
        if (ownerId != null) {
            return carService.getOwnerCars(ownerId);
        } else {
            return carService.getAll();
        }
    }

    @GetMapping("/search")
    public List<Car> find(@RequestParam("text") String findText) {
        return carService.find(findText);
    }

    @PostMapping ("/review/{id}/")
    public void addReview(@PathVariable Integer id,
                          @RequestHeader(value = "X-User-Id") Integer userId,
                          @RequestBody AddReviewDto addReviewDto) {
        carService.addReview(id, userId, addReviewDto.getText());
    }

    @GetMapping("/review/{id}")
    public List<GetReviewDto> getReviewByCar(@PathVariable Integer id) {
        return carService.getReviewByCar(id);
    }

}

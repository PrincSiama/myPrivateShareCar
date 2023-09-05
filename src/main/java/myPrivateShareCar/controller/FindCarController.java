package myPrivateShareCar.controller;

import lombok.RequiredArgsConstructor;
import myPrivateShareCar.dto.FindCarDto;
import myPrivateShareCar.service.FindCarService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/findCar")
public class FindCarController {
    private final FindCarService findCarService;

    @PostMapping
    public void findCar(@RequestBody FindCarDto findCarDto) {
        findCarService.findCar(findCarDto);
    }

    @GetMapping
    public List<FindCarDto> allRequestCar() {
        return findCarService.allRequestCar();
    }
}

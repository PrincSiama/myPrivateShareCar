package myPrivateShareCar.controller;

import lombok.RequiredArgsConstructor;
import myPrivateShareCar.model.RequestCar;
import myPrivateShareCar.service.RequestCarService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/requestCar")
public class RequestCarController {
    private final RequestCarService requestCarService;

    @PostMapping
    public void requestCar(@RequestBody RequestCar requestCar) {
        requestCarService.requestCar(requestCar);
    }

    @GetMapping
    public List<RequestCar> allRequestCar() {
        return requestCarService.allRequestCar();
    }
}

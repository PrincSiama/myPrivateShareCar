package myPrivateShareCar.controller;

import myPrivateShareCar.service.CarService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = CarController.class)
class CarControllerTest {

    @MockBean
    private CarService carService;

    @Autowired
    private MockMvc mvc;

    @Test
    void createCarTest() {

    }
}
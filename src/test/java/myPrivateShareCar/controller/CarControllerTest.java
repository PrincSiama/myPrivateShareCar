package myPrivateShareCar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import myPrivateShareCar.dto.CreateCarDto;
import myPrivateShareCar.model.Car;
import myPrivateShareCar.service.CarService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CarController.class)
class CarControllerTest {

    @MockBean
    private CarService carService;
    @Autowired
    private MockMvc mvc;
    private final ModelMapper mapper = new ModelMapper();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void createCarTest() throws Exception {
        CreateCarDto request = new CreateCarDto("Volvo", "S80", 2018, "black", "8080 123456", "В080ВВ80");
        int customId = 10;
        when(carService.create(Mockito.anyInt(), Mockito.any(CreateCarDto.class)))
                .thenAnswer(invocationOnMock -> {
                    CreateCarDto createCarDto = invocationOnMock.getArgument(1, CreateCarDto.class);
                    Car car = mapper.map(createCarDto, Car.class);
                    car.setId(customId);
                    car.setOwnerId(invocationOnMock.getArgument(0, Integer.class));
                    return car;
                });

        mvc.perform(post("/cars")
                        .content(objectMapper.writeValueAsString(request))
                        .header("X-Owner-Id", customId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customId))
                .andExpect(jsonPath("brand").value(request.getBrand()))
                .andExpect(jsonPath("model").value(request.getModel()))
                .andExpect(jsonPath("yearOfManufacture").value(request.getYearOfManufacture()))
                .andExpect(jsonPath("color").value(request.getColor()))
                .andExpect(jsonPath("documentNumber").value(request.getDocumentNumber()))
                .andExpect(jsonPath("registrationNumber").value(request.getRegistrationNumber()))
                .andExpect(jsonPath("pricePerDay").value(0))
                .andExpect(jsonPath("reviews").isEmpty());


    }
}
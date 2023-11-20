package myPrivateShareCar.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import myPrivateShareCar.dto.CreateUserDto;
import myPrivateShareCar.model.Passport;
import myPrivateShareCar.model.User;
import myPrivateShareCar.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mvc;
    private final ModelMapper mapper = new ModelMapper();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Корректное создание пользователя")
    void createUserTest() throws Exception {
        objectMapper.findAndRegisterModules();
        CreateUserDto request = new CreateUserDto("Иван", "Иванов", "ivan@ivanov.ru",
                LocalDate.of(2000, 10, 1), new Passport("1234", "123456",
                LocalDate.of(2014, 5, 15), "МВД №1"));
        int customUserId = 15;
        when(userService.create(Mockito.any(CreateUserDto.class)))
                .thenAnswer(invocationOnMock -> {
                    CreateUserDto createUserDto = invocationOnMock.getArgument(0, CreateUserDto.class);
                    User user = mapper.map(createUserDto, User.class);
                    user.setId(customUserId);
                    user.setRegistrationDate(LocalDate.now());
                    return user;
                });

        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customUserId))
                .andExpect(jsonPath("$.firstname").value(request.getFirstname()))
                .andExpect(jsonPath("$.lastname").value(request.getLastname()))
                .andExpect(jsonPath("$.email").value(request.getEmail()))
                .andExpect(jsonPath("$.birthday").value(request.getBirthday().toString()))
                .andExpect(jsonPath("$.passport").isNotEmpty())
                .andExpect(jsonPath("$.passport.series").value(request.getPassport().getSeries()))
                .andExpect(jsonPath("$.passport.number").value(request.getPassport().getNumber()))
                .andExpect(jsonPath("$.passport.dateOfIssue")
                        .value(request.getPassport().getDateOfIssue().toString()))
                .andExpect(jsonPath("$.passport.issuedBy").value(request.getPassport().getIssuedBy()));
    }

    @Test
    @DisplayName("Создание пользователя с невалидными данными")
    void createUserWithNotValidDataTest() throws Exception {
        objectMapper.findAndRegisterModules();
        CreateUserDto request = new CreateUserDto("Иван", "Иванов", "ivanov.ru",
                LocalDate.of(2000, 10, 1), new Passport("1234", "123456",
                LocalDate.of(2014, 5, 15), "МВД №1"));
        int customUserId = 15;

        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
//                todo нужно проверить, что возникает ошибка при валидации данных
//                .andExpect(result -> {
//                    assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException);
//                })
                ;
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }

    @Test
    void getById() {
    }
}
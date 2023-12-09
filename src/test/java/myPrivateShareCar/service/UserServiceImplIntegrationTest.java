package myPrivateShareCar.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jackson.jsonpointer.JsonPointerException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.ReplaceOperation;
import myPrivateShareCar.dto.CreateUserDto;
import myPrivateShareCar.dto.UserDto;
import myPrivateShareCar.exception.NotFoundException;
import myPrivateShareCar.exception.NotUpdatedException;
import myPrivateShareCar.model.Passport;
import myPrivateShareCar.model.User;
import myPrivateShareCar.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceImplIntegrationTest {
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    private final ModelMapper modelMapper = new ModelMapper();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        userService = new UserServiceImpl(userRepository, modelMapper, objectMapper);
    }

    @Test
    @DirtiesContext
    @DisplayName("Корректное создание пользователя")
    void createUserTest() {
        CreateUserDto createUserDto = new CreateUserDto("Иван", "Иванов", "ivan@ivanov.ru",
                LocalDate.of(2000, 10, 1), new Passport("1234", "123456",
                LocalDate.of(2014, 5, 15), "МВД №1"));

        User user = userService.create(createUserDto);

        assertNotNull(user);
        assertEquals(createUserDto.getEmail(), user.getEmail());
    }

    @Test
    @DisplayName("Корректное обновление пользователя")
    void update() {
        objectMapper.findAndRegisterModules();
        CreateUserDto createUserDto = new CreateUserDto("Иван", "Иванов", "ivan@ivanov.ru",
                LocalDate.of(2000, 10, 1), new Passport("1234", "123456",
                LocalDate.of(2014, 5, 15), "МВД №1"));
        User testUser = userService.create(createUserDto);
        int userId = testUser.getId();
        JsonPatch jsonPatch;
        try {
            jsonPatch = new JsonPatch(List.of(new ReplaceOperation(new JsonPointer("/firstname"),
                    new TextNode("Пётр"))));
        } catch (JsonPointerException e) {
            throw new NotUpdatedException("Невозможно обновить данные пользователя", e);
        }
        User user = userService.update(userId, jsonPatch);

        assertNotNull(user);
        assertNotNull(user.getRegistrationDate());
        assertEquals(userId, user.getId());
        assertEquals(createUserDto.getEmail(), user.getEmail());
    }

    @Test
    @DirtiesContext
    @DisplayName("Корректное удаление пользователя")
    void deleteUserTest() {
        CreateUserDto createUserDto = new CreateUserDto("Иван", "Иванов", "ivan@ivanov.ru",
                LocalDate.of(2000, 10, 1), new Passport("1234", "123456",
                LocalDate.of(2014, 5, 15), "МВД №1"));

        User user = userService.create(createUserDto);

        assertNotNull(user);
        int id = user.getId();

        userService.delete(id);

        assertThrows(NotFoundException.class, () -> userService.getById(id));
    }

    @Test
    @DisplayName("Удаление несуществующего пользователя")
    void deleteUserWithNotExistUserTest() {
        int customUserId = 55;

        assertThrows(NotFoundException.class, () -> userService.delete(customUserId));
    }

    @Test
    @DirtiesContext
    @DisplayName("Корректное получение пользователя")
    void getUserByIdTest() {
        CreateUserDto createUserDto = new CreateUserDto("Иван", "Иванов", "ivan@ivanov.ru",
                LocalDate.of(2000, 10, 1), new Passport("1234", "123456",
                LocalDate.of(2014, 5, 15), "МВД №1"));

        User user = userService.create(createUserDto);

        int userId = user.getId();

        UserDto userDto = userService.getById(userId);

        assertNotNull(userDto);
    }

    @Test
    @DisplayName("Получение несуществующего пользователя")
    void getUserByIdWithNotExistUserTest() {
        int customUserId = 77;

        assertThrows(NotFoundException.class, () -> userService.getById(customUserId));
    }
}
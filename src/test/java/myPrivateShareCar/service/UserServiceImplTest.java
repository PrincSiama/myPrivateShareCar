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
import myPrivateShareCar.exception.UpdateException;
import myPrivateShareCar.model.Passport;
import myPrivateShareCar.model.User;
import myPrivateShareCar.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    private UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userService = new UserServiceImpl(userRepository, new ModelMapper(), objectMapper);
    }

    @Test
    @DisplayName("Корректное создание пользователя")
    public void createUserTest() {
        CreateUserDto createUserDto = new CreateUserDto("Иван", "Иванов", "ivan@ivanov.ru",
                LocalDate.of(2000, 10, 1), new Passport("1234", "123456",
                LocalDate.of(2014, 5, 15), "МВД №1"));
        User testUser = new ModelMapper().map(createUserDto, User.class);
        testUser.setRegistrationDate(LocalDate.now());

        when(userRepository.save(Mockito.any(User.class))).thenReturn(testUser);

        User user = userService.create(createUserDto);

        verify(userRepository).save(Mockito.any(User.class));
        assertNotNull(user);
        assertEquals(createUserDto.getFirstname(), user.getFirstname());
        assertEquals(createUserDto.getLastname(), user.getLastname());
        assertEquals(createUserDto.getEmail(), user.getEmail());
        assertEquals(createUserDto.getBirthday(), user.getBirthday());
        assertEquals(createUserDto.getPassport(), user.getPassport());
        assertNotNull(user.getRegistrationDate());
    }

    // todo как написать jsonPatch?
    @Test
    @DisplayName("Корректное обновление пользователя")
    public void updateTest() {
        objectMapper.findAndRegisterModules();
        CreateUserDto createUserDto = new CreateUserDto("Иван", "Иванов", "ivan@ivanov.ru",
                LocalDate.of(2000, 10, 1), new Passport("1234", "123456",
                LocalDate.of(2014, 5, 15), "МВД №1"));
        User testUser = new ModelMapper().map(createUserDto, User.class);
        testUser.setRegistrationDate(LocalDate.now());
        int userId = testUser.getId();

        when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(testUser));

//        JsonPatch jsonPatch = { "op": "replace", "path": "/firstname", "value": "Пётр"};

        User updateUser;
        try {
            JsonPatch jsonPatch = new JsonPatch(List.of(new ReplaceOperation(new JsonPointer("/firstname"),
                    new TextNode("Пётр"))));
            updateUser = userService.update(userId, jsonPatch);
//            JsonNode jsonNode = objectMapper.convertValue(createUserDto, JsonNode.class);
//            JsonNode patched = jsonPatch.apply(jsonNode);
//            updateUser = objectMapper.treeToValue(patched, User.class);
        } catch (JsonPointerException e) {
            throw new UpdateException("Невозможно обновить данные пользователя", e);
        }

//        when(userRepository.save(Mockito.any(User.class))).thenReturn(updateUser);
    }

    @Test
    @DisplayName("Обновление несуществующего пользователя")
    public void updateUserWithNotExistUserTest() {
        int customUserId = 77;
        when(userRepository.findById(Mockito.anyInt())).thenThrow(
                new NotFoundException("Невозможно обновить пользователя. Пользователь с id "
                        + customUserId + " не найден"));

        assertThrows(NotFoundException.class, () -> userService.update(customUserId, null));
    }

    @Test
    @DisplayName("Корректное удаление пользователя")
    public void deleteUserTest() {
        int customUserId = 15;
        when(userRepository.existsById(Mockito.anyInt())).thenReturn(true);
        doNothing().when(userRepository).deleteById(Mockito.anyInt());

        userService.delete(customUserId);

        verify(userRepository).deleteById(Mockito.anyInt());
    }

    @Test
    @DisplayName("Удаление несуществующего пользователя")
    public void deleteUserWithNotExistUserTest() {
        int customUserId = 15;
        when(userRepository.existsById(Mockito.anyInt())).thenThrow(
                new NotFoundException("Невозможно удалить пользователя. Пользователь с id " + customUserId + " не найден"));

        assertThrows(NotFoundException.class, () -> userService.delete(customUserId));

        verify(userRepository, never()).deleteById(Mockito.anyInt());
    }

    @Test
    @DisplayName("Корректное получение пользователя по id")
    public void getUserByIdTest() {
        int customUserId = 1;
        CreateUserDto createUserDto = new CreateUserDto("Иван", "Иванов", "ivan@ivanov.ru",
                LocalDate.of(2000, 10, 1), new Passport("1234", "123456",
                LocalDate.of(2014, 5, 15), "МВД №1"));
        User testUser = new ModelMapper().map(createUserDto, User.class);
        testUser.setRegistrationDate(LocalDate.now());

        when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(testUser));

        UserDto userDto = userService.getById(customUserId);

        assertNotNull(userDto);
        assertEquals(createUserDto.getFirstname(), userDto.getFirstname());
        assertEquals(createUserDto.getLastname(), userDto.getLastname());
        assertEquals(createUserDto.getEmail(), userDto.getEmail());
    }

    @Test
    @DisplayName("Получение несуществующего пользователя")
    public void getUserByIdWithNotExistUserTest() {
        int customUserId = 1;
        when(userRepository.findById(Mockito.anyInt())).thenThrow(
                new NotFoundException("Невозможно получить пользователя. Пользователь с id "
                        + customUserId + " не найден"));

        assertThrows(NotFoundException.class, () -> {
            userService.getById(customUserId);
        });
    }
}
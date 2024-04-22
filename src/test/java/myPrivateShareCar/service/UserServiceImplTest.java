/*
package myPrivateShareCar.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jackson.jsonpointer.JsonPointerException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    private UserService userService;
    private final ModelMapper modelMapper = new ModelMapper();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userService = new UserServiceImpl(userRepository, modelMapper, objectMapper, passwordEncoder);
    }

    @Test
    @DisplayName("Корректное создание пользователя")
    public void createUserTest() {
        CreateUserDto createUserDto = new CreateUserDto("Иван", "Иванов", "ivan@ivanov.ru",
                LocalDate.of(2000, 10, 1), new Passport("1234", "123456",
                LocalDate.of(2014, 5, 15), "МВД №1"), "password1");

        User testUser = modelMapper.map(createUserDto, User.class);
        testUser.setRegistrationDate(LocalDate.now());

        when(userRepository.save(Mockito.any(User.class))).thenReturn(testUser);

        User user = userService.create(createUserDto);

        verify(userRepository).save(Mockito.any(User.class));
        assertNotNull(user);
        assertEquals(testUser.getFirstname(), user.getFirstname());
        assertEquals(testUser.getLastname(), user.getLastname());
        assertEquals(testUser.getEmail(), user.getEmail());
        assertEquals(testUser.getBirthday(), user.getBirthday());
        assertEquals(testUser.getRegistrationDate(), user.getRegistrationDate());
    }

    @Test
    @DisplayName("Корректное обновление пользователя")
    public void updateUserTest() {
        objectMapper.findAndRegisterModules();
        int customUserId = 22;

        User testUser = new User();
        testUser.setId(customUserId);
        testUser.setFirstname("Ivan");
        testUser.setLastname("Ivanov");
        testUser.setEmail("ivan@email.ru");
        testUser.setBirthday(LocalDate.of(2000, 10, 1));
        testUser.setRegistrationDate(LocalDate.of(2023, 11, 20));

        when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(testUser));

        User updateUser;
        try {
            JsonPatch jsonPatch = new JsonPatch(List.of(new ReplaceOperation(new JsonPointer("/firstname"),
                    new TextNode("Пётр"))));

            JsonNode jsonNode = objectMapper.convertValue(testUser, JsonNode.class);
            JsonNode patched = jsonPatch.apply(jsonNode);
            User returnUser = objectMapper.treeToValue(patched, User.class);

            when(userRepository.save(Mockito.any(User.class))).thenReturn(returnUser);

            updateUser = userService.update(customUserId, jsonPatch);
        } catch (JsonPointerException | JsonPatchException | JsonProcessingException e) {
            throw new NotUpdatedException("Невозможно обновить данные пользователя", e);
        }
        assertNotNull(updateUser);
        assertEquals("Пётр", updateUser.getFirstname());
        assertEquals("ivan@email.ru", updateUser.getEmail());
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
                LocalDate.of(2014, 5, 15), "МВД №1"), "password1");
        User testUser = modelMapper.map(createUserDto, User.class);
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

        assertThrows(NotFoundException.class, () -> userService.getById(customUserId));
    }
}*/

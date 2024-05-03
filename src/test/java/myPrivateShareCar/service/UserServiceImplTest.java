package myPrivateShareCar.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jackson.jsonpointer.JsonPointerException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.ReplaceOperation;
import myPrivateShareCar.dto.CreateUserDto;
import myPrivateShareCar.dto.FullUserDto;
import myPrivateShareCar.dto.UserDto;
import myPrivateShareCar.exception.NotFoundException;
import myPrivateShareCar.exception.NotUpdatedException;
import myPrivateShareCar.model.Passport;
import myPrivateShareCar.model.Role;
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

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    private UserService userService;
    @Mock
    private UserPrincipalService userPrincipalService;
    private final ModelMapper modelMapper = new ModelMapper();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        userService = new UserServiceImpl(userRepository, userPrincipalService,
                modelMapper, objectMapper, passwordEncoder);
    }

    @Test
    @DisplayName("Корректное создание пользователя")
    public void createUserTest() {
        CreateUserDto createUserDto = new CreateUserDto("Иван", "Иванов", "ivan@ivanov.ru",
                LocalDate.of(2000, 10, 1), new Passport("1234", "123456",
                LocalDate.of(2014, 5, 15), "МВД №1"),
                null, "password1");

        when(userRepository.save(Mockito.any(User.class))).then(returnsFirstArg());

        FullUserDto user = userService.create(createUserDto);

        verify(userRepository).save(Mockito.any(User.class));
        assertNotNull(user);
        assertEquals(LocalDate.now(), user.getRegistrationDate());
        assertEquals(Role.USER, user.getRole());
        assertEquals(createUserDto.getPassport().getNumber(), user.getPassport().getNumber());
        assertEquals(createUserDto.getEmail(), user.getEmail());
        assertNull(user.getDriverLicense());
    }

    @Test
    @DisplayName("Корректное обновление пользователя")
    public void updateUserTest() {
        objectMapper.findAndRegisterModules();
        int customUserId = 22;
        String updateName = "Пётр";

        CreateUserDto createUserDto = new CreateUserDto("Иван", "Иванов", "ivan@ivanov.ru",
                LocalDate.of(2000, 10, 1), new Passport("1234", "123456",
                LocalDate.of(2014, 5, 15), "МВД №1"),
                null, "password1");

        User testUser = modelMapper.map(createUserDto, User.class);
        testUser.setId(customUserId);

        Principal mockPrincipal = Mockito.mock(Principal.class);
        when(userPrincipalService.getUserFromPrincipal(Mockito.any(Principal.class))).thenReturn(testUser);

        when(userRepository.save(Mockito.any(User.class))).then(returnsFirstArg());

        FullUserDto updateUser;
        try {
            JsonPatch jsonPatch = new JsonPatch(List.of(new ReplaceOperation(new JsonPointer("/firstname"),
                    new TextNode(updateName))));

            updateUser = userService.update(jsonPatch, mockPrincipal);
        } catch (JsonPointerException e) {
            throw new NotUpdatedException("Невозможно обновить данные пользователя", e);
        }
        assertNotNull(updateUser);
        assertEquals(updateName, updateUser.getFirstname());
        assertEquals(createUserDto.getEmail(), updateUser.getEmail());
    }

    @Test
    @DisplayName("Обновление несуществующего пользователя")
    public void updateNonExistUserTest() {
        objectMapper.findAndRegisterModules();
        String updateName = "Пётр";
        Principal mockPrincipal = Mockito.mock(Principal.class);
        JsonPatch jsonPatch;

        try {
            jsonPatch = new JsonPatch(List.of(new ReplaceOperation(new JsonPointer("/firstname"),
                    new TextNode(updateName))));
        } catch (JsonPointerException e) {
            throw new NotUpdatedException("Невозможно обновить данные пользователя", e);
        }
        assertThrows(NotUpdatedException.class, () -> userService.update(jsonPatch, mockPrincipal));
    }

    @Test
    @DisplayName("Обновление пользователя с некорректным JsonPatch")
    public void incorrectUpdateUserTest() {
        objectMapper.findAndRegisterModules();
        int customUserId = 25;

        CreateUserDto createUserDto = new CreateUserDto("Иван", "Иванов", "ivan@ivanov.ru",
                LocalDate.of(2000, 10, 1), new Passport("1234", "123456",
                LocalDate.of(2014, 5, 15), "МВД №1"),
                null, "password1");
        User testUser = modelMapper.map(createUserDto, User.class);
        testUser.setId(customUserId);

        Principal mockPrincipal = Mockito.mock(Principal.class);
        when(userPrincipalService.getUserFromPrincipal(Mockito.any(Principal.class))).thenReturn(testUser);

        JsonPatch jsonPatch;
        try {
            jsonPatch = new JsonPatch(List.of(new ReplaceOperation(new JsonPointer("/fakeField"),
                    new TextNode("fakeText"))));
        } catch (JsonPointerException e) {
            throw new NotUpdatedException("Невозможно обновить данные пользователя", e);
        }

        assertThrows(NotUpdatedException.class, () -> userService.update(jsonPatch, mockPrincipal));
    }

    @Test
    @DisplayName("Корректное удаление пользователя")
    public void deleteUserTest() {
        int customUserId = 45;

        CreateUserDto createUserDto = new CreateUserDto("Иван", "Иванов", "ivan@ivanov.ru",
                LocalDate.of(2000, 10, 1), new Passport("1234", "123456",
                LocalDate.of(2014, 5, 15), "МВД №1"),
                null, "password1");

        User testUser = modelMapper.map(createUserDto, User.class);
        testUser.setId(customUserId);

        Principal mockPrincipal = Mockito.mock(Principal.class);
        when(userPrincipalService.getUserFromPrincipal(Mockito.any(Principal.class))).thenReturn(testUser);

        doNothing().when(userRepository).deleteById(anyInt());

        userService.delete(mockPrincipal);

        verify(userRepository).deleteById(anyInt());
    }

    @Test
    @DisplayName("Корректное получение пользователя по id")
    public void getUserByIdTest() {
        int customUserId = 47;

        CreateUserDto createUserDto = new CreateUserDto("Иван", "Иванов", "ivan@ivanov.ru",
                LocalDate.of(2000, 10, 1), new Passport("1234", "123456",
                LocalDate.of(2014, 5, 15), "МВД №1"),
                null, "password1");
        User testUser = modelMapper.map(createUserDto, User.class);
        testUser.setId(customUserId);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(testUser));

        UserDto userDto = userService.getById(customUserId);

        assertNotNull(userDto);
        assertEquals(createUserDto.getFirstname(), userDto.getFirstname());
        assertEquals(createUserDto.getLastname(), userDto.getLastname());
        assertEquals(createUserDto.getEmail(), userDto.getEmail());
    }

    @Test
    @DisplayName("Получение несуществующего пользователя")
    public void getUserByIdWithNotExistUserTest() {
        int customUserId = 49;

        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getById(customUserId));
    }
}

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@JdbcTest
@Sql("/schemaForTest.sql")
class UserServiceImplTest {
    private UserService userService;
    private final ModelMapper modelMapper = new ModelMapper();
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Mock
    private UserRepository userRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate = new JdbcTemplate(new DriverManagerDataSource());

    @BeforeEach
    public void setUp() {
        userService = new UserServiceImpl(userRepository, modelMapper, objectMapper);
    }

    @Test
    @DisplayName("Корректное создание пользователя")
    public void createUserTest() {
        int customUserId = 21;
        String createUserSql = "insert into users (id, firstname, lastname, email, birthday, registration_date)\n" +
                " values (?, 'Ivan', 'Ivanov', 'ivan@email.ru', '2000-10-01', '2023-11-20')";
        jdbcTemplate.update(createUserSql, customUserId);
        String getUserFromBdSql = "select * from users where id = ?";
        User testUser = getUserFromBd(getUserFromBdSql, customUserId);

        CreateUserDto createUserDto = new CreateUserDto("Иван", "Иванов", "ivan@ivanov.ru",
                LocalDate.of(2000, 10, 1), new Passport("1234", "123456",
                LocalDate.of(2014, 5, 15), "МВД №1"));

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

    // todo как написать jsonPatch?
    @Test
    @DisplayName("Корректное обновление пользователя")
    public void updateUserTest() {
        objectMapper.findAndRegisterModules();
        int customUserId = 22;
        String createUserSql = "insert into users (id, firstname, lastname, email, birthday, registration_date)\n" +
                " values (?, 'Ivan', 'Ivanov', 'ivan@email.ru', '2000-10-01', '2023-11-20')";
        jdbcTemplate.update(createUserSql, customUserId);
        String createPassportSql =
                "insert into passport (id, passport_series, passport_number, date_of_issue, issued_by)" +
                        " values (?, '1234', '123456', '2011-6-15', 'МВД №1')";
        jdbcTemplate.update(createPassportSql, customUserId);

        String getUserFromBdSql = "select * from users where id = ?";
//        String getUserWithPassportSql = "select * from users u left join passport p on u.id = p.id where u.id = ?";
        User testUser = getUserFromBd(getUserFromBdSql, customUserId);
//        testUser.setPassport(new Passport());
//        User testUser = getUserFromBd(getUserWithPassportSql, customUserId);
//        testUser.setPassport(new Passport("1234", "123456",
//                LocalDate.of(2014, 5, 15), "МВД №1"));


       /* CreateUserDto createUserDto = new CreateUserDto("Иван", "Иванов", "ivan@ivanov.ru",
                LocalDate.of(2000, 10, 1), new Passport("1234", "123456",
                LocalDate.of(2014, 5, 15), "МВД №1"));*/

        when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(testUser));

        User updateUser;
        try {
            JsonPatch jsonPatch = new JsonPatch(List.of(new ReplaceOperation(new JsonPointer("/firstname"),
                    new TextNode("Пётр"))));
            updateUser = userService.update(customUserId, jsonPatch);
        } catch (JsonPointerException e) {
            throw new UpdateException("Невозможно обновить данные пользователя", e);
        }
        assertNotNull(updateUser);
        assertEquals("Пётр", updateUser.getFirstname());
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

        assertThrows(NotFoundException.class, () -> {
            userService.getById(customUserId);
        });
    }

    private User getUserFromBd(String sql, int userId) {
        User user = new User();
        return jdbcTemplate.queryForObject(sql, (resultSet, rowNum) -> {
            user.setId(resultSet.getInt("id"));
            user.setFirstname(resultSet.getString("firstname"));
            user.setLastname(resultSet.getString("lastname"));
            user.setEmail(resultSet.getString("email"));
            user.setBirthday(resultSet.getDate("birthday").toLocalDate());
            user.setRegistrationDate(resultSet.getDate("registration_date").toLocalDate());
            return user;
        }, userId);
    }

    /*private User getUserFromBd(String sql, int userId) {
        User user = new User();
        Passport passport = new Passport();
        return jdbcTemplate.queryForObject(sql, (resultSet, rowNum) -> {
            user.setId(resultSet.getInt("id"));
            user.setFirstname(resultSet.getString("firstname"));
            user.setLastname(resultSet.getString("lastname"));
            user.setEmail(resultSet.getString("email"));
            user.setBirthday(resultSet.getDate("birthday").toLocalDate());
            user.setRegistrationDate(resultSet.getDate("registration_date").toLocalDate());
            passport.setId(resultSet.getInt("id"));
            passport.setSeries(resultSet.getString("passport_series"));
            passport.setNumber(resultSet.getString("passport_number"));
            passport.setDateOfIssue(resultSet.getDate("date_of_issue").toLocalDate());
            passport.setIssuedBy(resultSet.getString("issued_by"));
            user.setPassport(passport);
            return user;
        }, userId);
    }*/
}
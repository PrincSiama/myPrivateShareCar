package myPrivateShareCar.repository;

import myPrivateShareCar.dto.CreateUserDto;
import myPrivateShareCar.model.Passport;
import myPrivateShareCar.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    User user;

    @BeforeEach
    void setUp() {
        CreateUserDto createUserDto = new CreateUserDto("Пётр", "Петров", "petr@petrov.ru",
                LocalDate.of(2005, 5, 5), new Passport("2345", "456789",
                LocalDate.of(2019, 8, 3), "МВД №5"),
                null, "password1");
        User testUser = new ModelMapper().map(createUserDto, User.class);
        testUser.setRegistrationDate(LocalDate.now());
        user = userRepository.save(testUser);
    }

    @Test
    @DirtiesContext
    @DisplayName("Получение пользователя по email")
    void findByEmail() {
        String fakeEmail = "ivan@divan.com";
        Optional<User> userOptional1 = userRepository.findByEmail(fakeEmail);
        assertTrue(userOptional1.isEmpty());

        Optional<User> userOptional2 = userRepository.findByEmail(user.getEmail());
        assertTrue(userOptional2.isPresent());

        User testUser = userOptional2.get();
        assertNotNull(testUser);
        assertEquals(user.getId(), testUser.getId());
        assertEquals(user.getFirstname(), testUser.getFirstname());
        assertEquals(user.getPassport().getNumber(), testUser.getPassport().getNumber());
    }

    @Test
    @DirtiesContext
    @DisplayName("Получение пользователя по id")
    void findById() {
        int fakeId = 999;
        Optional<User> userOptional1 = userRepository.findById(fakeId);
        assertTrue(userOptional1.isEmpty());

        Optional<User> userOptional2 = userRepository.findById(user.getId());
        assertTrue(userOptional2.isPresent());

        User testUser = userOptional2.get();
        assertNotNull(testUser);
        assertEquals(user.getEmail(), testUser.getEmail());
        assertEquals(user.getFirstname(), testUser.getFirstname());
        assertEquals(user.getPassport().getNumber(), testUser.getPassport().getNumber());
    }

    @Test
    @DirtiesContext
    @DisplayName("Удаление пользователя по id")
    void deleteById() {
        List<User> userList = userRepository.findAll();
        assertEquals(1, userList.size());
        assertEquals(user.getId(), userList.get(0).getId());
        assertEquals(user.getEmail(), userList.get(0).getEmail());

        userRepository.deleteById(user.getId());
        List<User> emptyList = userRepository.findAll();
        assertTrue(emptyList.isEmpty());
    }
}
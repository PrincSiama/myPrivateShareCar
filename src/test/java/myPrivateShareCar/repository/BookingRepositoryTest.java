package myPrivateShareCar.repository;

import myPrivateShareCar.dto.CreateUserDto;
import myPrivateShareCar.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private CarRepository carRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Получение списка бронирований по id пользователя")
    void findByUser_IdOrderByStartRentAscTest() {
        int customOwnerId = 1;
        int customUserId = 2;
        int customCar1Id = 1;
        int customCar2Id = 2;
        int customCar3Id = 3;
        int customBooking1Id = 1;
        int customBooking2Id = 2;

        CreateUserDto createOwnerDto = new CreateUserDto("Иван", "Иванов", "ivan@ivanov.ru",
                LocalDate.of(2000, 10, 1), new Passport("1234", "123456",
                LocalDate.of(2014, 5, 15), "МВД №1"));
        User owner = new ModelMapper().map(createOwnerDto, User.class);
        owner.setId(customOwnerId);
        owner.setRegistrationDate(LocalDate.now());
        userRepository.save(owner);
        CreateUserDto createUserDto = new CreateUserDto("Пётр", "Петров", "petr@petrov.ru",
                LocalDate.of(2005, 5, 5), new Passport("2345", "456789",
                LocalDate.of(2019, 8, 3), "МВД №5"));
        User user = new ModelMapper().map(createUserDto, User.class);
        user.setId(customUserId);
        user.setRegistrationDate(LocalDate.now());
        userRepository.save(user);

        Car car1 = new Car(customCar1Id, "BMW", "530", 2019, "white",
                "2201 887900", "А001АА155",
                customOwnerId, 5000, new ArrayList<>());
        carRepository.save(car1);
        Car car2 = new Car(customCar2Id, "AUDI", "A6",
                2020, "red", "1344 168003", "А006АА47",
                customOwnerId, 5500, new ArrayList<>());
        carRepository.save(car2);
        Car car3 = new Car(customCar3Id, "LADA", "2106", 1995, "green",
                "0418 120087", "В210АХ06", customOwnerId, 1200, new ArrayList<>());
        carRepository.save(car3);

        List<Booking> bookingList1 = bookingRepository.findByUser_IdOrderByStartRentAsc(customUserId);
        assertTrue(bookingList1.isEmpty());

        Booking booking1 = new Booking(customBooking1Id, user, car1, LocalDate.now().plusDays(5),
                2, BookingStatus.WAITING, LocalDate.now().plusDays(7));
        bookingRepository.save(booking1);
        Booking booking2 = new Booking(customBooking2Id, user, car2, LocalDate.now().plusDays(7),
                3, BookingStatus.WAITING, LocalDate.now().plusDays(10));
        bookingRepository.save(booking2);

        List<Integer> bookingsIdList = bookingRepository.findByUser_IdOrderByStartRentAsc(customUserId)
                .stream().map(Booking::getId).collect(Collectors.toList());

        assertEquals(2, bookingsIdList.size());

        assertTrue(bookingsIdList.containsAll(List.of(booking1.getId(), booking2.getId())));
    }

    @Test
    @DisplayName("Получение списка всех бронирований пользователя с учётом статуса")
    void findAllByUser_IdAndBookingStatusOrderByStartRentAscTest() {
        int customOwnerId = 1;
        int customUserId = 2;
        int customCar1Id = 1;
        int customCar2Id = 2;
        int customBooking1Id = 1;
        int customBooking2Id = 2;
        int customBooking3Id = 3;

        CreateUserDto createOwnerDto = new CreateUserDto("Иван", "Иванов", "ivan@ivanov.ru",
                LocalDate.of(2000, 10, 1), new Passport("1234", "123456",
                LocalDate.of(2014, 5, 15), "МВД №1"));
        User owner = new ModelMapper().map(createOwnerDto, User.class);
        owner.setId(customOwnerId);
        owner.setRegistrationDate(LocalDate.now());
        userRepository.save(owner);
        CreateUserDto createUserDto = new CreateUserDto("Пётр", "Петров", "petr@petrov.ru",
                LocalDate.of(2005, 5, 5), new Passport("2345", "456789",
                LocalDate.of(2019, 8, 3), "МВД №5"));
        User user = new ModelMapper().map(createUserDto, User.class);
        user.setId(customUserId);
        user.setRegistrationDate(LocalDate.now());
        userRepository.save(user);

        Car car1 = new Car(customCar1Id, "BMW", "530", 2019, "white",
                "2201 887900", "А001АА155",
                customOwnerId, 5000, new ArrayList<>());
        carRepository.save(car1);
        Car car2 = new Car(customCar2Id, "AUDI", "A6",
                2020, "red", "1344 168003", "А006АА47",
                customOwnerId, 5500, new ArrayList<>());
        carRepository.save(car2);

        List<Booking> bookingList1 = bookingRepository.findAllByUser_IdAndBookingStatusOrderByStartRentAsc(
                customUserId, BookingStatus.WAITING);
        assertTrue(bookingList1.isEmpty());

        Booking booking1 = new Booking(customBooking1Id, user, car1, LocalDate.now().plusDays(5),
                2, BookingStatus.APPROVED, LocalDate.now().plusDays(7));
        bookingRepository.save(booking1);
        Booking booking2 = new Booking(customBooking2Id, user, car2, LocalDate.now().plusDays(7),
                3, BookingStatus.WAITING, LocalDate.now().plusDays(10));
        bookingRepository.save(booking2);
        Booking booking3 = new Booking(customBooking3Id, user, car2, LocalDate.now().plusDays(15),
                3, BookingStatus.WAITING, LocalDate.now().plusDays(18));
        bookingRepository.save(booking3);

        List<Booking> bookingList2 = bookingRepository.findAllByUser_IdAndBookingStatusOrderByStartRentAsc(
                customUserId, BookingStatus.APPROVED);
        assertEquals(1, bookingList2.size());
        List<Booking> bookingList3 = bookingRepository.findAllByUser_IdAndBookingStatusOrderByStartRentAsc(
                customUserId, BookingStatus.WAITING);
        assertEquals(2, bookingList3.size());
    }

    @Test
    @DisplayName("Получение списка бронирований по id владельца")
    void findAllByCar_OwnerIdOrderByStartRentAscTest() {
        int customOwnerId = 1;
        int customUserId = 2;
        int customCar1Id = 1;
        int customCar2Id = 2;
        int customBooking1Id = 1;
        int customBooking2Id = 2;

        CreateUserDto createOwnerDto = new CreateUserDto("Иван", "Иванов", "ivan@ivanov.ru",
                LocalDate.of(2000, 10, 1), new Passport("1234", "123456",
                LocalDate.of(2014, 5, 15), "МВД №1"));
        User owner = new ModelMapper().map(createOwnerDto, User.class);
        owner.setId(customOwnerId);
        owner.setRegistrationDate(LocalDate.now());
        userRepository.save(owner);
        CreateUserDto createUserDto = new CreateUserDto("Пётр", "Петров", "petr@petrov.ru",
                LocalDate.of(2005, 5, 5), new Passport("2345", "456789",
                LocalDate.of(2019, 8, 3), "МВД №5"));
        User user = new ModelMapper().map(createUserDto, User.class);
        user.setId(customUserId);
        user.setRegistrationDate(LocalDate.now());
        userRepository.save(user);

        Car car1 = new Car(customCar1Id, "BMW", "530", 2019, "white",
                "2201 887900", "А001АА155",
                customOwnerId, 5000, new ArrayList<>());
        carRepository.save(car1);
        Car car2 = new Car(customCar2Id, "AUDI", "A6",
                2020, "red", "1344 168003", "А006АА47",
                customOwnerId, 5500, new ArrayList<>());
        carRepository.save(car2);

        Booking booking1 = new Booking(customBooking1Id, user, car1, LocalDate.now().plusDays(5),
                2, BookingStatus.APPROVED, LocalDate.now().plusDays(7));
        bookingRepository.save(booking1);
        Booking booking2 = new Booking(customBooking2Id, user, car2, LocalDate.now().plusDays(7),
                3, BookingStatus.WAITING, LocalDate.now().plusDays(10));
        bookingRepository.save(booking2);

        List<Booking> bookingList = bookingRepository.findAllByCar_OwnerIdOrderByStartRentAsc(customOwnerId);
        assertEquals(2, bookingList.size());

        List<Booking> bookingList2 = bookingRepository.findAllByCar_OwnerIdAndBookingStatusOrderByStartRentAsc(
                customOwnerId, BookingStatus.APPROVED);
        assertEquals(1, bookingList2.size());
    }

    @Test
    @DisplayName("Получение списка бронирований по id автомобиля и датам")
    void bookingByRentDateTest() {
        int customOwnerId = 1;
        int customUserId = 2;
        int customCar1Id = 1;
        int customCar2Id = 2;
        int customBooking1Id = 1;
        int customBooking2Id = 2;

        CreateUserDto createOwnerDto = new CreateUserDto("Иван", "Иванов", "ivan@ivanov.ru",
                LocalDate.of(2000, 10, 1), new Passport("1234", "123456",
                LocalDate.of(2014, 5, 15), "МВД №1"));
        User owner = new ModelMapper().map(createOwnerDto, User.class);
        owner.setId(customOwnerId);
        owner.setRegistrationDate(LocalDate.now());
        userRepository.save(owner);
        CreateUserDto createUserDto = new CreateUserDto("Пётр", "Петров", "petr@petrov.ru",
                LocalDate.of(2005, 5, 5), new Passport("2345", "456789",
                LocalDate.of(2019, 8, 3), "МВД №5"));
        User user = new ModelMapper().map(createUserDto, User.class);
        user.setId(customUserId);
        user.setRegistrationDate(LocalDate.now());
        userRepository.save(user);

        Car car1 = new Car(customCar1Id, "BMW", "530", 2019, "white",
                "2201 887900", "А001АА155",
                customOwnerId, 5000, new ArrayList<>());
        carRepository.save(car1);
        Car car2 = new Car(customCar2Id, "AUDI", "A6",
                2020, "red", "1344 168003", "А006АА47",
                customOwnerId, 5500, new ArrayList<>());
        carRepository.save(car2);

        Booking booking1 = new Booking(customBooking1Id, user, car1, LocalDate.now().plusDays(5),
                2, BookingStatus.APPROVED, LocalDate.now().plusDays(7));
        bookingRepository.save(booking1);
        Booking booking2 = new Booking(customBooking2Id, user, car2, LocalDate.now().plusDays(7),
                3, BookingStatus.APPROVED, LocalDate.now().plusDays(10));
        bookingRepository.save(booking2);

        List<Booking> bookingList = bookingRepository.bookingByRentDate(customCar1Id,
                LocalDate.now().plusDays(100), LocalDate.now().plusDays(105));
        assertTrue(bookingList.isEmpty());

        List<Booking> bookingList2 = bookingRepository.bookingByRentDate(customCar2Id,
                LocalDate.now().plusDays(100), LocalDate.now().plusDays(105));
        assertTrue(bookingList2.isEmpty());

        List<Booking> bookingList3 = bookingRepository.bookingByRentDate(customCar1Id,
                LocalDate.now().plusDays(4), LocalDate.now().plusDays(6));
        assertEquals(1, bookingList3.size());

        List<Booking> bookingList4 = bookingRepository.bookingByRentDate(customCar2Id,
                LocalDate.now().plusDays(8), LocalDate.now().plusDays(9));
        assertEquals(1, bookingList4.size());

        List<Booking> bookingList5 = bookingRepository.bookingByRentDate(customCar2Id,
                LocalDate.now().plusDays(9), LocalDate.now().plusDays(15));
        assertEquals(1, bookingList5.size());
    }
}
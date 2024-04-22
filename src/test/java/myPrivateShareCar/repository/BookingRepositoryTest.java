/*
package myPrivateShareCar.repository;

import myPrivateShareCar.dto.CreateUserDto;
import myPrivateShareCar.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

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

    private User owner, user;
    private Car car1, car2, car3;

    @BeforeEach
    public void createUsersAndCars() {
        CreateUserDto createOwnerDto = new CreateUserDto("Иван", "Иванов", "ivan@ivanov.ru",
                LocalDate.of(2000, 10, 1), new Passport("1234", "123456",
                LocalDate.of(2014, 5, 15), "МВД №1"), "password1");
        owner = new ModelMapper().map(createOwnerDto, User.class);
        owner.setRegistrationDate(LocalDate.now());
        userRepository.save(owner);

        CreateUserDto createUserDto = new CreateUserDto("Пётр", "Петров", "petr@petrov.ru",
                LocalDate.of(2005, 5, 5), new Passport("2345", "456789",
                LocalDate.of(2019, 8, 3), "МВД №5"), "password2");
        user = new ModelMapper().map(createUserDto, User.class);
        user.setRegistrationDate(LocalDate.now());
        userRepository.save(user);

        car1 = new Car(0, "BMW", "530", 2019, "white",
                "2201 887900", "А001АА155",
                owner.getId(), 5000, new ArrayList<>());
        carRepository.save(car1);
        car2 = new Car(0, "AUDI", "A6",
                2020, "red", "1344 168003", "А006АА47",
                owner.getId(), 5500, new ArrayList<>());
        carRepository.save(car2);
        car3 = new Car(0, "LADA", "2106", 1995, "eggplant",
                "0418 120087", "В210АХ06",
                owner.getId(), 1200, new ArrayList<>());
        carRepository.save(car3);
    }

    @Test
    @DirtiesContext
    @DisplayName("Получение списка бронирований по id пользователя")
    void findByUser_IdOrderByStartRentAscTest() {
        List<Booking> bookingList1 = bookingRepository.findByUser_IdOrderByStartRentAsc(user.getId());
        assertTrue(bookingList1.isEmpty());

        Booking booking1 = new Booking(0, user, car1, LocalDate.now().plusDays(5),
                2, BookingStatus.WAITING, LocalDate.now().plusDays(7));
        bookingRepository.save(booking1);
        Booking booking2 = new Booking(0, user, car2, LocalDate.now().plusDays(7),
                3, BookingStatus.WAITING, LocalDate.now().plusDays(10));
        bookingRepository.save(booking2);

        List<Integer> bookingsIdList = bookingRepository.findByUser_IdOrderByStartRentAsc(user.getId())
                .stream().map(Booking::getId).collect(Collectors.toList());

        assertEquals(2, bookingsIdList.size());

        assertTrue(bookingsIdList.containsAll(List.of(booking1.getId(), booking2.getId())));
    }

    @Test
    @DirtiesContext
    @DisplayName("Получение списка всех бронирований пользователя с учётом статуса")
    void findAllByUser_IdAndBookingStatusOrderByStartRentAscTest() {
        List<Booking> bookingList1 = bookingRepository.findAllByUser_IdAndBookingStatusOrderByStartRentAsc(
                user.getId(), BookingStatus.WAITING);
        assertTrue(bookingList1.isEmpty());

        Booking booking1 = new Booking(0, user, car1, LocalDate.now().plusDays(5),
                2, BookingStatus.APPROVED, LocalDate.now().plusDays(7));
        bookingRepository.save(booking1);
        Booking booking2 = new Booking(0, user, car2, LocalDate.now().plusDays(7),
                3, BookingStatus.WAITING, LocalDate.now().plusDays(10));
        bookingRepository.save(booking2);
        Booking booking3 = new Booking(0, user, car2, LocalDate.now().plusDays(15),
                3, BookingStatus.WAITING, LocalDate.now().plusDays(18));
        bookingRepository.save(booking3);

        List<Booking> bookingList2 = bookingRepository.findAllByUser_IdAndBookingStatusOrderByStartRentAsc(
                user.getId(), BookingStatus.APPROVED);
        assertEquals(1, bookingList2.size());
        List<Booking> bookingList3 = bookingRepository.findAllByUser_IdAndBookingStatusOrderByStartRentAsc(
                user.getId(), BookingStatus.WAITING);
        assertEquals(2, bookingList3.size());
    }

    @Test
    @DirtiesContext
    @DisplayName("Получение списка бронирований по id владельца")
    void findAllByCar_OwnerIdOrderByStartRentAscTest() {
        Booking booking1 = new Booking(0, user, car1, LocalDate.now().plusDays(5),
                2, BookingStatus.APPROVED, LocalDate.now().plusDays(7));
        bookingRepository.save(booking1);
        Booking booking2 = new Booking(0, user, car2, LocalDate.now().plusDays(7),
                3, BookingStatus.WAITING, LocalDate.now().plusDays(10));
        bookingRepository.save(booking2);

        List<Booking> bookingList = bookingRepository.findAllByCar_OwnerIdOrderByStartRentAsc(owner.getId());
        assertEquals(2, bookingList.size());

        List<Booking> bookingList2 = bookingRepository.findAllByCar_OwnerIdAndBookingStatusOrderByStartRentAsc(
                owner.getId(), BookingStatus.APPROVED);
        assertEquals(1, bookingList2.size());
    }

    @Test
    @DirtiesContext
    @DisplayName("Получение списка бронирований по id автомобиля и датам")
    void bookingByRentDateTest() {
        Booking booking1 = new Booking(0, user, car1, LocalDate.now().plusDays(5),
                2, BookingStatus.APPROVED, LocalDate.now().plusDays(7));
        bookingRepository.save(booking1);
        Booking booking2 = new Booking(0, user, car2, LocalDate.now().plusDays(7),
                3, BookingStatus.APPROVED, LocalDate.now().plusDays(10));
        bookingRepository.save(booking2);

        List<Booking> bookingList = bookingRepository.bookingByRentDate(car1.getId(),
                LocalDate.now().plusDays(100), LocalDate.now().plusDays(105));
        assertTrue(bookingList.isEmpty());

        List<Booking> bookingList2 = bookingRepository.bookingByRentDate(car2.getId(),
                LocalDate.now().plusDays(100), LocalDate.now().plusDays(105));
        assertTrue(bookingList2.isEmpty());

        List<Booking> bookingList3 = bookingRepository.bookingByRentDate(car1.getId(),
                LocalDate.now().plusDays(4), LocalDate.now().plusDays(6));
        assertEquals(1, bookingList3.size());

        List<Booking> bookingList4 = bookingRepository.bookingByRentDate(car2.getId(),
                LocalDate.now().plusDays(8), LocalDate.now().plusDays(9));
        assertEquals(1, bookingList4.size());

        List<Booking> bookingList5 = bookingRepository.bookingByRentDate(car2.getId(),
                LocalDate.now().plusDays(9), LocalDate.now().plusDays(15));
        assertEquals(1, bookingList5.size());
    }
}*/

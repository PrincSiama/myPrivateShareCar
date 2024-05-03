package myPrivateShareCar.service;

import myPrivateShareCar.dto.BookingDto;
import myPrivateShareCar.dto.CreateBookingDto;
import myPrivateShareCar.dto.CreateUserDto;
import myPrivateShareCar.exception.NotCreatedException;
import myPrivateShareCar.exception.NotFoundException;
import myPrivateShareCar.exception.PermissionDeniedException;
import myPrivateShareCar.model.*;
import myPrivateShareCar.repository.BookingRepository;
import myPrivateShareCar.repository.CarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    private BookingService bookingService;
    @Mock
    private CarRepository carRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserPrincipalService userPrincipalService;
    private final ModelMapper modelMapper = new ModelMapper();

    User testUser;
    Car testCar;

    @BeforeEach
    public void setUp() {
        bookingService = new BookingServiceImpl(carRepository, bookingRepository, userPrincipalService, modelMapper);
    }

    @Test
    @DisplayName("Корректное создание бронирования")
    void correctCreateBookingTest() {
        int testUserId = 11;
        int carOwnerId = 31;
        int customCarId = 61;

        createTestUser(testUserId);
        testUser.setDriverLicense(new DriverLicense(testUserId, testUser, "1234", "654321",
                LocalDate.of(2015, 8, 6), "ГАИ №78"));

        createTestCar(customCarId, carOwnerId);

        CreateBookingDto createBookingDto = new CreateBookingDto(
                customCarId, LocalDate.now().plusDays(1), 5);

        Principal mockPrincipal = Mockito.mock(Principal.class);

        when(carRepository.findById(anyInt())).thenReturn(Optional.of(testCar));

        when(bookingRepository.bookingByRentDate(anyInt(), Mockito.any(LocalDate.class), Mockito.any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        when(bookingRepository.save(Mockito.any(Booking.class))).then(returnsFirstArg());

        BookingDto bookingDto = bookingService.create(createBookingDto, mockPrincipal);

        assertNotNull(bookingDto);
        assertEquals(customCarId, bookingDto.getCar().getId());
        assertEquals(testCar.getModel(), bookingDto.getCar().getModel());
        assertEquals(testUserId, bookingDto.getUser().getId());
        assertEquals(testUser.getEmail(), bookingDto.getUser().getEmail());
        assertEquals(createBookingDto.getStartRent(), bookingDto.getStartRent());
        assertEquals(BookingStatus.WAITING, bookingDto.getBookingStatus());
    }

    @Test
    @DisplayName("Создание бронирования несуществующего автомобиля")
    void createBookingWithNotExistCarTest() {
        int customCarId = 63;

        CreateBookingDto createBookingDto = new CreateBookingDto(
                customCarId, LocalDate.now().plusDays(1), 5);

        when(carRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.create(createBookingDto, null));
    }

    @Test
    @DisplayName("Создание бронирования пользователем без водительского удостоверения")
    void createBookingUserWithoutDriverLicenseTest() {
        int testUserId = 13;
        int carOwnerId = 33;
        int customCarId = 63;

        createTestUser(testUserId);

        createTestCar(customCarId, carOwnerId);

        CreateBookingDto createBookingDto = new CreateBookingDto(
                customCarId, LocalDate.now().plusDays(1), 5);

        Principal mockPrincipal = Mockito.mock(Principal.class);

        when(carRepository.findById(anyInt())).thenReturn(Optional.of(testCar));

        assertThrows(NotCreatedException.class, () -> bookingService.create(createBookingDto, mockPrincipal));
    }

    @Test
    @DisplayName("Создание бронирования забронированного автомобиля")
    void createBookingWithBookingCarTest() {
        int testUserId = 15;
        int carOwnerId = 35;
        int customCarId = 65;

        createTestUser(testUserId);
        testUser.setDriverLicense(new DriverLicense(testUserId, testUser, "1234", "654321",
                LocalDate.of(2015, 8, 6), "ГАИ №78"));

        createTestCar(customCarId, carOwnerId);

        CreateBookingDto createBookingDto = new CreateBookingDto(
                customCarId, LocalDate.now().plusDays(1), 5);

        Principal mockPrincipal = Mockito.mock(Principal.class);

        when(carRepository.findById(anyInt())).thenReturn(Optional.of(testCar));

        when(bookingRepository.bookingByRentDate(anyInt(), Mockito.any(LocalDate.class), Mockito.any(LocalDate.class)))
                .thenReturn(List.of(new Booking()));

        assertThrows(NotCreatedException.class, () -> bookingService.create(createBookingDto, mockPrincipal));
    }

    @Test
    @DisplayName("Корректное обновление статуса бронирования")
    void correctChangeStatusTest() {
        int carOwnerId = 37;
        int customCarId = 67;
        int customBookingId = 97;
        BookingStatus bookingStatus = BookingStatus.APPROVED;

        createTestUser(carOwnerId);

        Principal mockPrincipal = Mockito.mock(Principal.class);

        createTestCar(customCarId, carOwnerId);

        Booking booking = new Booking();
        booking.setId(customBookingId);
        booking.setCar(testCar);
        booking.setBookingStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));

        when(carRepository.findById(anyInt())).thenReturn(Optional.of(testCar));

        when(bookingRepository.save(Mockito.any(Booking.class))).then(returnsFirstArg());

        BookingDto bookingDto = bookingService.changeStatus(customBookingId, bookingStatus, mockPrincipal);

        assertNotNull(bookingDto);
        assertEquals(customCarId, bookingDto.getCar().getId());
        assertEquals(bookingStatus, bookingDto.getBookingStatus());
    }

    @Test
    @DisplayName("Обновление статуса несуществующего бронирования")
    void changeStatusForNotExistBookingTest() {
        int customBookingId = 99;
        BookingStatus bookingStatus = BookingStatus.APPROVED;

        when(bookingRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService
                .changeStatus(customBookingId, bookingStatus, null));
    }

    @Test
    @DisplayName("Обновление статуса бронирования с несуществующим автомобилем")
    void changeStatusWithNotExistCarTest() {
        int customBookingId = 101;
        BookingStatus bookingStatus = BookingStatus.APPROVED;

        when(bookingRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService
                .changeStatus(customBookingId, bookingStatus, null));
    }

    @Test
    @DisplayName("Обновление статуса бронирования не владельцем автомобиля")
    void changeStatusFromNotOwnerTest() {
        int carOwnerId = 43;
        int customCarId = 73;
        int customBookingId = 103;
        BookingStatus bookingStatus = BookingStatus.APPROVED;

        createTestUser(carOwnerId);

        Principal mockPrincipal = Mockito.mock(Principal.class);

        createTestCar(customCarId, carOwnerId + 1);

        Booking booking = new Booking();
        booking.setId(customBookingId);
        booking.setCar(testCar);
        booking.setBookingStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));

        when(carRepository.findById(anyInt())).thenReturn(Optional.of(testCar));

        assertThrows(PermissionDeniedException.class, () -> bookingService
                .changeStatus(customBookingId, bookingStatus, mockPrincipal));
        verify(bookingRepository, never()).save(Mockito.any(Booking.class));
    }

    @Test
    @DisplayName("Получение бронирования по id владельцем автомобиля")
    void getBookingByIdFromOwnerCarTest() {
        int carOwnerId = 45;
        int customCarId = 75;
        int customBookingId = 105;

        createTestUser(carOwnerId + 1);

        Principal mockPrincipal = Mockito.mock(Principal.class);

        createTestCar(customCarId, carOwnerId);

        Booking booking = new Booking();
        booking.setId(customBookingId);
        booking.setCar(testCar);
        booking.setUser(testUser);
        booking.setBookingStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));

        BookingDto bookingDto = bookingService.getBookingById(customBookingId, mockPrincipal);

        assertNotNull(bookingDto);
        assertEquals(customCarId, bookingDto.getCar().getId());
        assertEquals(BookingStatus.WAITING, bookingDto.getBookingStatus());
    }

    @Test
    @DisplayName("Получение бронирования по id создателем бронирования")
    void getBookingByIdFromCreatorBookingTest() {
        int bookingCreatorUserId = 47;
        int customCarId = 77;
        int customBookingId = 107;

        createTestUser(bookingCreatorUserId);

        Principal mockPrincipal = Mockito.mock(Principal.class);

        createTestCar(customCarId, bookingCreatorUserId);

        Booking booking = new Booking();
        booking.setId(customBookingId);
        booking.setUser(testUser);
        booking.setCar(testCar);
        booking.setBookingStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));

        BookingDto bookingDto = bookingService.getBookingById(customBookingId, mockPrincipal);

        assertNotNull(bookingDto);
        assertEquals(customCarId, bookingDto.getCar().getId());
        assertEquals(BookingStatus.WAITING, bookingDto.getBookingStatus());
    }

    @Test
    @DisplayName("Получение несуществующего бронирования по id")
    void getNotExistBookingByIdTest() {
        int fakeBookingId = 109;

        assertThrows(NotFoundException.class, () -> bookingService
                .getBookingById(fakeBookingId, null));
    }

    @Test
    @DisplayName("Получение бронирования по id случайным пользователем")
    void getBookingByIdFromUserTest() {
        int carOwnerId = 51;
        int customCarId = 81;
        int customBookingId = 111;

        CreateUserDto createUserDto1 = new CreateUserDto("Иван", "Иванов", "ivan@ivanov.ru",
                LocalDate.of(2000, 10, 1), new Passport("1234", "123456",
                LocalDate.of(2014, 5, 15), "МВД №1"),
                null, "password1");
        User carOwner = modelMapper.map(createUserDto1, User.class);
        carOwner.setId(carOwnerId);

        CreateUserDto createUserDto2 = new CreateUserDto("Пётр", "Петров", "pety@petrov.ru",
                LocalDate.of(2002, 12, 5), new Passport("2345", "234567",
                LocalDate.of(2014, 5, 15), "МВД №2"),
                null, "password2");
        User bookingCreatorUser = modelMapper.map(createUserDto2, User.class);
        bookingCreatorUser.setId(carOwnerId + 1);

        CreateUserDto createUserDto3 = new CreateUserDto("Евгений", "Ильин", "jeka@ilin.ru",
                LocalDate.of(1995, 5, 3), new Passport("3456", "345678",
                LocalDate.of(2014, 5, 15), "МВД №3"),
                null, "password3");
        User otherUser = modelMapper.map(createUserDto3, User.class);
        otherUser.setId(carOwnerId + 2);

        Principal mockPrincipal = Mockito.mock(Principal.class);

        when(userPrincipalService.getUserFromPrincipal(Mockito.any(Principal.class))).thenReturn(otherUser);

        createTestCar(customCarId, carOwnerId);

        Booking booking = new Booking();
        booking.setId(customBookingId);
        booking.setUser(bookingCreatorUser);
        booking.setCar(testCar);
        booking.setBookingStatus(BookingStatus.WAITING);

        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));

        assertThrows(PermissionDeniedException.class, () -> bookingService
                .getBookingById(customBookingId, mockPrincipal));
    }

    @Test
    @DisplayName("Получение бронирований пользователя")
    void getUserBookingsTest() {
        int customUserId = 53;

        createTestUser(customUserId);

        Principal mockPrincipal = Mockito.mock(Principal.class);

        when(bookingRepository.findByUser_IdOrderByStartRentAsc(customUserId))
                .thenReturn(List.of(new Booking(), new Booking()));

        List<BookingDto> bookingDtoList = bookingService.getUserBookings(null, mockPrincipal);

        verify(bookingRepository).findByUser_IdOrderByStartRentAsc(anyInt());
        verify(bookingRepository, never())
                .findAllByUser_IdAndBookingStatusOrderByStartRentAsc(anyInt(), Mockito.any(BookingStatus.class));
        assertFalse(bookingDtoList.isEmpty());
        assertEquals(2, bookingDtoList.size());
    }

    @Test
    @DisplayName("Получение бронирований пользователя с фильтром по статусу")
    void getUserBookingsWithBookingStatusTest() {
        int customUserId = 55;

        createTestUser(customUserId);

        Principal mockPrincipal = Mockito.mock(Principal.class);

        when(bookingRepository.findAllByUser_IdAndBookingStatusOrderByStartRentAsc(anyInt(),
                Mockito.any(BookingStatus.class))).thenReturn(List.of());

        List<BookingDto> bookingDtoList = bookingService.getUserBookings(BookingStatus.APPROVED, mockPrincipal);

        verify(bookingRepository)
                .findAllByUser_IdAndBookingStatusOrderByStartRentAsc(anyInt(), Mockito.any(BookingStatus.class));
        verify(bookingRepository, never()).findByUser_IdOrderByStartRentAsc(anyInt());
        assertTrue(bookingDtoList.isEmpty());
    }

    @Test
    @DisplayName("Получение бронирований владельца")
    void getOwnerBookingsTest() {
        int ownerId = 57;

        createTestUser(ownerId);

        Principal mockPrincipal = Mockito.mock(Principal.class);

        when(carRepository.findByOwnerId(anyInt(), any())).thenReturn(List.of(new Car()));

        when(bookingRepository.findAllByCar_OwnerIdOrderByStartRentAsc(ownerId))
                .thenReturn(List.of(new Booking(), new Booking()));

        List<BookingDto> bookingDtoList = bookingService.getOwnerBookings(null, mockPrincipal);

        verify(bookingRepository).findAllByCar_OwnerIdOrderByStartRentAsc(anyInt());
        verify(bookingRepository, never())
                .findAllByUser_IdAndBookingStatusOrderByStartRentAsc(anyInt(), Mockito.any(BookingStatus.class));
        assertFalse(bookingDtoList.isEmpty());
        assertEquals(2, bookingDtoList.size());
    }

    @Test
    @DisplayName("Получение бронирований владельца с фильтром по статусу")
    void getOwnerBookingsWithBookingStatusTest() {
        int ownerId = 59;

        createTestUser(ownerId);

        Principal mockPrincipal = Mockito.mock(Principal.class);

        when(carRepository.findByOwnerId(anyInt(), any())).thenReturn(List.of(new Car()));

        when(bookingRepository.findAllByCar_OwnerIdAndBookingStatusOrderByStartRentAsc(anyInt(),
                Mockito.any(BookingStatus.class))).thenReturn(List.of(new Booking(), new Booking(), new Booking()));

        List<BookingDto> bookingDtoList = bookingService.getOwnerBookings(BookingStatus.REJECTED, mockPrincipal);

        verify(bookingRepository)
                .findAllByCar_OwnerIdAndBookingStatusOrderByStartRentAsc(anyInt(), Mockito.any(BookingStatus.class));
        verify(bookingRepository, never()).findAllByCar_OwnerIdOrderByStartRentAsc(anyInt());
        assertFalse(bookingDtoList.isEmpty());
        assertEquals(3, bookingDtoList.size());
    }

    @Test
    @DisplayName("Получение бронирований владельца без автомобилей")
    void getOwnerBookingsWithoutCarsTest() {
        int ownerId = 59;

        createTestUser(ownerId);

        Principal mockPrincipal = Mockito.mock(Principal.class);

        when(carRepository.findByOwnerId(anyInt(), any())).thenReturn(List.of());

        assertThrows(PermissionDeniedException.class, () -> bookingService
                .getOwnerBookings(null, mockPrincipal));
    }

    private void createTestUser(int id) {
        CreateUserDto createUserDto = new CreateUserDto("Иван", "Иванов", "ivan@ivanov.ru",
                LocalDate.of(2000, 10, 1), new Passport("1234", "123456",
                LocalDate.of(2014, 5, 15), "МВД №1"),
                null, "password1");
        testUser = modelMapper.map(createUserDto, User.class);
        testUser.setId(id);

        when(userPrincipalService.getUserFromPrincipal(Mockito.any(Principal.class))).thenReturn(testUser);
    }

    private void createTestCar(int customCarId, int ownerId) {
        testCar = new Car(customCarId, "BMW", "530", 2019, "white",
                "2201 887900", "А001АА155", ownerId, 5000, new ArrayList<>());
    }
}
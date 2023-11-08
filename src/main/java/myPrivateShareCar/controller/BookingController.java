package myPrivateShareCar.controller;

import lombok.RequiredArgsConstructor;
import myPrivateShareCar.dto.BookingDto;
import myPrivateShareCar.dto.CreateBookingDto;
import myPrivateShareCar.model.BookingStatus;
import myPrivateShareCar.service.BookingService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/booking")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestBody @Valid CreateBookingDto createBookingDto,
                          @RequestHeader(value = "X-User-Id") int userId) {
        return bookingService.create(userId, createBookingDto.getCarId(),
                createBookingDto.getStartRent(), createBookingDto.getDurationRentInDays());
    }

    @PutMapping("/{bookingId}") //?status={status}
    public BookingDto updateStatus(@PathVariable int bookingId,
                                @RequestParam BookingStatus status,
                                @RequestHeader(value = "X-Owner-Id") int ownerId) {
        return bookingService.updateStatus(ownerId, bookingId, status);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable int bookingId,
                                     @RequestHeader(value = "X-User-Id") int userId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping //?status={status}
    public List<BookingDto> getUserBookings(@RequestHeader(value = "X-User-Id") int userId,
                                            @RequestParam(value = "status", required = false) BookingStatus status) {
        return bookingService.getUserBookings(userId, status);
    }

    @GetMapping("/owner") //?status={status}
    public List<BookingDto> getOwnerBookings(@RequestHeader(value = "X-Owner-Id") int ownerId,
                                             @RequestParam(value = "status", required = false) BookingStatus status) {
        return bookingService.getOwnerBookings(ownerId, status);
    }

}

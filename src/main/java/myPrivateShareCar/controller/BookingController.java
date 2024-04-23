package myPrivateShareCar.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import myPrivateShareCar.dto.BookingDto;
import myPrivateShareCar.dto.CreateBookingDto;
import myPrivateShareCar.model.BookingStatus;
import myPrivateShareCar.service.BookingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/booking")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestBody @Valid CreateBookingDto createBookingDto) {
        return bookingService.create(createBookingDto);
    }

    @PutMapping("/{bookingId}")
    public BookingDto updateStatus(@PathVariable int bookingId,
                                   @RequestParam BookingStatus status) {
        return bookingService.changeStatus(bookingId, status);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable int bookingId) {
        return bookingService.getBookingById(bookingId);
    }

    @GetMapping //?status={status}
    public List<BookingDto> getUserBookings(@RequestParam(value = "status", required = false) BookingStatus status) {
        return bookingService.getUserBookings(status);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestParam(value = "status", required = false) BookingStatus status) {
        return bookingService.getOwnerBookings(status);
    }

}

package myPrivateShareCar.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import myPrivateShareCar.dto.BookingDto;
import myPrivateShareCar.dto.CreateBookingDto;
import myPrivateShareCar.model.BookingStatus;
import myPrivateShareCar.service.BookingService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/booking")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestBody @Valid CreateBookingDto createBookingDto,
                             Principal principal) {
        return bookingService.create(createBookingDto, principal);
    }

    @PutMapping("/{bookingId}")
    public BookingDto changeStatus(@PathVariable int bookingId,
                                   @RequestParam BookingStatus status,
                                   Principal principal) {
        return bookingService.changeStatus(bookingId, status, principal);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable int bookingId,
                                     Principal principal) {
        return bookingService.getBookingById(bookingId, principal);
    }

    @GetMapping //?status={status}
    public List<BookingDto> getUserBookings(@RequestParam(value = "status", required = false) BookingStatus status,
                                            Principal principal) {
        return bookingService.getUserBookings(status, principal);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestParam(value = "status", required = false) BookingStatus status,
                                             Principal principal) {
        return bookingService.getOwnerBookings(status, principal);
    }

}

package myPrivateShareCar.controller;

import lombok.RequiredArgsConstructor;
import myPrivateShareCar.dto.CreateBookingDto;
import myPrivateShareCar.dto.GetBookingDto;
import myPrivateShareCar.model.Booking;
import myPrivateShareCar.service.BookingServiceImpl;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/booking")
public class BookingController {

    private final BookingServiceImpl bookingService;

    @PostMapping
    public Booking create(@RequestBody @Valid CreateBookingDto createBookingDto,
                          @RequestHeader(value = "X-User-Id") Integer userId) {
        return bookingService.create(userId, createBookingDto.getCarId(),
                createBookingDto.getStartRent(), createBookingDto.getDurationRent());
    }

    @PutMapping("/{bookingId}") //?approved={approved}
    public Booking approved(@PathVariable Integer bookingId,
                         @RequestParam boolean approved,
                         @RequestHeader(value = "X-Owner-Id") Integer ownerId) {
        return bookingService.approved(ownerId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public GetBookingDto getBookingById(@PathVariable Integer bookingId,
                                        @RequestHeader(value = "X-User-Id") Integer userId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<GetBookingDto> getAllUserBookings(@RequestHeader(value = "X-User-Id") Integer userId,
                                                  @RequestParam(value = "status",
                                                          required = false, defaultValue = "all") String status) {
        return bookingService.getAllUserBookings(userId, status.toUpperCase());
    }

    @GetMapping("/owner") //?status={status}
    public List<GetBookingDto> getAllOwnerCarBookings(@RequestHeader(value = "X-User-Id") Integer userId,
                                           @RequestParam(value = "status",
                                                   required = false, defaultValue = "all") String status) {
        return bookingService.getAllOwnerCarBookings(userId, status.toUpperCase());
    }

}

package myPrivateShareCar.service;

import lombok.AllArgsConstructor;
import myPrivateShareCar.model.BookingStatus;
import myPrivateShareCar.repository.BookingRepository;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@EnableScheduling
@AllArgsConstructor
public class ScheduledBookingStatusUpdate {
    private final BookingRepository bookingRepository;

    @Scheduled(cron = "@Daily")
    private void checkBookingStatus() {
        bookingRepository.findAllByApprovedBookingStatusAndEndRentBeforeNow().stream()
                .peek(booking -> booking.setBookingStatus(BookingStatus.FINISHED))
                .forEach(bookingRepository::save);
    }
}

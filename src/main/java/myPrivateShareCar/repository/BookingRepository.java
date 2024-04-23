package myPrivateShareCar.repository;

import myPrivateShareCar.model.Booking;
import myPrivateShareCar.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer>, JpaSpecificationExecutor<Booking> {
    List<Booking> findByUser_IdOrderByStartRentAsc(int userId);

    List<Booking> findAllByUser_IdAndBookingStatusOrderByStartRentAsc(int userId, BookingStatus bookingStatus);

    List<Booking> findAllByCar_OwnerIdOrderByStartRentAsc(int ownerId);

    List<Booking> findAllByCar_OwnerIdAndBookingStatusOrderByStartRentAsc(int ownerId, BookingStatus bookingStatus);

    List<Booking> findAllByUserIdAndCarIdAndBookingStatusIn(int userId, int carId,
                                                            List<BookingStatus> bookingStatuses);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.bookingStatus = 'APPROVED' AND b.endRent < CURRENT_DATE")
    List<Booking> findAllByApprovedBookingStatusAndEndRentBeforeNow();

    @Query("SELECT b FROM Booking b " +
            "WHERE b.car.id = ?1 AND b.bookingStatus = 'APPROVED' AND " +
            "((?3 >= b.startRent AND ?2 <= b.endRent) OR " +
            "(?2 BETWEEN b.startRent AND b.endRent AND ?3 BETWEEN b.startRent AND b.endRent))")
    List<Booking> bookingByRentDate(int carId, LocalDate startRent, LocalDate endRent);

}

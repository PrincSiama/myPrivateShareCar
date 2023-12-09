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

    List<Booking> findAllByUserIdAndCarIdAndBookingStatusOrBookingStatus(int userId, int carId,
                                                                         BookingStatus bookingStatusApproved,
                                                                         BookingStatus bookingStatusFinished);

    @Query("select b from Booking b" +
            " where b.car.id = ?1 and b.bookingStatus = 'APPROVED' and" +
            " ((?3 >= b.startRent and ?2 <= b.endRent) or " +
            "(?2 between b.startRent and b.endRent and ?3 between b.startRent and b.endRent))")
    List<Booking> bookingByRentDate(int carId, LocalDate startRent, LocalDate endRent);

}

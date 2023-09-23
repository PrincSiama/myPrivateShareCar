package myPrivateShareCar.repository;

import myPrivateShareCar.model.Booking;
import myPrivateShareCar.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JpaBookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByUserIdOrderByStartRentAsc(Integer userId);
    List<Booking> findAllByUserIdAndBookingStatusOrderByStartRentAsc(Integer userId, BookingStatus bookingStatus);

    @Query("select b from Booking b left join Car c on b.carId = c.id" +
            " where c.ownerId = ?1 order by b.startRent asc")
    List<Booking> findByOwnerId(Integer userId);

    @Query("select b from Booking b left join Car c on b.carId = c.id" +
            " where c.ownerId = ?1 and b.bookingStatus = ?2 order by b.startRent asc")
    List<Booking> findByOwnerIdAndStatus(Integer userId, BookingStatus bookingStatus);

    List<Booking> findByUserIdAndCarIdAndBookingStatusOrderByStartRentAsc(Integer userId, Integer carId,
                                                                          BookingStatus bookingStatus);

}

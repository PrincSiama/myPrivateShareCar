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

    // REVIEW: findAllByCar_OwnerIdOrderByStartRentAsc не подойдет? вместо Query
    @Query("select b from Booking b left join Car c on b.car.id = c.id" +
            " where c.ownerId = ?1 order by b.startRent asc")
    List<Booking> findByOwnerId(int userId);

    // REVIEW: аналогично предыдущему. Кажется, можно запросный метод сделать
    @Query("select b from Booking b left join Car c on b.car.id = c.id" +
            " where c.ownerId = ?1 and b.bookingStatus = ?2 order by b.startRent asc")
    List<Booking> findByOwnerIdAndStatus(int userId, BookingStatus bookingStatus);

    // REVIEW: я бы статусы передала в метод (на случай, если в коде переименуется, а тут можно забыть). И тогда
    // можно сделать запросный метод
    @Query("select b from Booking b" +
            " where b.user.id = ?1 and b.car.id = ?2 and (b.bookingStatus = 'APPROVED' or b.bookingStatus = 'FINISHED')")
    List<Booking> findByUserIdAndCarIdAndApprovedAndFinishedStatus(int userId, int carId);

    @Query("select b from Booking b" +
            " where b.car.id = ?1 and b.bookingStatus = 'APPROVED' and" +
            " ((?3 >= b.startRent and ?2 <= b.endRent) or " +
            "(?2 between b.startRent and b.endRent and ?3 between b.startRent and b.endRent))")
    List<Booking> bookingByRentDate(int carId, LocalDate startRent, LocalDate endRent);

}

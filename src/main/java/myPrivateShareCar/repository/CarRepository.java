package myPrivateShareCar.repository;

import myPrivateShareCar.model.Car;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface CarRepository extends JpaRepository<Car, Integer>, JpaSpecificationExecutor<Car> {
    List<Car> findByOwnerId(int ownerId, Pageable pageable);

    @Query("select c from Car c where" +
            " lower(c.brand) like lower(concat('%', :text, '%'))" +
            " or lower(c.model) like lower(concat('%', :text, '%'))" +
            " or lower(c.color) like lower(concat('%', :text, '%'))")
    List<Car> findAllContainingText(String text, Pageable pageable);

    @Query("select c from Car c where c.id not in (" +
            " select b.car.id from Booking b" +
            " where b.bookingStatus = 'APPROVED' and " +
            " ((:startRent <= b.endRent and :endRent >= b.startRent)" +
            " or (:startRent between b.startRent and b.endRent and :endRent between b.startRent and b.endRent)))")
    List<Car> carsByRentDate(LocalDate startRent, LocalDate endRent, Pageable pageable);

    @Query(" select c from Car c where" +
            " ((lower(c.brand) like lower(concat('%', :text, '%'))" +
            " or lower(c.model) like lower(concat('%', :text, '%'))" +
            " or lower(c.color) like lower(concat('%', :text, '%')))" +
            " and c.id not in (" +
            "select c from Car c left join Booking b on c.id = b.car.id" +
            " where b.bookingStatus = 'APPROVED' and" +
            " ((:startRent <= b.endRent and :endRent >= b.startRent)" +
            " or (:startRent between b.startRent and b.endRent and :endRent between b.startRent and b.endRent))))")
    List<Car> carsByRentDateAndContainingText(String text, LocalDate startRent, LocalDate endRent, Pageable pageable);
}

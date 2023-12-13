package myPrivateShareCar.specification;

import myPrivateShareCar.model.Booking;
import myPrivateShareCar.model.BookingStatus;
import myPrivateShareCar.model.Car;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class CarSpecification {

    public static Specification<Car> findText(String text) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("brand")), "%" + text.toLowerCase() + "%"),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("model")), "%" + text.toLowerCase() + "%"),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("color")), "%" + text.toLowerCase() + "%"));
    }

    public static Specification<Booking> findBookingByDate(LocalDate startRent, LocalDate endRent) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.equal(root.get("bookingStatus"), BookingStatus.APPROVED),
                criteriaBuilder.or(
                        criteriaBuilder.and(
                                criteriaBuilder.greaterThanOrEqualTo(root.get("endRent"), startRent),
                                criteriaBuilder.lessThanOrEqualTo(root.get("startRent"), endRent)
                        ),
                        criteriaBuilder.and(
                                criteriaBuilder.lessThanOrEqualTo(root.get("startRent"), startRent),
                                criteriaBuilder.greaterThanOrEqualTo(root.get("endRent"), startRent),
                                criteriaBuilder.lessThanOrEqualTo(root.get("startRent"), endRent),
                                criteriaBuilder.greaterThanOrEqualTo(root.get("endRent"), endRent)
                        )
                )
        );
    }

    public static Specification<Car> availableCars(List<Booking> bookingsForDates) {

        if (bookingsForDates.isEmpty()) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        } else {
            return (root, query, criteriaBuilder) -> criteriaBuilder.not(criteriaBuilder.in(root.get("id"))
                    .value(bookingsForDates.stream().map(booking -> booking.getCar().getId()).collect(Collectors.toList())));
        }
    }
}

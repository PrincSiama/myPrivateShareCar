package myPrivateShareCar.specification;

import lombok.AllArgsConstructor;
import lombok.Data;
import myPrivateShareCar.model.Booking;
import myPrivateShareCar.model.BookingStatus;
import myPrivateShareCar.model.Car;
import myPrivateShareCar.repository.BookingRepository;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class CarSpecification {
    private final BookingRepository bookingRepository;

    public List<Specification<Car>> searchParametersToSpecifications(String text,
                                                                     LocalDate startRent, LocalDate endRent) {
        List<Specification<Car>> specifications = new ArrayList<>();

        specifications.add(text == null ? null : findText(text));
        specifications.add((startRent == null && endRent == null) ? null : rentDate(startRent, endRent));

        return specifications.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    private Specification<Car> findText(String text) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("brand")), "%" + text.toLowerCase() + "%"),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("model")), "%" + text.toLowerCase() + "%"),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("color")), "%" + text.toLowerCase() + "%"));
    }

    private Specification<Booking> findBookingByDate(LocalDate startRent, LocalDate endRent) {
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

    private Specification<Car> rentDate(LocalDate startRent, LocalDate endRent) {
        List<Booking> bookingList = bookingRepository.findAll(findBookingByDate(startRent, endRent));

        if (bookingList.isEmpty()) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        } else {
            return (root, query, criteriaBuilder) -> criteriaBuilder.not(criteriaBuilder.in(root.get("id"))
                    .value(bookingList.stream().map(booking -> booking.getCar().getId()).collect(Collectors.toList())));
        }
    }
}

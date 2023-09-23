package myPrivateShareCar.repository;

import myPrivateShareCar.model.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JpaCarRepository extends JpaRepository<Car, Integer> {
    List<Car> findByOwnerId(Integer id);

    @Query("select c from Car c where" +
            " lower(c.brand) like lower(concat('%', :text, '%'))" +
            " or lower(c.model) like lower(concat('%', :text, '%'))" +
            " or lower(c.color) like lower(concat('%', :text, '%'))")
    List<Car> findAllContainingText(String text);
}

package myPrivateShareCar.repository;

import myPrivateShareCar.model.User;

public interface UserRepository {
    User create(User user);
    User update(User user);
    void delete(String id);
    User getById(String id);
    boolean isExistEmail(String email);
    boolean isExistPassport(String passport);
    boolean isCorrect(String id);
}

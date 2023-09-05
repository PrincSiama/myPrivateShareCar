package myPrivateShareCar.repository;

import myPrivateShareCar.model.User;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final Map<String, User> users = new HashMap<>(); // <id, user>

    @Override
    public User create(User user) {
        user.setId(UUID.randomUUID().toString());
        user.setRegistrationDate(LocalDate.now());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        // todo придумать нормальную логику обновления. Чтобы обновлялись только поля, которые отличаются
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(String id) {
        users.remove(id);
    }

    @Override
    public User getById(String id) {
        return users.get(id);
    }

    /*@Override
    public User getByEmail(String email) {
        return users.get(email);
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }*/

    @Override
    public boolean isExistEmail(String email) {
        return users.values().stream().anyMatch(user -> user.getEmail().equals(email));
    }

    @Override
    public boolean isExistPassport(String passport) {
        return users.values().stream().anyMatch(user -> user.getNumberPassport().equals(passport));
    }


    @Override
    public boolean isCorrect(String id) {
        return users.containsKey(id);
    }


}

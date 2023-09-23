package myPrivateShareCar.service;

import lombok.RequiredArgsConstructor;
import myPrivateShareCar.dto.CreateUserDto;
import myPrivateShareCar.dto.UpdateUserDto;
import myPrivateShareCar.exception.NotFoundException;
import myPrivateShareCar.model.User;
import myPrivateShareCar.repository.JpaUserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final JpaUserRepository userRepository;
    private final ModelMapper mapper;

    @Override
    public User create(CreateUserDto createUserDto) {
        User user = mapper.map(createUserDto, User.class);
        user.setRegistrationDate(LocalDate.now());
        return userRepository.save(user);
    }

    @Override
    public User update(Integer id, UpdateUserDto updateUserDto) { // json-patch
        if (userRepository.existsById(id)) {
            User oldUser = userRepository.findById(id).get();
            User user = mapper.map(updateUserDto, User.class);
            user.setId(oldUser.getId());
            user.setEmail(oldUser.getEmail());
            user.setBirthday(oldUser.getBirthday());
            user.setRegistrationDate(oldUser.getRegistrationDate());
            return userRepository.save(user);
        }
        throw new NotFoundException("Невозможно обновить пользователя. Пользователь с id "
                + id + " не найден");
    }

    @Override
    public void delete(Integer id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new NotFoundException("Невозможно удалить пользователя. Пользователь с id " + id + " не найден");
        }
    }

    @Override
    public User getById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Невозможно получить пользователя. Пользователь с id " + id + " не найден"));
    }

}

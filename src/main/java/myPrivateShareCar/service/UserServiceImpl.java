package myPrivateShareCar.service;

import lombok.RequiredArgsConstructor;
import myPrivateShareCar.dto.CreateUserDto;
import myPrivateShareCar.dto.UpdateUserDto;
import myPrivateShareCar.exception.AlreadyExistException;
import myPrivateShareCar.exception.NotFoundException;
import myPrivateShareCar.model.User;
import myPrivateShareCar.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper mapper;

    @Override
    public User create(CreateUserDto createUserDto) {
        if (!userRepository.isExistEmail(createUserDto.getEmail())
                && !userRepository.isExistPassport(createUserDto.getNumberPassport())) {
            User user = mapper.map(createUserDto, User.class);
            return userRepository.create(user);
        }
        throw new AlreadyExistException("Невозможно создать пользователя. Пользователь с такими данными уже существует");
    }

    @Override
    public User update(String id, UpdateUserDto updateUserDto) { // json-patch
        if (userRepository.isCorrect(id)) {
            User oldUser = getById(id);
            User user = mapper.map(updateUserDto, User.class);
            user.setId(oldUser.getId());
            user.setRegistrationDate(oldUser.getRegistrationDate());
            return userRepository.update(user);
        }
        throw new NotFoundException("Невозможно обновить пользователя. Пользователь с id "
                + id + " не найден");
    }

    @Override
    public void delete(String id) {
        if (userRepository.isCorrect(id)) {
            userRepository.delete(id);
        } else {
            throw new NotFoundException("Невозможно удалить пользователя. Пользователь с id " + id + " не найден");
        }
    }

    @Override
    public User getById(String id) {
        if (userRepository.isCorrect(id)) {
            return userRepository.getById(id);
        }
        throw new NotFoundException("Невозможно получить пользователя. Пользователь с id " + id + " не найден");
    }

    /*@Override
    public User getByEmail(String email) {
        if (userRepository.isExist(email)) {
            return userRepository.getByEmail(email);
        }
        throw new NotFoundException("Невозможно получить пользователя. Пользователь с email " + email + " не найден");
    }*/

    /*@Override
    public Collection<User> getAll() {
        return userRepository.getAll();
    }*/
}

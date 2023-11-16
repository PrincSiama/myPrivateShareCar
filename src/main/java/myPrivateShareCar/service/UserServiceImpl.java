package myPrivateShareCar.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import lombok.AllArgsConstructor;
import myPrivateShareCar.dto.CreateUserDto;
import myPrivateShareCar.dto.UpdateUserDto;
import myPrivateShareCar.dto.UserDto;
import myPrivateShareCar.exception.NotFoundException;
import myPrivateShareCar.exception.UpdateException;
import myPrivateShareCar.model.User;
import myPrivateShareCar.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper mapper;
    private final ObjectMapper objectMapper;

    @Override
    public User create(CreateUserDto createUserDto) {
        User user = mapper.map(createUserDto, User.class);
        user.setRegistrationDate(LocalDate.now());
        return userRepository.save(user);
    }

    @Override
    public User update(int id, JsonPatch jsonPatch) {
        UpdateUserDto updateUserDto = mapper.map(userRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Невозможно обновить" + " пользователя. Пользователь с id " + id + " не найден")),
                UpdateUserDto.class);
        try {
            JsonNode jsonNode = objectMapper.convertValue(updateUserDto, JsonNode.class);
            JsonNode patched = jsonPatch.apply(jsonNode);
            User updateUser = objectMapper.treeToValue(patched, User.class);
            return userRepository.save(updateUser);
        } catch (JsonPatchException | JsonProcessingException e) {
            throw new UpdateException("Невозможно обновить данные пользователя", e);
        }
    }

    @Override
    public void delete(int id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new NotFoundException("Невозможно удалить пользователя. Пользователь с id " + id + " не найден");
        }
    }

    @Override
    public UserDto getById(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Невозможно получить пользователя. Пользователь с id " + id +
                        " не найден"));
        return mapper.map(user, UserDto.class);
    }

}

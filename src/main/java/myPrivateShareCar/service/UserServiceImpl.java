package myPrivateShareCar.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import lombok.AllArgsConstructor;
import myPrivateShareCar.dto.CreateUserDto;
import myPrivateShareCar.dto.FullUserDto;
import myPrivateShareCar.dto.UserDto;
import myPrivateShareCar.exception.NotFoundException;
import myPrivateShareCar.exception.NotUpdatedException;
import myPrivateShareCar.model.User;
import myPrivateShareCar.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper mapper;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public FullUserDto create(CreateUserDto createUserDto) {
        User user = mapper.map(createUserDto, User.class);
        user.setRegistrationDate(LocalDate.now());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return mapper.map(userRepository.save(user), FullUserDto.class);
    }

    @Override
    public FullUserDto update(JsonPatch jsonPatch) {
        User user = getUserFromAuth();
        try {
            JsonNode jsonNode = objectMapper.convertValue(user, JsonNode.class);
            JsonNode patched = jsonPatch.apply(jsonNode);
            User updateUser = objectMapper.treeToValue(patched, User.class);
            updateUser.setPassword(user.getPassword());
            return mapper.map(userRepository.save(updateUser), FullUserDto.class);
        } catch (JsonPatchException | JsonProcessingException e) {
            throw new NotUpdatedException("Невозможно обновить данные пользователя", e);
        }
    }

    @Override
    public void delete() {
        userRepository.deleteById(getUserFromAuth().getId());
    }

    @Override
    public UserDto getById(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Невозможно получить пользователя. Пользователь с id " + id +
                        " не найден"));
        return mapper.map(user, UserDto.class);
    }

    private User getUserFromAuth() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}

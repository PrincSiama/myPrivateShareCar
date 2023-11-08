package myPrivateShareCar.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
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
        UpdateUserDto updateUserDto = mapper.map(userRepository.findById(id).orElseThrow(() -> new NotFoundException("Невозможно обновить" +
                " пользователя. Пользователь с id " + id + " не найден")), UpdateUserDto.class);
        //objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
try {
        JsonNode jsonNode = objectMapper.convertValue(updateUserDto, JsonNode.class);
        // todo отловить ошибки через errorHandler

    //  todo после первой строки выпадает в ошибку и не продолжает следующие строки. Отдельно работает.
/*[
    {"op": "add", "path": "/driverLicense", "value": {}},
    {"op": "replace", "path": "/driverLicense/series", "value": "5678"},
    {"op": "replace", "path": "/driverLicense/number", "value": "567890"},
    {"op": "replace", "path": "/driverLicense/dateOfIssue", "value": "2015-05-05"},
    {"op": "replace", "path": "/driverLicense/issuedBy", "value": "ГАИ 5555"}
]*/


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

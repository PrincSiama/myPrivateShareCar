package myPrivateShareCar.service;

import com.github.fge.jsonpatch.JsonPatch;
import myPrivateShareCar.dto.CreateUserDto;
import myPrivateShareCar.dto.UserDto;
import myPrivateShareCar.model.User;

public interface UserService {
    User create(CreateUserDto createUserDto);
    User update(int id, JsonPatch jsonPatch);
    void delete(int id);
    UserDto getById(int id);
}

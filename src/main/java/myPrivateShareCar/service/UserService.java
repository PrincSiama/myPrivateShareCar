package myPrivateShareCar.service;

import com.github.fge.jsonpatch.JsonPatch;
import myPrivateShareCar.dto.CreateUserDto;
import myPrivateShareCar.dto.UserDto;
import myPrivateShareCar.model.User;

public interface UserService {
    User create(CreateUserDto createUserDto);
    User update(JsonPatch jsonPatch);
    void delete();
    UserDto getById(int id);
}

package myPrivateShareCar.service;

import com.github.fge.jsonpatch.JsonPatch;
import myPrivateShareCar.dto.CreateUserDto;
import myPrivateShareCar.dto.FullUserDto;
import myPrivateShareCar.dto.UserDto;

public interface UserService {
    FullUserDto create(CreateUserDto createUserDto);
    FullUserDto update(JsonPatch jsonPatch);
    void delete();
    UserDto getById(int id);
}

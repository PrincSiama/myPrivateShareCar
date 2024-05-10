package myPrivateShareCar.service;

import com.github.fge.jsonpatch.JsonPatch;
import myPrivateShareCar.dto.CreateUserDto;
import myPrivateShareCar.dto.FullUserDto;
import myPrivateShareCar.dto.UserDto;

import java.security.Principal;

public interface UserService {
    FullUserDto create(CreateUserDto createUserDto);

    FullUserDto update(JsonPatch jsonPatch, Principal principal);

    void delete(Principal principal);

    UserDto getById(int id);
}

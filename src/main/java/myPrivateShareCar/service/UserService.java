package myPrivateShareCar.service;

import myPrivateShareCar.dto.CreateUserDto;
import myPrivateShareCar.dto.UpdateUserDto;
import myPrivateShareCar.model.User;

public interface UserService {
    User create(CreateUserDto createUserDto);
    User update(String id, UpdateUserDto updateUserDto);
    void delete(String id);
    User getById(String id);
}

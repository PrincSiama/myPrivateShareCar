package myPrivateShareCar.service;

import myPrivateShareCar.dto.CreateUserDto;
import myPrivateShareCar.dto.UpdateUserDto;
import myPrivateShareCar.model.User;

public interface UserService {
    User create(CreateUserDto createUserDto);
    User update(Integer id, UpdateUserDto updateUserDto);
    void delete(Integer id);
    User getById(Integer id);
}

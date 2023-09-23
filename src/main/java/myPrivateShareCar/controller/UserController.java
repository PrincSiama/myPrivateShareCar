package myPrivateShareCar.controller;

import lombok.RequiredArgsConstructor;
import myPrivateShareCar.dto.CreateUserDto;
import myPrivateShareCar.dto.UpdateUserDto;
import myPrivateShareCar.model.User;
import myPrivateShareCar.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public User create(@RequestBody @Valid CreateUserDto createUserDto) {
        return userService.create(createUserDto);
    }

    @PutMapping("/{id}")
    public User update(@PathVariable Integer id,
                       @RequestBody @Valid UpdateUserDto updateUserDto) {
        return userService.update(id, updateUserDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        userService.delete(id);
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable Integer id) {
        return userService.getById(id);
    }

}

package myPrivateShareCar.controller;

import com.github.fge.jsonpatch.JsonPatch;
import lombok.RequiredArgsConstructor;
import myPrivateShareCar.dto.CreateUserDto;
import myPrivateShareCar.dto.UserDto;
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

    @PatchMapping("/{id}")
    public User update(@PathVariable int id,
                       @RequestBody @Valid JsonPatch jsonPatch) {
        return userService.update(id, jsonPatch);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        userService.delete(id);
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable int id) {
        return userService.getById(id);
    }

}

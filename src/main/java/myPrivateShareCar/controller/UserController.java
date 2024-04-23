package myPrivateShareCar.controller;

import com.github.fge.jsonpatch.JsonPatch;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import myPrivateShareCar.dto.CreateUserDto;
import myPrivateShareCar.dto.FullUserDto;
import myPrivateShareCar.dto.UserDto;
import myPrivateShareCar.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public FullUserDto create(@RequestBody @Valid CreateUserDto createUserDto) {
        return userService.create(createUserDto);
    }

    @PatchMapping() //todo изменился путь. убрал {id}
    // todo проверить тесты
    public FullUserDto update(@RequestBody @Valid JsonPatch jsonPatch) {
        return userService.update(jsonPatch);
    }

    @DeleteMapping() //todo изменился путь. убрал {id}
    // todo проверить тесты
    public void delete() {
        userService.delete();
    }

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable int id) {
        return userService.getById(id);
    }


}

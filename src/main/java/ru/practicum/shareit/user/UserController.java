package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto){
        log.info("Проверка контроллера метода createUser userDto {}", userDto);
        return userService.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@RequestBody UserDto userDto,
                              @PathVariable long userId){
        log.info("Проверка контроллера метода updateUser userDto {}", userDto);
        log.info("Проверка контроллера метода updateUser userId {}", userId);
        return userService.updateUser(userDto, userId);
    }

    @GetMapping
    public List<UserDto> getAllUsers(){
        return userService.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getByIdUser(@PathVariable long userId){
        log.info("Проверка контроллера метода getByIdUser userId {}", userId);

        return userService.getByIdUser(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable long userId){
        log.info("Проверка контроллера метода deleteUserById userId {}", userId);
        userService.deleteUserById(userId);
    }

}

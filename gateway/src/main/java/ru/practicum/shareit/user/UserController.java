package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    private final UserClient userClient;

    public UserController(UserClient userClient) {
        this.userClient = userClient;
    }

    @PostMapping
    public ResponseEntity<Object> createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Проверка контроллера метода createUser userDto {}", userDto);
        return userClient.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@RequestBody UserDto userDto,
                                             @PathVariable
                                             @Positive(message = "Id не может быть <= 0")
                                             @NotNull(message = "Id не может быть null") Long userId) {
        log.info("Проверка контроллера метода updateUser userDto {}", userDto);
        log.info("Проверка контроллера метода updateUser userId {}", userId);
        return userClient.updateUser(userDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        return userClient.getAllUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getByIdUser(@PathVariable
                                                  @Positive(message = "Id не может быть <= 0")
                                                  @NotNull(message = "Id не может быть null") Long userId) {
        log.info("Проверка контроллера метода getByIdUser userId {}", userId);
        return userClient.getByIdUser(userId);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable
                                   @Positive(message = "Id не может быть <= 0")
                                   @NotNull(message = "Id не может быть null") Long userId) {
        log.info("Проверка контроллера метода deleteUserById userId {}", userId);
        userClient.deleteUserById(userId);
    }
}

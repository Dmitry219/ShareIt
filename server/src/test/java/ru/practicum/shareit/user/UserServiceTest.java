package ru.practicum.shareit.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {"db.name=test"})
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;
    private UserMapping userMapping;
    private UserDto ownerDto;
    private UserDto userDto;
    private User owner;
    private User user;

    @BeforeEach
    void setUp() {
        ownerDto = UserDto.builder()
                .id(1L)
                .name("owner")
                .email("owner@owner.ru")
                .build();

        userDto = UserDto.builder()
                .id(2L)
                .name("user")
                .email("user@user.ru")
                .build();

        owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@owner.ru")
                .build();

        user = User.builder()
                .id(2L)
                .name("user")
                .email("user@user.ru")
                .build();

    }

    @Test
    void createUserTrueOwner() {
        when(userRepository.save(any(User.class))).thenReturn(owner);

        UserDto createOwner = userService.createUser(ownerDto);

        assertEquals(ownerDto, createOwner);
    }

    @Test
    void createUserTrueUser() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto createUser = userService.createUser(userDto);

        assertEquals(userDto, createUser);
    }

    @Test
    void updateUserTrueUser() {
        user.setName("updateUser");
        when(userRepository.save(any(User.class))).thenReturn(user);

        userDto.setName("updateUser");
        UserDto updateUser = userService.updateUser(userDto, 1L);

        assertEquals(userDto, updateUser);
    }

    @Test
    void updateUserTrueOwner() {
        owner.setName("updateUser");
        when(userRepository.save(any(User.class))).thenReturn(owner);

        ownerDto.setName("updateUser");
        UserDto updateOwner = userService.updateUser(ownerDto, 1L);

        assertEquals(ownerDto, updateOwner);
    }

    @Test
    void getAllUsersTrueUserAndOwner() {
        when(userRepository.findAll()).thenReturn(List.of(user,owner));

        List<UserDto> users = userService.getAllUsers();

        assertEquals(2, users.size());
        assertEquals(userDto, users.get(0));
        assertEquals(ownerDto, users.get(1));
    }

    @Test
    void getByIdUserNotCorrectTrue() {
        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> userService.getByIdUser(99L));
        assertEquals("Пользователь с таким Id не существует!", notFoundException.getMessage());

    }
}
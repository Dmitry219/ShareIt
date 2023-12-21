package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Service
public interface UserService {
    public UserDto createUser(UserDto userDto);

    public UserDto updateUser(UserDto userDto, long userId);

    public List<UserDto> getAllUsers();

    public UserDto getByIdUser(long userId);

    public void deleteUserById(long userId);
}

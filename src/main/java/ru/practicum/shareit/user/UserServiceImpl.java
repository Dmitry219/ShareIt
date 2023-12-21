package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.exception.ValidationExceptionDuplicate;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapping userMapping;

    public UserServiceImpl(UserRepository userRepository, UserMapping userMapping) {
        this.userRepository = userRepository;
        this.userMapping = userMapping;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        log.info("Проерка сервиса метода createUser userDto {}", userDto);
        User user = userRepository.createUser(emailDuplicationCheckUser(userMapping.mapToUser(userDto)));
        return userMapping.mapToUserDto(user);
    }

    @Override
    public UserDto updateUser(UserDto userDto, long userId) {
        log.info("Проерка сервиса метода createUser userDto {}", userDto);
        log.info("Проерка сервиса метода createUser userId {}", userId);

        User user = userMapping.mapToUser(userDto);
        user.setId(userId);
        if (user.getEmail() == null) {
            user.setEmail(userRepository.getByIdUser(userId).getEmail());
        } else if (user.getName() == null) {
            if (user.getEmail().equals(userRepository.getByIdUser(userId).getEmail())) {
                log.info("если email равны {}, {}", user.getEmail(), userRepository.getByIdUser(userId).getEmail());
                user.setName(userRepository.getByIdUser(userId).getName());
            } else if (!user.getEmail().equals(userRepository.getByIdUser(userId).getEmail())) {
                log.info("Попал в последнюю проверку где email не != {}, {}",
                        user.getEmail(), userRepository.getByIdUser(userId).getEmail());
                emailDuplicationCheckUser(user).setName(userRepository.getByIdUser(userId).getName());
                user.setName(userRepository.getByIdUser(userId).getName());
            }
        }
        User newUser = userRepository.updateUser(user);
        return userMapping.mapToUserDto(newUser);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.getStorageUser().stream()
                .map(userMapping::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getByIdUser(long userId) {
        return userMapping.mapToUserDto(userRepository.getByIdUser(userId));
    }

    @Override
    public void deleteUserById(long userId) {
        userRepository.deleteUserById(userId);
    }


    public User emailDuplicationCheckUser(User user) {
        for (User value : userRepository.getStorageUser()) {
            if (value.getEmail().equals(user.getEmail())) {
                log.info("Дублирование email");
                throw new ValidationExceptionDuplicate(" email. Пользователь стаким email уже существует!");
            }
        }
        return user;
    }
}

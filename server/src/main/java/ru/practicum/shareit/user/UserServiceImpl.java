package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public UserDto createUser(UserDto userDto) {
        log.info("Проерка сервиса метода createUser userDto {}", userDto);

        User user = userRepository.save(UserMapping.mapToUser(userDto));
        return UserMapping.mapToUserDto(user);
    }

    @Transactional
    @Override
    public UserDto updateUser(UserDto userDto, long userId) {
        log.info("Проерка сервиса метода createUser userDto {}", userDto);
        log.info("Проерка сервиса метода createUser userId {}", userId);

        User user = UserMapping.mapToUser(userDto);
        user.setId(userId);
        if (user.getEmail() == null) {
            user.setEmail(userRepository.getById(userId).getEmail());
        } else if (user.getName() == null) {
            if (user.getEmail().equals(userRepository.getById(userId).getEmail())) {
                log.info("если email равны {}, {}", user.getEmail(), userRepository.getById(userId).getEmail());
                user.setName(userRepository.getById(userId).getName());
            } else if (!user.getEmail().equals(userRepository.getById(userId).getEmail())) {
                log.info("Попал в последнюю проверку где email не != {}, {}",
                        user.getEmail(), userRepository.getById(userId).getEmail());
                user.setName(userRepository.getById(userId).getName());
            }
        }

        User newUser = userRepository.save(user);
        return UserMapping.mapToUserDto(newUser);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapping::mapToUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getByIdUser(long userId) {
        checkId(userId);
        return UserMapping.mapToUserDto(userRepository.getById(userId));
    }

    @Transactional
    @Override
    public void deleteUserById(long userId) {
        userRepository.deleteById(userId);
    }

    private void checkId(long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Пользователь с таким Id не существует!");
        }
    }
}

package ru.practicum.shareit.user;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class UserCreateRequestJsonTest {
    @Autowired
    private JacksonTester<UserDto> json;

    @Test
    @SneakyThrows
    void testSerialize(){
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("nnn")
                .email("user@user.com")
                .build();

        JsonContent<UserDto> result = json.write(userDto);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.email");
        assertThat(result).hasJsonPath("$.name");

        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo("user@user.com");
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("nnn");
    }
}

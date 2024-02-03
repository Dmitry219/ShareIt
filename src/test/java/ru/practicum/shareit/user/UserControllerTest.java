package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private UserService userService;
    @Autowired
    private MockMvc mvc;
    UserDto userDtoRequest;
    UserDto userDtoResponse;
    UserDto userDto1;
    UserDto userDto2;

    @BeforeEach
    void setUp() {

        userDto1 = UserDto.builder()
                .id(1L)
                .email("user1@user.com")
                .name("user1")
                .build();

        userDto2 = UserDto.builder()
                .id(2L)
                .email("user2@user.com")
                .name("user2")
                .build();

        userDtoRequest = UserDto.builder()
                .email("user@user.com")
                .name("user")
                .build();


        userDtoResponse = UserDto.builder()
                .id(1L)
                .email("user@user.com")
                .name("user")
                .build();

    }

    @SneakyThrows
    @Test
    void createUserCorrectStatusOk() {
        when(userService.createUser(userDtoRequest))
                .thenReturn(userDtoResponse);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDtoResponse.getName())))
                .andExpect(jsonPath("$.email", is(userDtoResponse.getEmail())));
        verify(userService, times(1)).createUser(userDtoRequest);
    }

    @Test
    @SneakyThrows
    void createUserNotNameIsBadRequest() {

        userDtoRequest = UserDto.builder()
                .email("user@user.com")
                .name("")
                .build();

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, never()).createUser(userDtoRequest);
    }

    @Test
    @SneakyThrows
    void updateUserCorrectStatusOk() {

        mvc.perform(patch("/users/{userId}", 1L)
                        .content(mapper.writeValueAsString(userDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService, never()).createUser(userDtoRequest);

    }

    @Test
    @SneakyThrows
    void getAllUsersStatusOk() {
        when(userService.getAllUsers()).thenReturn(List.of(userDto1, userDto2));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(userDto1,userDto2))))
                .andExpect(jsonPath("$", hasSize(2)));
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    @SneakyThrows
    void getByIdUserStatusOk() {
        when(userService.getByIdUser(anyLong())).thenReturn(userDtoResponse);

        mvc.perform(get("/users/{userId}", 1L))
                .andExpect(content().json(mapper.writeValueAsString(userDtoResponse)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDtoResponse.getName())))
                .andExpect(jsonPath("$.email", is(userDtoResponse.getEmail())));;
        verify(userService, times(1)).getByIdUser(anyLong());
    }

    @Test
    @SneakyThrows
    void getByIdUserIncorrectIdStatusNotFound() {
        String errorMessage = "Пользователь с таким Id не существует!";
        when(userService.getByIdUser(anyLong())).thenThrow(new NotFoundException(errorMessage));

        mvc.perform(get("/users/{userId}", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", Matchers.containsString(errorMessage)));
        verify(userService, times(1)).getByIdUser(anyLong());
    }

    @Test
    @SneakyThrows
    void deleteUserById() {

        mvc.perform(delete("/users/{userId}", 1L))
                .andExpect(status().isOk());
        verify(userService, times(1)).deleteUserById(anyLong());
    }
}
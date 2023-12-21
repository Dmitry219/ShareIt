package ru.practicum.shareit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    UserController userController;
    @Autowired
    UserService userService;
    UserDto userDto;
    UserDto userDto1;
    ObjectMapper mapper;
    String jsonString;
    String jsonString1;

    @BeforeEach
    void create() throws JsonProcessingException {

        userDto = UserDto.builder()
                .name("DDD")
                .email("ddd@mail.ru")
                .build();

        userDto1 = UserDto.builder()
                .id(1L)
                .name("DDD")
                .email("dddmail.ru")
                .build();

        mapper = new ObjectMapper();
        jsonString = mapper.writeValueAsString(userDto);
        jsonString1 = mapper.writeValueAsString(userDto1);
    }

    @Test
    public void createUserStatusOK() throws Exception {

        mockMvc.perform(
                        post("/users")
                                .content(jsonString)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("DDD"))
                .andExpect(jsonPath(".email").value("ddd@mail.ru"));
    }

    @Test
    public void getUserStatusBadRequest() throws Exception {

        mockMvc.perform(
                        get("/users/{userId}", 1))
                .andExpect(status().isBadRequest())
                .andExpect(mockMvc -> mockMvc.getResolvedException().getClass().equals(NullPointerException.class));
    }

    @Test
    public void getUserStatusOk() throws Exception {
        userService.createUser(userDto);

        mockMvc.perform(
                        get("/users/{userId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("DDD"))
                .andExpect(jsonPath("$.email").value("ddd@mail.ru"));
    }

    @Test
    public void patchUserStatusOk() throws Exception {
        Long id = userService.createUser(UserDto.builder().name("newDDD").email("ddd@mail.ru").build()).getId();
        System.out.println(userService.getByIdUser(id));
        System.out.println(jsonString);
        mockMvc.perform(
                        patch("/users/{userId}", id)
                                .content(jsonString)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("DDD"))
                .andExpect(jsonPath("$.email").value("ddd@mail.ru"));
    }

    @Test
    public void deleteUserStatusOk() throws Exception {
        UserDto u = userService.createUser(userDto);
        mockMvc.perform(
                        delete("/users/{userId}", u.getId()))
                .andExpect(status().isOk());
    }
}

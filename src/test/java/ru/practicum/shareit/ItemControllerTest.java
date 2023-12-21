package ru.practicum.shareit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    UserController userController;
    @Autowired
    UserService userService;
    @Autowired
    ItemController itemController;
    @Autowired
    ItemService itemService;
    Item item;
    ItemDto itemDto;
    ItemDto itemDto1;
    ItemDto itemDto2;
    UserDto userDto;
    UserDto userDto1;
    UserDto userDto2;
    ObjectMapper mapper;
    String userJsonString;
    String userJsonString1;
    String itemJsonString;
    String itemJsonString1;
    TestRestTemplate restTemplate;
    @BeforeEach
    void create() throws JsonProcessingException {
        itemDto = ItemDto.builder()
                .name("IIIrrr")
                .description("iiiQQQRrR")
                .available(true)
                .build();

        itemDto1 = ItemDto.builder()
                .name("EEEttt")
                .description("eeeQqQrRRR")
                .available(false)
                .build();

        itemDto2 = ItemDto.builder()
                .name("AAAwww")
                .description("aaaqQqRrR")
                .available(false)
                .build();

        userDto = UserDto.builder()
                .name("DDD")
                .email("ddd@mail.ru")
                .build();

        userDto1 = UserDto.builder()
                .name("WWW2")
                .email("www@mail.ru")
                .build();

        userDto2 = UserDto.builder()
                .name("QQQ3")
                .email("qqq@mail.ru")
                .build();

        mapper = new ObjectMapper();
        userJsonString = mapper.writeValueAsString(userDto);
        userJsonString1 = mapper.writeValueAsString(userDto1);
        userJsonString1 = mapper.writeValueAsString(userDto2);
        userService.createUser(userDto);
        userService.createUser(userDto1);
        userService.createUser(userDto2);
        itemJsonString = mapper.writeValueAsString(itemDto);
        itemJsonString1 = mapper.writeValueAsString(itemDto1);
    }

    @Test
    public void createItemStatusOK() throws Exception {

        mockMvc.perform(
                        post("/items")
                                .content(itemJsonString)
                                .header("X-Sharer-User-Id", "1")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("IIIrrr"))
                .andExpect(jsonPath(".description").value("iiiQQQRrR"))
                .andExpect(jsonPath("$.available").value("true"));
    }

    @Test
    public void getItemStatusOK() throws Exception {
        itemService.createItem(itemDto, 1);

        mockMvc.perform(
                        get("/items/{itemId}", 1)
                                .content(itemJsonString)
                                .header("X-Sharer-User-Id", "1")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("IIIrrr"))
                .andExpect(jsonPath("$.description").value("iiiQQQRrR"))
                .andExpect(jsonPath("$.available").value("true"));
    }

    @Test
    public void getItemStatusBadRequest() throws Exception {

        mockMvc.perform(
                        get("/items/{itemsId}", 1))
                .andExpect(status().isBadRequest())
                .andExpect(mockMvc -> mockMvc.getResolvedException().getClass().equals(NullPointerException.class));
    }

    @Test
    public void patchUserStatusOk() throws Exception {
        Long itemId = itemService.createItem(ItemDto.builder().name("WWW").description("wWw").available(true).build(), 1).getId();
        itemService.createItem(itemDto, 1);

        mockMvc.perform(
                        patch("/items/{itemId}", itemId)
                                .content(itemJsonString)
                                .header("X-Sharer-User-Id", "1")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("IIIrrr"))
                .andExpect(jsonPath("$.description").value("iiiQQQRrR"))
                .andExpect(jsonPath("$.available").value("true"));
    }

    @Test
    public void getByIdUsersByIdItemStatusOK() throws Exception {
        itemService.createItem(itemDto, 3);

        mockMvc.perform(
                        get("/items/{itemId}", 1)
                                .content(itemJsonString)
                                .header("X-Sharer-User-Id", "3")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("IIIrrr"))
                .andExpect(jsonPath("$.description").value("iiiQQQRrR"))
                .andExpect(jsonPath("$.available").value("true"));
    }

    @Test
    public void getListItemsByTextStatusOK() throws Exception {
        itemService.createItem(itemDto, 1);
        itemService.createItem(itemDto1, 1);
        itemService.createItem(itemDto2, 3);

        mockMvc.perform(
                        get("/items/search?text=iii")
                                .content(itemJsonString)
                                .header("X-Sharer-User-Id", "1")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("IIIrrr"))
                .andExpect(jsonPath("$[0].description").value("iiiQQQRrR"))
                .andExpect(jsonPath("$[0].available").value("true"));

    }

    @Test
    public void getListAllItemsStatusOK() throws Exception {
        itemService.createItem(itemDto, 1);
        itemService.createItem(itemDto1, 1);

        mockMvc.perform(
                        get("/items")
                                //.content(itemJsonString)
                                .header("X-Sharer-User-Id", "1")
                                //.param("text", "iii")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("IIIrrr"))
                .andExpect(jsonPath("$[0].description").value("iiiQQQRrR"))
                .andExpect(jsonPath("$[0].available").value("true"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("EEEttt"))
                .andExpect(jsonPath("$[1].description").value("eeeQqQrRRR"))
                .andExpect(jsonPath("$[1].available").value("false"));

    }

}

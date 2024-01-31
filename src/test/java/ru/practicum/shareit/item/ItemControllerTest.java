package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemService itemService;
    @Autowired
    private MockMvc mvc;
    private ItemDto itemDtoRequest;
    private ItemDto itemDtoResponse;
    private CommentDto commentDtoRequest;
    private CommentDto commentDtoResponses;


    @BeforeEach
    void setUp() {
        itemDtoRequest = ItemDto.builder()
                .name("вещь")
                .description("Какая-то вещь")
                .available(true)
                .build();

        itemDtoResponse = ItemDto.builder()
                .id(1L)
                .name("вещь")
                .description("Какая-то вещь")
                .available(true)
                .build();

        commentDtoRequest = CommentDto.builder()
                .text("Привет какая-то вещь!")
                .authorName("user")
                .created(LocalDateTime.now()).build();

        commentDtoResponses = CommentDto.builder()
                .id(1L)
                .text("Привет какая-то вещь!")
                .authorName("user")
                .created(LocalDateTime.now()).build();
    }

    @Test
    @SneakyThrows
    void createItemStatusOk() {
        when(itemService.createItem(any(), anyLong())).thenReturn(itemDtoResponse);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemService, times(1)).createItem(any(), anyLong());
    }

    @Test
    @SneakyThrows
    void createItemNotName() {
        itemDtoRequest.setName(null);
        when(itemService.createItem(any(), anyLong())).thenReturn(itemDtoResponse);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(itemService, never()).createItem(any(), anyLong());
    }

    @Test
    @SneakyThrows
    void updateItemStatusOk() {
        itemDtoRequest.setName("updateName");
        itemDtoRequest.setDescription(null);

        itemDtoResponse.setName("updateName");
        when(itemService.updateItem(any(),anyLong(),anyLong())).thenReturn(itemDtoResponse);

        mvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(itemDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoResponse.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoResponse.getDescription())));

        verify(itemService, times(1)).updateItem(any(), anyLong(), anyLong());
    }

    @Test
    @SneakyThrows
    void getItemByIdStatusOk() {
        when(itemService.getByIdItem(anyLong(),anyLong())).thenReturn(itemDtoResponse);

        mvc.perform(get("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoResponse.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoResponse.getDescription())));
        verify(itemService,times(1)).getByIdItem(anyLong(),anyLong());
    }

    @Test
    @SneakyThrows
    void getListItemsByIdUserStatusOk() {
        when(itemService.getListItemsByIdUser(anyLong(),anyInt(),anyInt())).thenReturn(List.of(itemDtoResponse));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "1")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(itemService, times(1)).getListItemsByIdUser(anyLong(),anyInt(),anyInt());
    }

    @Test
    @SneakyThrows
    void getListItemsByTextStatusOk() {
        when(itemService.getListItemsByText(anyString(),anyLong(),anyInt(),anyInt()))
                .thenReturn(List.of(itemDtoResponse));

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", "1")
                        .param("text", "ВеЩь")
                        .param("from", "1")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(itemService, times(1))
                .getListItemsByText(anyString(),anyLong(),anyInt(),anyInt());
    }

    @Test
    @SneakyThrows
    void createCommentStatusOk() {
        when(itemService.createComment(any(),anyLong(),anyLong())).thenReturn(commentDtoResponses);

        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(commentDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemService, times(1)).createComment(any(),anyLong(),anyLong());
    }
}
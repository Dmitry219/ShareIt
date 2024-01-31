package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.model.ItemDbForRequest;
import ru.practicum.shareit.request.dto.ItemReqListItemsDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private ItemRequestService itemRequestService;
    @Autowired
    private MockMvc mvc;
    private ItemRequestDto itemRequestDtoRequest;
    private ItemRequestDto itemRequestDtoResponse;
    private ItemReqListItemsDto itemReqListItemsDto;

    @BeforeEach
    void setUp() {
        itemRequestDtoRequest = ItemRequestDto.builder()
                .description("Швабра дерекенная")
                .created(LocalDateTime.now())
                .build();

        itemRequestDtoResponse = ItemRequestDto.builder()
                .id(1L)
                .description("Швабра дерекенная")
                .created(LocalDateTime.now())
                .build();

        itemReqListItemsDto = ItemReqListItemsDto.builder()
                .id(1L)
                .description("швабра школльная")
                .created(LocalDateTime.now())
                .items(List.of(ItemDbForRequest.builder()
                        .id(1L)
                        .name("швабра")
                        .description("швабра для всех типов полов")
                        .available(true)
                        .requestId(1L)
                        .build()))
                .build();
    }

    @Test
    @SneakyThrows
    void createItemRequestStatusOk() {
        when(itemRequestService.createItemRequest(any(), anyLong()))
                .thenReturn(itemRequestDtoResponse);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDtoResponse.getDescription())));
        verify(itemRequestService, times(1)).createItemRequest(itemRequestDtoRequest, 1L);
    }

    @Test
    @SneakyThrows
    void createItemRequestEmptyDescription() {
        when(itemRequestService.createItemRequest(any(), anyLong()))
                .thenReturn(itemRequestDtoResponse);

        itemRequestDtoRequest.setDescription(null);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(itemRequestService, never()).createItemRequest(itemRequestDtoRequest, 1L);
    }

    @Test
    @SneakyThrows
    void getListItemRequestDtoByIdUserStatusOk() {

        when(itemRequestService.getListItemRequestDtoByIdUser(anyLong())).thenReturn(List.of(itemReqListItemsDto));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
        verify(itemRequestService, times(1)).getListItemRequestDtoByIdUser(anyLong());
    }

    @Test
    @SneakyThrows
    void getListAllItemRequestDtoExpectByIdUserStatusOk() {

        when(itemRequestService.getListAllItemRequestDtoExpectByIdUser(anyLong(),anyInt(),anyInt()))
                .thenReturn(List.of(itemReqListItemsDto));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", "1")
                        .param("from", "1")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
        verify(itemRequestService, times(1))
                .getListAllItemRequestDtoExpectByIdUser(anyLong(),anyInt(),anyInt());
    }

    @Test
    @SneakyThrows
    void getItemRequestByIdStatusOk() {
        when(itemRequestService.getByItemRequestId(anyLong(),anyLong()))
                .thenReturn(itemReqListItemsDto);

        mvc.perform(get("/requests/{requestId}", 1L)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());
        verify(itemRequestService, times(1))
                .getByItemRequestId(anyLong(),anyLong());
    }
}
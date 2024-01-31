package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.apache.coyote.http11.upgrade.UpgradeServletOutputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mvc;
    private BookingDtoRequest bookingDtoRequest;
    private BookingDtoResponse bookingDtoResponse;

    @BeforeEach
    void setUp() {
        bookingDtoRequest = BookingDtoRequest.builder()
                .itemId(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusMinutes(5L)).build();

        bookingDtoResponse = BookingDtoResponse.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusMinutes(10L))
                .item(ItemDto.builder()
                        .id(1L)
                        .name("вещь")
                        .description("какая-то вещь")
                        .available(true)
                        .lastBooking(BookingDtoShort.builder()
                                .id(1L)
                                .start(LocalDateTime.now())
                                .end(LocalDateTime.now().plusMinutes(1L))
                                .bookerId(1L)
                                .build())
                        .nextBooking(BookingDtoShort.builder()
                                .id(1L)
                                .start(LocalDateTime.now())
                                .end(LocalDateTime.now().plusMinutes(3L))
                                .bookerId(1L)
                                .build())
                        .comments(List.of(CommentDto.builder()
                                        .id(1L)
                                        .text("нужна вещь")
                                        .authorName("user")
                                        .created(LocalDateTime.now())
                                .build()))
                        .requestId(1L)
                        .build())
                .booker(UserDto.builder()
                        .id(1L)
                        .name("user")
                        .email("user@user.com")
                        .build())
                .status(StatusType.APPROVED)
                .build();
    }

    @Test
    @SneakyThrows
    void createBookingStatusOk() {
        when(bookingService.createBooking(any(),anyLong())).thenReturn(bookingDtoResponse);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .content(mapper.writeValueAsString(bookingDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(bookingService,times(1)).createBooking(any(),anyLong());
    }

    @Test
    @SneakyThrows
    void updateBooking() {
        bookingDtoResponse.setItem(ItemDto.builder()
                .id(1L)
                .name("вещь")
                .description("какая-то вещь")
                .available(false)
                .lastBooking(BookingDtoShort.builder()
                        .id(1L)
                        .start(LocalDateTime.now())
                        .end(LocalDateTime.now().plusMinutes(1L))
                        .bookerId(1L)
                        .build())
                .nextBooking(BookingDtoShort.builder()
                        .id(1L)
                        .start(LocalDateTime.now())
                        .end(LocalDateTime.now().plusMinutes(3L))
                        .bookerId(1L)
                        .build())
                .comments(List.of(CommentDto.builder()
                        .id(1L)
                        .text("нужна вещь")
                        .authorName("user")
                        .created(LocalDateTime.now())
                        .build()))
                .requestId(1L)
                .build());
        when(bookingService.updateBooking(anyLong(),any(),anyLong())).thenReturn(bookingDtoResponse);

        mvc.perform(patch("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", "1")
                        .param("approved", "false")
                        .content(mapper.writeValueAsString(bookingDtoRequest))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(bookingService,times(1)).updateBooking(anyLong(),any(),anyLong());
    }

    @Test
    @SneakyThrows
    void getBookingByIdByUserId() {
        when(bookingService.getBookingByIdByUserId(anyLong(),anyLong())).thenReturn(bookingDtoResponse);

        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .header("X-Sharer-User-Id", "1"))
                .andExpect(status().isOk());
        verify(bookingService,times(1)).getBookingByIdByUserId(anyLong(),anyLong());
    }

    @Test
    @SneakyThrows
    void getAllBookingsBookerByIdAndStatesByStatusOk() {
        when(bookingService.getAllBookingsBookerByIdAndStatesBy(anyString(),anyLong(),anyInt(),anyInt()))
                .thenReturn(List.of(bookingDtoResponse));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(bookingService,times(1))
                .getAllBookingsBookerByIdAndStatesBy(anyString(),anyLong(),anyInt(),anyInt());
    }

    @Test
    @SneakyThrows
    void getAllBookingsOwnerByIdStatusOk() {
        when(bookingService.getAllBookingsOwnerById(anyString(),anyLong(),anyInt(),anyInt()))
                .thenReturn(List.of(bookingDtoResponse));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", "1")
                        .param("state", "ALL")
                        .param("from", "1")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(bookingService,times(1))
                .getAllBookingsOwnerById(anyString(),anyLong(),anyInt(),anyInt());
    }
}
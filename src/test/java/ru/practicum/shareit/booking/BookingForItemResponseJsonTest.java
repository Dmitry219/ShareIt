package ru.practicum.shareit.booking;

import static org.assertj.core.api.Assertions.assertThat;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

@JsonTest
public class BookingForItemResponseJsonTest {
    @Autowired
    private JacksonTester<BookingDtoResponse> json;

    @Test
    @SneakyThrows
    void testSerialize() {
        BookingDtoResponse bookingDtoResponse = BookingDtoResponse.builder()
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

        JsonContent<BookingDtoResponse> result = json.write(bookingDtoResponse);

        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.booker");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).hasJsonPath("$.item");
        assertThat(result).hasJsonPath("$.status");
    }
}

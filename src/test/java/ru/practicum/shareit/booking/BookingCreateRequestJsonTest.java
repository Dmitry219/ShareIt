package ru.practicum.shareit.booking;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

@JsonTest
public class BookingCreateRequestJsonTest {
    @Autowired
    private JacksonTester<BookingDtoRequest> json;

    @Test
    @SneakyThrows
    void testsSerialize() {
        BookingDtoRequest bookingDtoRequest = BookingDtoRequest.builder()
                .itemId(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusMinutes(5L)).build();

        JsonContent<BookingDtoRequest> result = json.write(bookingDtoRequest);
        assertThat(result).hasJsonPath("$.itemId");
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).extractingJsonPathNumberValue("$.itemId")
                .isEqualTo(1);
        assertThat(result).hasJsonPathValue("$.start");
        assertThat(result).hasJsonPathValue("$.end");
    }
}

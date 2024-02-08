package ru.practicum.shareit.request;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemRequestCreateJsonTest {
    @Autowired
    private JacksonTester<ItemRequestDto> json;

    @Test
    @SneakyThrows
    void testSerialize() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("ddd")
                .build();

        JsonContent<ItemRequestDto> result = json.write(itemRequestDto);
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("ddd");
    }
}

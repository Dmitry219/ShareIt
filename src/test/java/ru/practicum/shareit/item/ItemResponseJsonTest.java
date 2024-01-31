package ru.practicum.shareit.item;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemResponseJsonTest {
    @Autowired
    private JacksonTester<ItemDto> json;

    @Test
    @SneakyThrows
    void testSerialize() {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("item")
                .description("item description")
                .available(true)
                .requestId(1L)
                .build();

        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("tttt")
                .authorName("dima")
                .created(LocalDateTime.now())
                .build();;

        itemDto.setComments(List.of(commentDto));

        JsonContent<ItemDto> result = json.write(itemDto);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).hasJsonPath("$.available");
        assertThat(result).hasJsonPath("$.comments");
        assertThat(result).hasJsonPath("$.requestId");

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("item");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("item description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.comments[0].text").isEqualTo("tttt");
        assertThat(result).extractingJsonPathStringValue("$.comments[0].authorName").isEqualTo("dima");
        assertThat(result).hasJsonPathValue("$.comments[0].created");
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }
}

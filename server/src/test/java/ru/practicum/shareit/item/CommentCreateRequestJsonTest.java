package ru.practicum.shareit.item;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.comment.CommentDto;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class CommentCreateRequestJsonTest {
    @Autowired
    private JacksonTester<CommentDto> json;

    @Test
    @SneakyThrows
    void testSerialize() throws Exception {
        CommentDto commentDto = CommentDto.builder()
                .text("Comment text").build();

        JsonContent<CommentDto> result = json.write(commentDto);
        assertThat(result).hasJsonPath("$.text");
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("Comment text");
    }
}

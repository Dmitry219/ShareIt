package ru.practicum.shareit.comment;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

public class CommentMapping {

    //метод конвертации bookingDtoRequest в Booking при создании и отправке в бд
    public static Comment toComment(CommentDto commentDto, Item item, User user) {
        return Comment.builder()
                .text(commentDto.getText())
                .itemId(item)
                .authorId(user)
                .created(LocalDateTime.now())
                .build();
    }

    //метод конвертации Booking в BookingDtoResponse при создании и отправки на фронт
    public static CommentDto toCommentDto(Comment comment) {

        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthorId().getName())
                .created(comment.getCreated())
                .build();
    }
}

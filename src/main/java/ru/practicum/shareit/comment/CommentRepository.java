package ru.practicum.shareit.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(value = "SELECT * FROM COMMENTS c WHERE ITEM_ID = ?1 AND" +
            " ITEM_ID IN (SELECT ITEM_ID FROM BOOKINGS b WHERE BOOKER_ID = ?2);",
            nativeQuery = true)
    List<Comment> getCommentByItemIdAndByBookerId(long itemId, long ownerId);

    @Query(value = "SELECT * FROM COMMENTS c WHERE ITEM_ID IN" +
            " (SELECT ID FROM ITEMS i WHERE ID = ?1" +
            " AND OWNER_ID = ?2);",
            nativeQuery = true)
    List<Comment> getCommentByItemIdAndByOwnerId(long itemId, long ownerId);
}

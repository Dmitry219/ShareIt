package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query(value = "SELECT b.* FROM BOOKINGS b , ITEMS i , USERS u " +
            "WHERE b.ID = ?1 " +
            "AND u.ID = b.BOOKER_ID " +
            "AND i.ID = b.ITEM_ID ;", nativeQuery = true)
    Booking getBookingById(long bookingId);

    Booking findFirstByItemId(long itemId);

    List<Booking> findAllByBookerIdOrderByIdDesc(long userId, PageRequest pageRequest);

    List<Booking> findAllByItemOwnerIdOrderByIdDesc(long ownerId, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStartAfterAndEndAfterOrderByIdDesc(long userId, LocalDateTime localDateTimeNow1, LocalDateTime localDateTimeNow2, PageRequest pageRequest);

    List<Booking> findAllByItemOwnerIdAndStartAfterAndEndAfterOrderByIdDesc(long ownerId, LocalDateTime localDateTimeNow1, LocalDateTime localDateTimeNow2, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndStatus(long userID, String state, PageRequest pageRequest);

    List<Booking> findAllByItemOwnerIdAndStatus(long ownerId, String state, PageRequest pageRequest);

    @Query(value = "SELECT * FROM BOOKINGS b " +
            "WHERE ITEM_ID = ?1 " +
            "AND START_DATE < ?2 " +
            "AND STATUS = 'APPROVED' ORDER BY START_DATE DESC LIMIT 1;",
            nativeQuery = true)
    Booking getLastBookigByItemIdStatusApproved(long itemId, LocalDateTime nowTime);

    @Query(value = "SELECT * FROM BOOKINGS b " +
            "WHERE ITEM_ID = ?1 " +
            "AND START_DATE > ?2 " +
            "AND STATUS = 'APPROVED' ORDER BY START_DATE ASC LIMIT 1;",
            nativeQuery = true)
    Booking getNextBookigByItemIdStatusApproved(long itemId, LocalDateTime nowTime);

    @Query(value = "SELECT * FROM BOOKINGS b WHERE BOOKER_ID = ?1 " +
            "AND ITEM_ID = ?2 " +
            "AND STATUS = 'APPROVED' " +
            "AND END_DATE < LOCALTIMESTAMP() LIMIT 1;",
            nativeQuery = true)
    Booking getListBokingsByBookerIdAndByItemIdAndLessEndTime(long userId, long itemId);

    List<Booking> findAllByBookerIdAndStartBeforeAndEndAfterOrderById(long userId, LocalDateTime localDateTimeNow1, LocalDateTime localDateTimeNow2, PageRequest pageRequest);

    List<Booking> findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderById(long ownerId, LocalDateTime localDateTimeNow1, LocalDateTime localDateTimeNow2, PageRequest pageRequest);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(long ownerId, LocalDateTime localDateTimeNow, PageRequest pageRequest);

    List<Booking> findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(long userId, LocalDateTime localDateTimeNow, PageRequest pageRequest);

}

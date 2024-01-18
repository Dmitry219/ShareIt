package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Booking findFirstByItemId(long itemId);

    @Query(value = "SELECT * FROM BOOKINGS b WHERE BOOKER_ID = ?1 ORDER BY ID DESC ;", nativeQuery = true)
    List<Booking> getListBookingsByBookerID(long userId);

    @Query(value = "SELECT * FROM BOOKINGS b WHERE ITEM_ID IN " +
                    "(SELECT i.id FROM ITEMS i WHERE OWNER_ID = ?1) " +
                    "ORDER BY START_DATE DESC ;",
            nativeQuery = true)
    List<Booking> getListBookingByOwnerID(long userId);

    @Query(value = "SELECT * FROM BOOKINGS b WHERE START_DATE > ?1" +
                    " AND END_DATE > ?1" +
                    " AND BOOKER_ID = ?2 ORDER BY ID DESC", nativeQuery = true)
    List<Booking> getListBookingFutureDateTimeByBookerId(LocalDateTime localDateTimeNow, long userId);

    @Query(value = "SELECT *" +
                    " FROM BOOKINGS b" +
                    " WHERE START_DATE > ?1" +
                    " AND ITEM_ID IN" +
                    " (SELECT ID" +
                    " FROM ITEMS i" +
                    " WHERE OWNER_ID = ?2)" +
                    " ORDER BY START_DATE DESC ;",
            nativeQuery = true)
    List<Booking> getListBookingFutureDateTimeByOwnerId(LocalDateTime localDateTimeNow, long ownerId);

    @Query(value = "SELECT * FROM BOOKINGS b" +
            " WHERE b.STATUS = ?1 AND b.BOOKER_ID = ?2 ;",
            nativeQuery = true)
    List<Booking> getListBookingsByWaityng(String state, long userID);

    @Query(value = "SELECT * " +
            "FROM BOOKINGS b " +
            "WHERE STATUS = ?1 " +
            "AND ITEM_ID IN " +
            "(SELECT ID FROM ITEMS i WHERE OWNER_ID = ?2) " +
            "ORDER BY START_DATE DESC ;",
            nativeQuery = true)
    List<Booking> getListBookingWaityngByOwnerId(String state, long ownerId);

    @Query(value = "SELECT * FROM BOOKINGS b" +
            " WHERE b.STATUS = ?1 AND b.BOOKER_ID = ?2 ;",
            nativeQuery = true)
    List<Booking> getListBookingsByRejected(String state, long userID);

    @Query(value = "SELECT * " +
            "FROM BOOKINGS b " +
            "WHERE STATUS = ?1 " +
            "AND ITEM_ID IN " +
            "(SELECT ID FROM ITEMS i WHERE OWNER_ID = ?2) " +
            "ORDER BY START_DATE DESC ;",
            nativeQuery = true)
    List<Booking> getListBookingRejectedByOwnerId(String state, long ownerId);

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

//    @Query(value = "SELECT * FROM BOOKINGS b  WHERE BOOKER_ID = ?1 AND ITEM_ID = ?2 AND END_DATE < LOCALTIMESTAMP();",
//            nativeQuery = true)
//    List<Booking> getListBokingsByBookerIdAndByItemIdAndLessEndTime (long userId, long itemId);

    @Query(value = "SELECT * FROM BOOKINGS b WHERE START_DATE < ?1" +
            " AND END_DATE > ?1" +
            " AND BOOKER_ID = ?2 ORDER BY ID ;", nativeQuery = true)
    List<Booking> getListBookingCurrentDateTimeByBookerId(LocalDateTime localDateTimeNow, long userId);

    @Query(value = "SELECT * FROM BOOKINGS b " +
            "WHERE START_DATE < ?1 " +
            "AND END_DATE > ?1 " +
            "AND ITEM_ID IN " +
            "(SELECT ID FROM ITEMS i WHERE OWNER_ID = ?2) " +
            "ORDER BY START_DATE DESC ;",
            nativeQuery = true)
    List<Booking> getListBookingCurrentDateTimeByOwnerId(LocalDateTime localDateTimeNow, long ownerId);

    @Query(value = "SELECT * FROM BOOKINGS b " +
            "WHERE END_DATE < ?1 " +
            "AND STATUS = 'APPROVED' " +
            "AND BOOKER_ID = ?2 ORDER BY ID DESC;\n",
            nativeQuery = true)
    List<Booking> getListBookingPastDateTimeByBookerId(LocalDateTime localDateTimeNow, long userId);

    @Query(value = "SELECT * FROM BOOKINGS b" +
            " WHERE END_DATE < LOCALTIMESTAMP()" +
            " AND STATUS = 'APPROVED'" +
            " AND ITEM_ID IN (SELECT ID FROM ITEMS i WHERE OWNER_ID = 4)" +
            " ORDER BY START_DATE DESC ;",
            nativeQuery = true)
    List<Booking> getListBookingPastDateTimeByOwnerId(LocalDateTime localDateTimeNow, long ownerId);

}

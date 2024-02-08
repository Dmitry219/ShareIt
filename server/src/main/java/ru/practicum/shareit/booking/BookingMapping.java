package ru.practicum.shareit.booking;


import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.item.ItemMapping;
import ru.practicum.shareit.user.UserMapping;

@Slf4j
public class BookingMapping implements BookingMapper {

    //метод конвертации bookingDtoRequest в Booking при создании и отправке в бд
    public static Booking toBookingCreate(BookingDtoRequest bookingDtoRequest, long userId) {
        return Booking.builder()
                .start(bookingDtoRequest.getStart())
                .end(bookingDtoRequest.getEnd())
                .status(String.valueOf(StatusType.WAITING))
                .build();
    }

    //метод конвертации Booking в BookingDtoResponse при создании и отправки на фронт
    public static BookingDtoResponse toBookingResponseGetItemName(Booking booking) {
        log.info("Проерка маппера метода toBookingResponseGetItemName booking {}", booking);
        return BookingDtoResponse.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapping.mapToItemDto(booking.getItem()))
                .booker(UserMapping.mapToUserDto(booking.getBooker()))
                .status(StatusType.valueOf(booking.getStatus()))
                .build();
    }

    public static BookingDtoShort toBookingDtoShort(Booking booking) {
        log.info("Проерка маппера метода toBookingResponseGetItemName booking {}", booking);
        return BookingDtoShort.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .bookerId(booking.getBooker().getId())
                .build();
    }

}

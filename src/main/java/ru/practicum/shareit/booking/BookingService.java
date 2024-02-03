package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import java.util.List;

public interface BookingService {
    BookingDtoResponse createBooking(BookingDtoRequest bookingDtoRequest, long userId);

    BookingDtoResponse updateBooking(long bookingId, Boolean approved, long userId);

    BookingDtoResponse getBookingByIdByUserId(long bookingId, long userId);

    List<BookingDtoResponse> getAllBookingsBookerByIdAndStatesBy(String state,long userId,int from, int size);

    List<BookingDtoResponse> getAllBookingsOwnerById(String state, long ownerId,int from, int size);
}

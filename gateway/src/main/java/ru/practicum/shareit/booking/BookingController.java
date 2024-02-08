package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.ValidationUserException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private static final String X_SHARER_USER_ID = "X-Sharer-User-Id";
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestBody @Valid BookingDtoRequest bookingDtoRequest,
                                                @RequestHeader(value = X_SHARER_USER_ID) @Positive(message = "Id не может быть <= 0")
                                                @NotNull(message = "Id не может быть null") Long userId) {
        checkTimeEndAndStart(bookingDtoRequest);
        log.info("Проверка контроллера метода createBooking bookingDto {}", bookingDtoRequest);
        log.info("Проверка контроллера метода createBooking userId {}", userId);
        return bookingClient.bookItem(userId, bookingDtoRequest);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(@PathVariable
                                                    @Positive(message = "Id не может быть <= 0")
                                                    @NotNull(message = "Id не может быть null") Long bookingId,
                                                @RequestParam Boolean approved,
                                                @RequestHeader(value = X_SHARER_USER_ID)
                                                    @Positive(message = "Id не может быть <= 0")
                                                    @NotNull(message = "Id не может быть null") Long userId) {
        log.info("Проверка контроллер метод updateBooking bookingId {}", bookingId);
        log.info("Проверка контроллер метод updateBooking approved {}", approved);
        log.info("Проверка контроллер метод updateBooking X-Sharer-User-Id {}", userId);

        return bookingClient.updateBookItem(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingByIdByUserId(@PathVariable
                                                             @Positive(message = "Id не может быть <= 0")
                                                             @NotNull(message = "Id не может быть null") Long bookingId,
                                                         @RequestHeader(X_SHARER_USER_ID)
                                                             @Positive(message = "Id не может быть <= 0")
                                                             @NotNull(message = "Id не может быть null") Long userId) {
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingsBookerByIdAndStatesBy(@RequestParam(defaultValue = "ALL") String state,
                                                                      @RequestHeader(X_SHARER_USER_ID)
                                                                          @Positive(message = "Id не может быть <= 0")
                                                                          @NotNull(message = "Id не может быть null") Long userId,
                                                                      @RequestParam(value = "from", defaultValue = "0")
                                                                          @PositiveOrZero(message = "From не может быть < 0") Integer from,
                                                                      @RequestParam(value = "size", defaultValue = "10")
                                                                          @Positive(message = "Size не может быть <= 0") Integer size) {
        log.info("Проверка контроллер метод getAllBookingsBookerByIdAndStatesBy - status {}", state);
        log.info("Проверка контроллер метод getAllBookingsBookerByIdAndStatesBy - userId {}", userId);
        BookingState stateParam = BookingState.from(String.valueOf(state))
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
        return bookingClient.getBookingsUser(userId, stateParam, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsOwnerById(@RequestParam(defaultValue = "ALL") String state,
                                                          @RequestHeader(X_SHARER_USER_ID)
                                                               @Positive(message = "Id не может быть <= 0")
                                                               @NotNull(message = "Id не может быть null") Long userId,
                                                          @RequestParam(value = "from", defaultValue = "0")
                                                              @PositiveOrZero(message = "From не может быть < 0") Integer from,
                                                          @RequestParam(value = "size", defaultValue = "10")
                                                              @Positive(message = "Size не может быть <= 0") Integer size) {
        BookingState stateParam = BookingState.from(String.valueOf(state))
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + state));
        return bookingClient.getBookingsOwner(userId, stateParam, from, size);
    }

        private void checkTimeEndAndStart(BookingDtoRequest bookingDtoRequest) {
        log.info("Проверка сервис метод checkTimeEnd проверка TIME");

        if (bookingDtoRequest.getEnd().isBefore(bookingDtoRequest.getStart())) {
            throw new ValidationUserException("Вермя End предшествует Start!");
        } else if (bookingDtoRequest.getEnd().equals(bookingDtoRequest.getStart())) {
            throw new ValidationUserException("Вермя End равно Start!");
        } else if (bookingDtoRequest.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationUserException("Вермя Start не может быть в прошлом!");
        }
    }
}

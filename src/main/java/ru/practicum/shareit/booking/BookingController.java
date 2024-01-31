package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDtoResponse createBooking(@Valid @RequestBody BookingDtoRequest bookingDtoRequest,
                                            @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        log.info("Проверка контроллера метода createBooking bookingDto {}", bookingDtoRequest);
        log.info("Проверка контроллера метода createBooking userId {}", userId);
        return bookingService.createBooking(bookingDtoRequest, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse updateBooking(@PathVariable long bookingId,
                                 @RequestParam Boolean approved,
                                 @RequestHeader(value = "X-Sharer-User-Id") long userId) {
        log.info("Проверка контроллер метод updateBooking bookingId {}", bookingId);
        log.info("Проверка контроллер метод updateBooking approved {}", approved);
        log.info("Проверка контроллер метод updateBooking X-Sharer-User-Id {}", userId);

        return bookingService.updateBooking(bookingId, approved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBookingByIdByUserId(@PathVariable long bookingId,
                                                   @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.getBookingByIdByUserId(bookingId,userId);
    }

    @GetMapping
    public List<BookingDtoResponse> getAllBookingsBookerByIdAndStatesBy(@RequestParam (defaultValue = "ALL") String state,
                                                                        @RequestHeader("X-Sharer-User-Id") long userId,
                                                                        @RequestParam(value = "from", defaultValue = "0") @Positive int from,
                                                                        @RequestParam(value = "size", defaultValue = "10") @Positive int size) {
        log.info("Проверка контроллер метод getAllBookingsBookerByIdAndStatesBy - status {}", state);
        log.info("Проверка контроллер метод getAllBookingsBookerByIdAndStatesBy - userId {}", userId);
        return bookingService.getAllBookingsBookerByIdAndStatesBy(state,userId,from,size);
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getAllBookingsOwnerById(@RequestParam (defaultValue = "ALL") String state,
                                                            @RequestHeader("X-Sharer-User-Id") long userId,
                                                            @RequestParam(value = "from", defaultValue = "0") @Positive int from,
                                                            @RequestParam(value = "size", defaultValue = "10") @Positive int size) {
        return bookingService.getAllBookingsOwnerById(state,userId,from,size);
    }
}

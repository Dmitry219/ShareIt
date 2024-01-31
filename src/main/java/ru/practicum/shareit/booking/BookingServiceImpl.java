package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationUserException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              ItemRepository itemRepository, UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public BookingDtoResponse createBooking(BookingDtoRequest bookingDtoRequest, long userId) {
        log.info("Проерка сервиса метода createBooking bookingDto {}", bookingDtoRequest);
        log.info("Проерка сервиса метода createBooking userId {}", userId);

        checkIdItem(bookingDtoRequest);
        checkIdUser(userId);
        checkIdOwnerByUserId(bookingDtoRequest, userId);

        checkItemIdForAvailability(bookingDtoRequest);
        checkTimeEndAndStart(bookingDtoRequest);
        Booking booking = BookingMapping.toBookingCreate(bookingDtoRequest, userId);
        booking.setItem(itemRepository.findById(bookingDtoRequest.getItemId()).get());
        booking.setBooker(userRepository.findById(userId).get());

        return BookingMapping.toBookingResponseGetItemName(bookingRepository.save(booking));
    }

    @Transactional
    @Override
    public BookingDtoResponse updateBooking(long bookingId, Boolean approved, long userId) {
        log.info("Проерка сервиса метода updateBooking bookingId {}", bookingId);
        log.info("Проерка сервиса метода updateBooking approved {}", approved);
        log.info("Проерка сервиса метода updateBooking userId {}", userId);

        checkIdBooking(bookingId);

        checkIdOwner(bookingId, userId);
        Booking booking = bookingRepository.getBookingById(bookingId);
        log.info("Проерка сервиса метода updateBooking booking {}", booking);

        if (!booking.getStatus().equals(String.valueOf(StatusType.APPROVED))) {
            if (approved.equals(true)) {
                booking.setStatus(String.valueOf(StatusType.APPROVED));
            } else if (approved.equals(false)) {
                booking.setStatus(String.valueOf(StatusType.REJECTED));
            } else if (approved.equals(null)) {
                throw new NotFoundException("Отсутствует одобрение!");
            }
        } else {
            throw new ValidationUserException("Уже одобренно!");
        }

        log.info("Проерка сервиса метода updateBooking NEWbooking {}", booking);

        return BookingMapping.toBookingResponseGetItemName(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoResponse getBookingByIdByUserId(long bookingId, long userId) {
        checkIdBooking(bookingId);
        Booking booking = bookingRepository.findById(bookingId).get();
        if (booking.getBooker().getId() != (userId)) {
            if (booking.getItem().getOwner().getId() != (userId)) {
                throw new NotFoundException("Несоответсвие id ни владельца ни бронировщик");
            }
        }
        return BookingMapping.toBookingResponseGetItemName(booking);
    }

    @Override
    public List<BookingDtoResponse> getAllBookingsBookerByIdAndStatesBy(String state, long userId, int from, int size) {
        checkIdUser(userId);
        PageRequest pageRequest = PageRequest.of(from, size);
        LocalDateTime localDateTimeNow = LocalDateTime.now();
        List<BookingDtoResponse> bookings = new ArrayList<>();
        log.info("Проверка сервиса метода getAllBookingsBookerByIdAndStatesBy status {}", state);

        if (state.equals("ALL")) { //все
            bookings = bookingRepository.findAllByBookerIdOrderByIdDesc(userId,PageRequest.of(from/size, size)).stream()
                    .map(BookingMapping::toBookingResponseGetItemName)
                    .collect(Collectors.toList());
        } else if (state.equals("CURRENT")) { //текущий
            bookings = bookingRepository.findAllByBookerIdAndStartBeforeAndEndAfterOrderById(userId, localDateTimeNow, localDateTimeNow, pageRequest).stream()
                    .map(BookingMapping::toBookingResponseGetItemName)
                    .collect(Collectors.toList());
        } else if (state.equals("PAST")) { //прошлое
            bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, localDateTimeNow, pageRequest).stream()
                    .map(BookingMapping::toBookingResponseGetItemName)
                    .collect(Collectors.toList());
        } else if (state.equals("FUTURE")) { //будущее
            bookings = bookingRepository.findAllByBookerIdAndStartAfterAndEndAfterOrderByIdDesc(userId, localDateTimeNow, localDateTimeNow, pageRequest).stream()
                    .map(BookingMapping::toBookingResponseGetItemName)
                    .collect(Collectors.toList());
        } else if (state.equals("WAITING")) { //ожидающий WAITING
            bookings = bookingRepository.findAllByBookerIdAndStatus(userId, state, pageRequest).stream()
                    .map(BookingMapping::toBookingResponseGetItemName)
                    .collect(Collectors.toList());
        } else if (state.equals("REJECTED")) { //отклонённый REJECTED
            bookings = bookingRepository.findAllByBookerIdAndStatus(userId, state, pageRequest).stream()
                    .map(BookingMapping::toBookingResponseGetItemName)
                    .collect(Collectors.toList());
        } else { //UNSUPPORTED_STATUS
            throw new IllegalArgumentException(String.format("Unknown state: %s", state));
        }
        return bookings;
    }

    @Override
    public List<BookingDtoResponse> getAllBookingsOwnerById(String state, long ownerId,int from, int size) {
        checkIdUser(ownerId);
        LocalDateTime localDateTimeNow = LocalDateTime.now();
        List<BookingDtoResponse> bookings = new ArrayList<>();
        PageRequest pageRequest = PageRequest.of(from, size);
        log.info("Проерка сервиса метода getAllBookingsOwnerById status {}", state);
        log.info("Проерка сервиса метода getAllBookingsOwnerById ownerId {}", ownerId);

        if (state.equals("ALL")) { //все
            bookings = bookingRepository.findAllByItemOwnerIdOrderByIdDesc(ownerId,PageRequest.of(from/size, size)).stream()
                    .map(BookingMapping::toBookingResponseGetItemName)
                    .collect(Collectors.toList());
        } else if (state.equals("CURRENT")) { //текущий
            bookings = bookingRepository.findAllByItemOwnerIdAndStartBeforeAndEndAfterOrderById(ownerId,localDateTimeNow,localDateTimeNow,pageRequest).stream()
                    .map(BookingMapping::toBookingResponseGetItemName)
                    .collect(Collectors.toList());
        } else if (state.equals("PAST")) { //прошлое
            bookings = bookingRepository.findAllByItemOwnerIdAndEndBeforeOrderByStartDesc(ownerId,localDateTimeNow,pageRequest).stream()
                    .map(BookingMapping::toBookingResponseGetItemName)
                    .collect(Collectors.toList());
        } else if (state.equals("FUTURE")) { //будущее
            bookings = bookingRepository.findAllByItemOwnerIdAndStartAfterAndEndAfterOrderByIdDesc(ownerId,localDateTimeNow,localDateTimeNow,pageRequest).stream()
                    .map(BookingMapping::toBookingResponseGetItemName)
                    .collect(Collectors.toList());
        } else if (state.equals("WAITING")) { //ожидающий WAITING
            bookings = bookingRepository.findAllByItemOwnerIdAndStatus(ownerId,state,pageRequest).stream()
                    .map(BookingMapping::toBookingResponseGetItemName)
                    .collect(Collectors.toList());
        } else if (state.equals("REJECTED")) { //отклонённый REJECTED
            bookings = bookingRepository.findAllByItemOwnerIdAndStatus(ownerId,state,pageRequest).stream()
                    .map(BookingMapping::toBookingResponseGetItemName)
                    .collect(Collectors.toList());
        } else { //UNSUPPORTED_STATUS
            throw new IllegalArgumentException(String.format("Unknown state: %s", state));
        }
        return bookings;
    }



    // Методы проверки наличия объектов по индификатору
    private void checkIdBooking(long bokingId) {
        log.info("Проверка сервис метод checkIdBooking проверка ID BOOKING");
        if (bokingId <= 0) {
            log.info("Проверка сервис метод checkIdBooking, проверка ОТРИЦАТЕЛЬНГО id бронировщика");
            throw new ValidationUserException(
                    String.format("Booing с такми id = %s не может быть меньше нуля или равен нулю!",
                            bokingId));
        } else if (!bookingRepository.existsById(bokingId)) {
            log.info("Проверка сервис метод checkIdBooking, проверка СУЩЕСТВОВАНИЯ бронировщика");
            throw new NotFoundException(String.format("Booking с таким id = %s не существует!",
                    bokingId));
        }
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

    private void checkIdItem(BookingDtoRequest bookingDtoRequest) {
        log.info("Проверка сервис метод checkIdItem проверка ID ITEM");
        if (bookingDtoRequest.getItemId() <= 0) {
            log.info("Проверка сервис метод checkIdItem, проверка ОТРИЦАТЕЛЬНГО id вещи");
            throw new ValidationUserException(
                    String.format("Item с такми id = %s не может быть меньше нуля или равен нулю!",
                            bookingDtoRequest.getItemId()));
        } else if (!itemRepository.existsById(bookingDtoRequest.getItemId())) {
            log.info("Проверка сервис метод checkIdItem, проверка СУЩЕСТВОВАНИЯ вещи");
            throw new NotFoundException(String.format("Item с таким id = %s не существует!",
                    bookingDtoRequest.getItemId()));
        }
    }

    private void checkItemIdForAvailability(BookingDtoRequest bookingDtoRequest) {
        log.info("Проверка сервис метод checkItemIdForAvailability ДОСТУПНОСТИ");

        if (!itemRepository.getById(bookingDtoRequest.getItemId()).isAvailable()) {
            log.info("Проверка сервис метод checkItemIdForAvailability проверка ДОСТУПНОСТИ вещи");
            throw new ValidationUserException("Вещь не доступна!");
        }
    }

    private void checkIdUser(long userId) {
        log.info("Проверка сервис метод checkIdUser проверка ID USER");
        if (userId <= 0) {
            throw new ValidationUserException("userId не может быть меньше нуля или равен нулю!");
        } else if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format("User с таким id = %s  не существует!", userId));
        }
    }

    private void checkIdOwner(long bookingId, long userId) {
        log.info("Проверка сервис метод checkIdOwner проверка что ID USER не является владельцем");
        if (bookingRepository.findById(bookingId).get().getBooker().getId().equals(userId)) {
            throw new NotFoundException("userId владелец не может бронировать свою вещь!");
        }
    }

    private void checkIdOwnerByUserId(BookingDtoRequest bookingDtoRequest, long userId) {
        log.info("Проверка сервис метод checkIdOwner проверка что ID USER не является владельцем");

        if (itemRepository.getItemById(bookingDtoRequest.getItemId()).getOwner().getId().equals(userId)) {
            throw new NotFoundException("userId владелец не может бронировать свою вещь!");
        }
    }
}

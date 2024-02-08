package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapping;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.comment.CommentMapping;
import ru.practicum.shareit.exception.AuthorizationFailureException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationUserException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(readOnly = true)
public class ItemSrviceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Autowired
    public ItemSrviceImpl(UserRepository userRepository, ItemRepository itemRepository, BookingRepository bookingRepository, CommentRepository commentRepository, ItemRequestRepository itemRequestRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.itemRequestRepository = itemRequestRepository;
    }

    @Transactional
    @Override
    public ItemDto createItem(ItemDto itemDto, long userId) {
        log.info("Проверка сервис метод createItem itemDto {}", itemDto);
        log.info("Проверка сервис метод createItem item {}", userId);

        Item item = ItemMapping.mapToItem(itemDto);

        if (itemDto.getRequestId() != null) {
            item.setRequestId(itemRequestRepository.findById(itemDto.getRequestId()).get());
        }

        checkIdUser(userId);
        item.setOwner(userRepository.findById(userId).get());
        itemRepository.save(item);
        log.info("Проверка сервис метод createItem item {}", item);
        log.info("Проврка сервиса метод createItem проверяем хранилище {}", itemRepository.getById(item.getId()));
        ItemDto itemDtoNew = ItemMapping.mapToItemDto(item);
        if (item.getRequestId() != null) {
            itemDtoNew.setRequestId(item.getRequestId().getId());
        }
        return itemDtoNew;
    }

    @Transactional
    @Override
    public ItemDto updateItem(ItemDto itemDto, long itemId, long userId) {
        log.info("Проверка сервис метод updateItem itemDto {}", itemDto);
        log.info("Проверка сервис метод updateItem itemId {}", itemId);
        log.info("Проверка сервис метод updateItem userId {}", userId);
        log.info("Проверка сервис метод updateItem прорка правильной item {}", itemDto);

        checkIdItem(itemId);

        checkIdOwnerItem(itemId, userId);
        checkIdUser(userId);

        Item newItem = itemRepository.getReferenceById(itemId);
        log.info("Проверка сервис метод updateItem newItem {}", newItem);

        if (itemDto.getName() != null) {
            newItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            newItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            newItem.setAvailable(itemDto.getAvailable());
        }

        return ItemMapping.mapToItemDto(itemRepository.save(newItem));
    }

    @Override
    public ItemDto getByIdItem(long itemId, long userId) {
        checkIdItem(itemId);
        checkIdUser(userId);

        Item item = itemRepository.getItemById(itemId);

        List<CommentDto> commentDtos = new ArrayList<>();

        BookingDtoShort lastBooking = null;
        BookingDtoShort nextBooking = null;

        log.info("Проверка сервис метод getByIdItem item {}", item);

        if (item.getOwner().getId() == userId) {

            commentDtos = commentRepository.getCommentByItemIdAndByOwnerId(itemId, userId).stream()
                    .map(CommentMapping::toCommentDto)
                    .collect(Collectors.toList());

            if (bookingRepository.findFirstByItemId(itemId) != null) {
                Booking bookingLast = bookingRepository.getLastBookigByItemIdStatusApproved(
                        item.getId(), LocalDateTime.now());
                Booking bookingNext = bookingRepository.getNextBookigByItemIdStatusApproved(
                        item.getId(), LocalDateTime.now());

                if (bookingLast != null) {
                    lastBooking = BookingMapping.toBookingDtoShort(bookingLast);
                }
                if (bookingNext != null) {
                    nextBooking = BookingMapping.toBookingDtoShort(bookingNext);
                }
            }

        } else {
            commentDtos = commentRepository.getCommentByItemIdAndByBookerId(itemId, userId).stream()
                    .map(CommentMapping::toCommentDto)
                    .collect(Collectors.toList());;
        }
        return ItemMapping.mapToItemDtoBookingByIdOwner(item, lastBooking, nextBooking, commentDtos);
    }

    @Override
    public List<ItemDto> getListItemsByIdUser(long userId, int from, int size) {
        checkIdUser(userId);
        PageRequest pageRequest = PageRequest.of(from, size);
        List<Item> items = itemRepository.findAllByOwner_idOrderById(userId, pageRequest);
        List<ItemDto> newItems = new ArrayList<>();

        List<CommentDto> commentDtos;

        for (Item item : items) {
            BookingDtoShort lastBooking = null;
            BookingDtoShort nextBooking = null;
            if (item.getOwner().getId() == userId) {

                commentDtos = commentRepository.getCommentByItemIdAndByOwnerId(item.getId(), userId).stream()
                        .map(CommentMapping::toCommentDto)
                        .collect(Collectors.toList());

                if (bookingRepository.findFirstByItemId(item.getId()) != null) {
                    Booking bookingLast = bookingRepository.getLastBookigByItemIdStatusApproved(
                            item.getId(), LocalDateTime.now());
                    Booking bookingNext = bookingRepository.getNextBookigByItemIdStatusApproved(
                            item.getId(), LocalDateTime.now());
                    if (bookingLast != null) {
                        lastBooking = BookingMapping.toBookingDtoShort(bookingLast);
                    }
                    if (bookingNext != null) {
                        nextBooking = BookingMapping.toBookingDtoShort(bookingNext);
                    }
                }
            } else {
                commentDtos = commentRepository.getCommentByItemIdAndByBookerId(item.getId(), userId).stream()
                        .map(CommentMapping::toCommentDto)
                        .collect(Collectors.toList());
            }
            newItems.add(ItemMapping.mapToItemDtoBookingByIdOwner(item, lastBooking, nextBooking, commentDtos));
        }
        return newItems;
    }

    @Override
    public List<ItemDto> getListItemsByText(String text, long userId, int from, int size) {
        log.info("Проверка сервис метод getListItemsByText проверка text {} , userId {}", text, userId);
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        PageRequest pageRequest = PageRequest.of(from, size);
        return itemRepository.findAllByText(text, pageRequest).stream()
                .map(ItemMapping::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CommentDto createComment(CommentDto commentDto, long bookerId, long itemId) {
        log.info("Проверка сервис метод createComment проверка commentDto={} , bookerId={} , itemId={} - ", commentDto, bookerId, itemId);
        checkIdUser(bookerId);
        checkIdItem(itemId);

        User user = userRepository.findById(bookerId).get();
        Item item = itemRepository.findById(itemId).get();
        log.info("Проверка сервис метод createComment проверка что дошли при тестировании до bookings");
        Booking bookings = bookingRepository
                .getListBokingsByBookerIdAndByItemIdAndLessEndTime(bookerId, itemId);
        log.info("Проверка сервис метод createComment проверка bookings={}", bookings);
        log.info("Проверка сервис метод createComment проверка что прошли при тестировании до bookings");

        Comment comment = null;
        CommentDto commentDtoNew = null;

//сохранять только тогда когда если бронирование относится к этому пользователю и если бранирование завершено
            if (bookings != null) {
                if (bookings.getEnd().isBefore(LocalDateTime.now())) {
                    comment = CommentMapping.toComment(commentDto, item, user);
                    commentDtoNew = CommentMapping.toCommentDto(commentRepository.save(comment));
                }
            } else {
                throw new ValidationUserException("В базе не нашлось нужного бронирования!");
            }

        return commentDtoNew;
    }

    //методы для проверки сущностей по индификатору
    private void checkIdItem(long itemId) {
        log.info("Проверка сервис метод checkIdItem проверка itemId");
        if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException("Item с таким Id не существует!");
        }
    }

    private void checkIdUser(long userId) {
        log.info("Проверка сервис метод checkIdUser проверка userId");
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User с таким Id не существует!");
        }
    }

    private void checkIdOwnerItem(long itemId, long userId) {
        log.info("Проверка сервис метод checkIdOwnerItem проверка владельца");
        if (itemRepository.getItemById(itemId).getOwner().getId() != userId) {
            throw new AuthorizationFailureException("Вещь с таким ID имеет владельца!");
        }
    }
}

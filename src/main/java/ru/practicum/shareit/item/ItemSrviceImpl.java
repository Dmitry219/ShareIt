package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemSrviceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public ItemSrviceImpl(UserRepository userRepository, ItemRepository itemRepository, BookingRepository bookingRepository, CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, long userId) {
        log.info("Проверка сервис метод createItem itemDto {}", itemDto);
        log.info("Проверка сервис метод createItem item {}", userId);
        Item item = ItemMapping.mapToItem(itemDto);
        checkIdUser(userId);
        item.setOwner(userRepository.findById(userId).get());
        itemRepository.save(item);
        log.info("Проверка сервис метод createItem item {}", item);
        log.info("Проврка сервиса метод createItem проверяем хранилище {}", itemRepository.getById(item.getId()));
        return ItemMapping.mapToItemDto(item);
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long itemId, long userId) {
        log.info("Проверка сервис метод updateItem itemDto {}", itemDto);
        log.info("Проверка сервис метод updateItem itemId {}", itemId);
        log.info("Проверка сервис метод updateItem userId {}", userId);
        log.info("Проверка сервис метод updateItem прорка правильной item {}", itemRepository.getById(itemId));

        checkIdItem(itemId);
        log.info("Проверка сервис метод updateItem проверка правильного user {}", userRepository.getById(userId));
        checkIdOwnerItem(itemId, userId);
        checkIdUser(userId);

        Item item = ItemMapping.mapToItem(itemDto);
        log.info("Проверка сервис метод updateItem item {}", item);
        Item newItem = itemRepository.getById(itemId);
        log.info("Проверка сервис метод updateItem newItem {}", newItem);

        if (item.getName() != null) {
            newItem.setName(item.getName());
        }
        if (item.getDescription() != null) {
            newItem.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            newItem.setAvailable(item.getAvailable());
        }

        itemRepository.save(newItem);

        log.info("Проверка сервиса метод updateItem проверка правильного хранилища {}",
                itemRepository.getById(itemId));
        return ItemMapping.mapToItemDto(newItem);
    }

    @Override
    public ItemDto getByIdItem(long itemId, long userId) {
        ItemDto itemDto = null;
        checkIdItem(itemId);
        checkIdUser(userId);

        Item item = itemRepository.getItemById(itemId);

        List<Comment> commentDbs = new ArrayList<>();
        List<CommentDto> commentDtos = new ArrayList<>();

        BookingDtoShort lastBooking = null;
        BookingDtoShort nextBooking = null;

        log.info("Проверка сервис метод getByIdItem item {}", item);

        if (item.getOwner().getId() == userId) {

            commentDbs = commentRepository.getCommentByItemIdAndByOwnerId(itemId, userId);

            if (!commentDbs.isEmpty()) {
                commentDtos = commentDbs.stream()
                        .map(CommentMapping::toCommentDto)
                        .collect(Collectors.toList());
            }

            if (bookingRepository.findById(itemId).isPresent()) {
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
                itemDto = ItemMapping.mapToItemDtoBookingByIdOwner(item, lastBooking, nextBooking, commentDtos);
            } else {
                itemDto = ItemMapping.mapToItemDtoBookingByIdOwner(item, lastBooking, nextBooking, commentDtos);
            }
        } else {

            commentDbs = commentRepository.getCommentByItemIdAndByBookerId(itemId, userId);
            if (!commentDbs.isEmpty()) {
                commentDtos = commentDbs.stream()
                        .map(CommentMapping::toCommentDto)
                        .collect(Collectors.toList());
            }

            itemDto = ItemMapping.mapToItemDtoBookingByIdOwner(item, lastBooking, nextBooking, commentDtos);
        }
        return itemDto;
    }

    @Override
    public List<ItemDto> getListItemsByIdUser(long userId) {
        checkIdUser(userId);
        List<Item> items = itemRepository.findAllByOwner_id(userId);
        List<ItemDto> newItems = new ArrayList<>();

        List<Comment> commentDbs = new ArrayList<>();
        List<CommentDto> commentDtos = new ArrayList<>();

        for (Item item : items) {
            BookingDtoShort lastBooking = null;
            BookingDtoShort nextBooking = null;
            if (item.getOwner().getId() == userId) {

                commentDbs = commentRepository.getCommentByItemIdAndByOwnerId(item.getId(), userId);

                if (!commentDbs.isEmpty()) {
                    commentDtos = commentDbs.stream()
                            .map(CommentMapping::toCommentDto)
                            .collect(Collectors.toList());
                }

                if (bookingRepository.findById(item.getId()).isPresent()) {
                    Booking bookingLast = bookingRepository.getLastBookigByItemIdStatusApproved(
                            item.getId(), LocalDateTime.now());
                    Booking bookingNext = bookingRepository.getNextBookigByItemIdStatusApproved(
                            item.getId(), LocalDateTime.now());
                    if (bookingLast != null && bookingNext != null) {
                        lastBooking = BookingMapping.toBookingDtoShort(bookingLast);
                        nextBooking = BookingMapping.toBookingDtoShort(bookingNext);
                        newItems.add(ItemMapping.mapToItemDtoBookingByIdOwner(item, lastBooking, nextBooking, commentDtos));
                    } else {
                        newItems.add(ItemMapping.mapToItemDtoBookingByIdOwner(item, lastBooking, nextBooking, commentDtos));
                    }
                } else {
                    newItems.add(ItemMapping.mapToItemDtoBookingByIdOwner(item, lastBooking, nextBooking, commentDtos));
                }
            } else {

                commentDbs = commentRepository.getCommentByItemIdAndByBookerId(item.getId(), userId);
                if (!commentDbs.isEmpty()) {
                    commentDtos = commentDbs.stream()
                            .map(CommentMapping::toCommentDto)
                            .collect(Collectors.toList());
                }

                newItems.add(ItemMapping.mapToItemDtoBookingByIdOwner(item, lastBooking, nextBooking, commentDtos));
            }
        }
        return newItems;
    }

    @Override
    public List<ItemDto> getListItemsByText(String text, long userId) {
        log.info("Проверка сервис метод getListItemsByText проверка text {} , userId {}", text, userId);
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.findAll().stream()
                .filter(item -> item.getDescription().toLowerCase().contains(text.toLowerCase()) &&
                        item.getAvailable().equals(true))
                .map(ItemMapping::mapToItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto createComment(CommentDto commentDto, long bookerId, long itemId) {
        log.info("Проверка сервис метод createComment проверка commentDto={} , bookerId={} , itemId={} - ", commentDto, bookerId, itemId);
        checkIdUser(bookerId);
        checkIdItem(itemId);

        User user = userRepository.findById(bookerId).get();
        Item item = itemRepository.findById(itemId).get();
        Booking bookings = bookingRepository
                .getListBokingsByBookerIdAndByItemIdAndLessEndTime(bookerId, itemId);

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
        if (itemId <= 0) {
            throw new ValidationUserException("ItemId не может быть меньше нуля или равен нулю!");
        } else if (!itemRepository.existsById(itemId)) {
            throw new NotFoundException("Item с таким Id не существует!");
        }
    }

    private void checkIdUser(long userId) {
        log.info("Проверка сервис метод checkIdUser проверка userId");
        if (userId <= 0) {
            throw new ValidationUserException("userId не может быть меньше нуля или равен нулю!");
        } else if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User с таким Id не существует!");
        }
    }

    private void checkIdOwnerItem(long itemId, long userId) {
        log.info("Проверка сервис метод checkIdOwnerItem проверка владельца");
        if (itemRepository.getById(itemId).getOwner().getId() != userId) {
            throw new AuthorizationFailureException("Вещь с таким ID имеет владельца!");
        }
    }
}

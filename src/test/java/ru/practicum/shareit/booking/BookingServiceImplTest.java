package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationUserException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {"db.name=test"})
class BookingServiceImplTest {
    @Mock
    BookingRepository bookingRepository;
    @Mock
    UserService userService;
    @Mock
    ItemService itemService;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @InjectMocks
    BookingServiceImpl bookingService;

    private UserDto ownerDto;
    private UserDto userDto;
    private User owner;
    private User user;
    private Item item;
    private Item itemTwo;
    private ItemRequestDto itemRequestDto;
    private ItemRequest itemRequest;
    private ItemDto itemDto;
    private BookingDtoShort bookingDtoShort;
    private CommentDto commentDto;
    private Booking booking;
    private Comment comment;
    private BookingDtoRequest bookingDtoRequest;
    private BookingDtoResponse bookingDtoResponse;

    @BeforeEach
    public void setUp(){

        ownerDto = UserDto.builder()
                .id(1L)
                .name("owner")
                .email("owner@owner.ru")
                .build();

        userDto = UserDto.builder()
                .id(2L)
                .name("user")
                .email("user@user.ru")
                .build();

        itemDto = ItemDto.builder()
                .id(1L)
                .name("item")
                .description("item des")
                .available(true)
                .comments(new ArrayList<>())
                .requestId(1L)
                .build();

        owner = User.builder()
                .id(1L)
                .name("owner")
                .email("owner@owner.ru")
                .build();

        user = User.builder()
                .id(2L)
                .name("user")
                .email("user@user.ru")
                .build();



        item = Item.builder()
                .id(1L)
                .name("item")
                .description("item des")
                .available(true)
                .owner(owner)
                .requestId(itemRequest)
                .build();

        itemTwo = Item.builder()
                .id(2L)
                .name("itemTwo")
                .description("itemTwo des")
                .available(true)
                .owner(owner)
                .requestId(itemRequest)
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("item req")
                .created(LocalDateTime.now())
                .build();

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("item req")
                .requestor(user)
                .created(LocalDateTime.now())
                .build();

        bookingDtoRequest = BookingDtoRequest.builder()
                .itemId(1L)
                .start(LocalDateTime.now().plusMinutes(1L))
                .end(LocalDateTime.now().plusMinutes(2L))
                .build();

        bookingDtoResponse = BookingDtoResponse.builder()
                .id(1L)
                .start(LocalDateTime.now().plusMinutes(1L))
                .end(LocalDateTime.now().plusMinutes(2L))
                .item(itemDto)
                .booker(userDto)
                .status(StatusType.APPROVED)
                .build();

        bookingDtoShort = BookingDtoShort.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusMinutes(1L))
                .bookerId(2L)
                .build();

        commentDto = CommentDto.builder()
                .id(1L)
                .text("comment")
                .authorName("user")
                .created(LocalDateTime.now().plusMinutes(10))
                .build();

        booking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().minusDays(1L))
                .end(LocalDateTime.now().minusMinutes(50L))
                .item(item)
                .booker(user)
                .status("APPROVED")
                .build();

        comment = Comment.builder()
                .id(1L)
                .text("comment")
                .itemId(item)
                .authorId(user)
                .created(LocalDateTime.now().plusMinutes(10))
                .build();
    }

    @Test
    void createBookingTrue() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.getItemById(anyLong())).thenReturn(item);
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        when(itemRepository.getById(bookingDtoRequest.getItemId())).thenReturn(item);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(itemTwo));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        BookingDtoResponse createdBooking = bookingService.createBooking(bookingDtoRequest, 2L);

        assertEquals(createdBooking.getId(), bookingDtoResponse.getId());
    }

    @Test
    void createBookingItemWithThisIdDoesNotExistTrue() {
        //item.setAvailable(false);
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        NotFoundException validationUserException = assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(bookingDtoRequest, 2L));

        assertEquals("User с таким id = 2  не существует!", validationUserException.getMessage());
    }

    @Test
    void createBookingItemWithThisIdDoesNotExist() {
        item.setAvailable(false);

        NotFoundException validationUserException = assertThrows(NotFoundException.class,
                () -> bookingService.createBooking(bookingDtoRequest, 2L));

        assertEquals("Item с таким id = 1 не существует!", validationUserException.getMessage());
    }

    @Test
    void getBookingByIdByUserId() {

    }

    @Test
    void getAllBookingsBookerByIdAndStatesByTrue() {
        when(userRepository.existsById(anyLong())).thenReturn(true);

        List<BookingDtoResponse> bookings = bookingService.getAllBookingsBookerByIdAndStatesBy( "ALL",1L,1, 2);
        assertEquals(bookings.size(), 0);
    }

    @Test
    void getAllBookingsOwnerById() {
        PageRequest pageRequest = PageRequest.of(1, 2);
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(bookingRepository.findAllByItemOwnerIdOrderByIdDesc(anyLong(), any()))
                .thenReturn(List.of(booking));

        List<BookingDtoResponse> bookings = bookingService.getAllBookingsOwnerById("ALL", 1L ,1, 10);
        assertEquals(bookings.size(), 1);
        assertEquals(bookings.get(0).getId(), bookingDtoResponse.getId());
    }
}
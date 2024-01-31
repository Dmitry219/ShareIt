package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.comment.CommentMapping;
import ru.practicum.shareit.comment.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {"db.name=test"})
class ItemSrviceImplTest {
    @Mock
    ItemRepository itemRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    CommentRepository commentRepository;
    @Mock
    CommentMapping commentMapping;
    @InjectMocks
    ItemSrviceImpl itemSrvice;

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
    void createItemTrue() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        ItemDto expectedItem = itemSrvice.createItem(itemDto, 1L);
        assertEquals(expectedItem, itemDto);
    }

    @Test
    void updateItemTrue() {

        //when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.getItemById(anyLong())).thenReturn(item);
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.getReferenceById(anyLong())).thenReturn(item);
        //when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        itemDto.setName("new item name");
        itemDto.setDescription("new item description");
        itemDto.setAvailable(!item.isAvailable());
        ItemDto expectedItemDto = itemSrvice.updateItem(itemDto, 1L, 1L);

        assertEquals(expectedItemDto.getId(), itemDto.getId());
    }

    @Test
    void getByIdItemtrue() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.getItemById(anyLong())).thenReturn(item);
        ItemDto itemDtoTest = itemSrvice.getByIdItem(1L, 1L);
        assertEquals(itemDto.getId(), itemDtoTest.getId());
        assertEquals(itemDto.getName(), itemDtoTest.getName());
        assertEquals(itemDto.getDescription(), itemDtoTest.getDescription());
        assertEquals(itemDto.getAvailable(), itemDtoTest.getAvailable());
    }

    @Test
    void getListItemsByIdUserTrue() {
        when(userRepository.existsById(anyLong())).thenReturn(true);

        List<ItemDto> expectedItems = itemSrvice.getListItemsByIdUser(2L, 1, 2);
        assertEquals(0, expectedItems.size());
    }

    @Test
    void getListItemsByTextTrue() {
        List<Item> items = List.of(item, itemTwo);

        when(itemRepository.findAllByText(anyString(),any())).thenReturn(items);
        List<ItemDto> expectedItems = itemSrvice.getListItemsByText("item", 1L, 1, 2);
        assertEquals(2, expectedItems.size());
    }

    @Test
    void getListItemsByNotCorrectTextTrue() {
        List<ItemDto> expectedItems = itemSrvice.getListItemsByText("nuuulll", 1L, 1, 2);
        assertEquals(0, expectedItems.size());
    }

    @Test
    void createComment() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.getListBokingsByBookerIdAndByItemIdAndLessEndTime(anyLong(),anyLong()))
                .thenReturn(booking);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        CommentDto createdComment = itemSrvice.createComment(commentDto, 2L, 2L);
        assertEquals(commentDto.getText(), createdComment.getText());
    }
}
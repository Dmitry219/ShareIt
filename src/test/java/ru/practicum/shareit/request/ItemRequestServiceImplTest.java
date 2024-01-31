package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemReqListItemsDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = {"db.name=test"})
class ItemRequestServiceImplTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemService itemService;
    @Mock
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private UserDto ownerDto;
    private UserDto userDto;
    private User owner;
    private User user;
    private Item item;
    private ItemRequestDto itemRequestDto;
    private ItemRequest itemRequest;

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

        item = Item.builder()
                .id(1L)
                .name("item")
                .description("item des")
                .available(true)
                .owner(owner)
                .requestId(itemRequest)
                .build();
    }


    @Test
    void createItemRequest() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(owner));
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequestDto expectedItemRequestDto = itemRequestService.createItemRequest(itemRequestDto, 1L);

        assertEquals(itemRequestDto.getId(), expectedItemRequestDto.getId());
        assertEquals(itemRequestDto.getDescription(), expectedItemRequestDto.getDescription());
        assertNotNull(expectedItemRequestDto.getCreated());
    }

    @Test
    void getListItemRequestDtoByIdUser() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findAllByRequestorId(anyLong())).thenReturn(List.of(itemRequest));
        List<ItemReqListItemsDto> expectedItemRequests = itemRequestService.getListItemRequestDtoByIdUser(1L);
        assertEquals(itemRequest.getId(), expectedItemRequests.get(0).getId());
        assertEquals(itemRequest.getDescription(), expectedItemRequests.get(0).getDescription());
        assertEquals(0, expectedItemRequests.get(0).getItems().size());
    }

    @Test
    void getListAllItemRequestDtoExpectByIdUser() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findAllByRequestorIdIsNotOrderByCreatedDesc(anyLong(), any())).thenReturn(List.of(itemRequest));
        List<ItemReqListItemsDto> expectedItemRequests = itemRequestService.getListAllItemRequestDtoExpectByIdUser(1L, 1, 2);
        assertEquals(itemRequest.getId(), expectedItemRequests.get(0).getId());
        assertEquals(itemRequest.getDescription(), expectedItemRequests.get(0).getDescription());
        assertEquals(0, expectedItemRequests.get(0).getItems().size());
    }

    @Test
    void getByItemRequestId() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.existsById(anyLong())).thenReturn(true);
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        ItemReqListItemsDto expectedItemRequest = itemRequestService.getByItemRequestId(1L, 1L);
        assertEquals(itemRequest.getId(), expectedItemRequest.getId());
        assertEquals(itemRequest.getDescription(), expectedItemRequest.getDescription());
    }
}
package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;
    private Item itemOne;
    private Item itemTwo;

    @BeforeEach
    public void init() {
        User userOne = User.builder()
                .email("dmitry@dmitry.ru")
                .name("dmitry")
                .build();

        User userTwo = User.builder()
                .email("ad@ad.ru")
                .name("Petya")
                .build();

        userRepository.save(userOne);
        userRepository.save(userTwo);

        itemOne = Item.builder()
                .id(1L)
                .name("item one")
                .description("item one description")
                .owner(userOne)
                .available(true)
                .build();

        itemTwo = Item.builder()
                .id(2L)
                .name("item two name")
                .description("item two description")
                .owner(userTwo)
                .available(true)
                .build();

        itemRepository.save(itemOne);
        itemRepository.save(itemTwo);
    }


    @ParameterizedTest
    @CsvSource({
            "dima, 0"
    })
    void searchItem(String text, String size) {
        Integer countResult = Integer.valueOf(size);
        PageRequest pageRequest = PageRequest.of(1, 2);
        List<Item> itemsAll = itemRepository.findAll();
        List<Item> items = itemRepository.findAllByText(text, pageRequest);
        assertEquals(countResult, items.size());
    }

    @AfterEach
    public void deleteItems() {
        itemRepository.deleteAll();
    }
}

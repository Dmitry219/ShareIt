package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Repository
@Slf4j
public class UserRepository {
    private final Map<Long, User> storageUser = new HashMap<>();
    private long generateId = 0;

    private long getGenerateId() {
        return ++generateId;
    }

    public User createUser(User user) {
        user.setId(getGenerateId());
        storageUser.put(user.getId(), user);
        return user;
    }

    public Collection<User> getStorageUser() {
        return storageUser.values();
    }

    public User updateUser(User user) {
        log.info("Проверка репозитория метод updateUser user{}", user);
        if (!storageUser.containsKey(user.getId())) {
            return null;
        }
        storageUser.put(user.getId(), user);
        return user;
    }

    public User getByIdUser(long userId) {
        return storageUser.get(userId);
    }

    public  void deleteUserById(long userId) {
        storageUser.remove(userId);
    }
}

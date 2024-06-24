package ru.yandex.practicum.catsgram.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User newUser) {
        if (newUser.getEmail() == null || newUser.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }

        if (isDuplicatedEmail(newUser.getEmail())) {
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
        newUser.setId(getNextId());
        newUser.setRegistrationDate(Instant.now());
        users.put(newUser.getId(), newUser);
        return newUser;
    }

    @PutMapping
    public User update(@RequestBody User newUser){
        if(newUser.getId() == null){
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        User oldUser = users.get(newUser.getId());
        if (!newUser.getEmail().equals(oldUser.getEmail())){
            List<User> userList = users.values()
                    .stream()
                    .filter(user -> user.getEmail().equals(newUser.getEmail()))
                    .toList();
            if (userList.size()>1){
                throw new DuplicatedDataException("Этот имейл уже используется");
            }}
            if (newUser.getEmail() !=null){
                oldUser.setEmail(newUser.getEmail());
            }
            if (newUser.getUsername() !=null){
                oldUser.setUsername(newUser.getUsername());
            }
            if (newUser.getPassword() !=null){
                oldUser.setPassword(newUser.getPassword());
            }
            users.put(oldUser.getId(), oldUser);
            return oldUser;
        }



    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private boolean isDuplicatedEmail(String newUserEmail){
        Optional<User> maybeEmail = users.values()
                .stream()
                .filter(user -> user.getEmail().equals(newUserEmail))
                .findFirst();
       return maybeEmail.isPresent();
    }
}

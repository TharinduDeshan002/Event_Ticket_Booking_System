package com.eventticketbooking.service;

import com.eventticketbooking.file.UserFileHandler;
import com.eventticketbooking.model.User;
import com.eventticketbooking.util.IdGenerator;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class UserService {

    private final UserFileHandler userFileHandler;

    public UserService(UserFileHandler userFileHandler) {
        this.userFileHandler = userFileHandler;
    }

    public List<User> findAll() {
        return userFileHandler.readAll();
    }

    public User findById(String id) {
        return findAll().stream()
                .filter(u -> u.getId().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }

    public User findByEmail(String email) {
        return findAll().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    public User register(User user) {
        if (findByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("Email already registered");
        }
        user.setId(IdGenerator.nextId("U", findAll(), User::getId));
        if (user.getRole() == null || user.getRole().isBlank()) {
            user.setRole("USER");
        }
        user.setRegisteredDate(LocalDate.now().toString());
        List<User> users = findAll();
        users.add(user);
        userFileHandler.writeAll(users);
        return user;
    }

    public User login(String email, String password) {
        User user = findByEmail(email);
        if (user == null || !user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        return user;
    }

    public void update(User updated) {
        List<User> users = findAll();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(updated.getId())) {
                updated.setPassword(users.get(i).getPassword());
                updated.setRole(users.get(i).getRole());
                updated.setRegisteredDate(users.get(i).getRegisteredDate());
                users.set(i, updated);
                userFileHandler.writeAll(users);
                return;
            }
        }
        throw new IllegalArgumentException("User not found");
    }

    public void delete(String id) {
        List<User> users = findAll();
        if (!users.removeIf(u -> u.getId().equals(id))) {
            throw new IllegalArgumentException("User not found");
        }
        userFileHandler.writeAll(users);
    }
}

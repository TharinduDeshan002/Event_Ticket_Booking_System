package com.eventticketbooking.file;

import com.eventticketbooking.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserFileHandler {

    private final FileManager fileManager;
    private final String usersPath;

    public UserFileHandler(FileManager fileManager,
                           @Value("${app.data.users}") String usersPath) {
        this.fileManager = fileManager;
        this.usersPath = usersPath;
    }

    public List<User> readAll() {
        List<User> users = new ArrayList<>();
        for (String line : fileManager.readLines(usersPath)) {
            users.add(parseLine(line));
        }
        return users;
    }

    public void writeAll(List<User> users) {
        List<String> lines = new ArrayList<>();
        lines.add("# FORMAT: userId|fullName|email|phone|password|role|city|country|registeredDate");
        for (User user : users) {
            lines.add(formatLine(user));
        }
        fileManager.writeLines(usersPath, lines);
    }

    public User parseLine(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length < 9) {
            throw new IllegalArgumentException("Invalid user line: " + line);
        }
        User user = new User();
        user.setId(p[0].trim());
        user.setFullName(p[1].trim());
        user.setEmail(p[2].trim());
        user.setPhone(p[3].trim());
        user.setPassword(p[4].trim());
        user.setRole(p[5].trim());
        user.setCity(p[6].trim());
        user.setCountry(p[7].trim());
        user.setRegisteredDate(p[8].trim());
        return user;
    }

    public String formatLine(User user) {
        return String.join("|",
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getPassword(),
                user.getRole(),
                user.getCity(),
                user.getCountry(),
                user.getRegisteredDate()
        );
    }
}

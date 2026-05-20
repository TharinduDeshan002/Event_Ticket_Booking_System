package com.eventticketbooking.file;

import com.eventticketbooking.model.Admin;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AdminFileHandler {

    private final FileManager fileManager;
    private final String adminsPath;

    public AdminFileHandler(FileManager fileManager,
                            @Value("${app.data.admins}") String adminsPath) {
        this.fileManager = fileManager;
        this.adminsPath = adminsPath;
    }

    public List<Admin> readAll() {
        List<Admin> admins = new ArrayList<>();
        for (String line : fileManager.readLines(adminsPath)) {
            admins.add(parseLine(line));
        }
        return admins;
    }

    public Admin parseLine(String line) {
        String[] p = line.split("\\|", -1);
        if (p.length < 5) {
            throw new IllegalArgumentException("Invalid admin line: " + line);
        }
        Admin admin = new Admin();
        admin.setId(p[0].trim());
        admin.setUsername(p[1].trim());
        admin.setPassword(p[2].trim());
        admin.setFullName(p[3].trim());
        admin.setEmail(p[4].trim());
        return admin;
    }
}

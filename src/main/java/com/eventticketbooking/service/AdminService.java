package com.eventticketbooking.service;

import com.eventticketbooking.file.AdminFileHandler;
import com.eventticketbooking.model.Admin;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final AdminFileHandler adminFileHandler;

    public AdminService(AdminFileHandler adminFileHandler) {
        this.adminFileHandler = adminFileHandler;
    }

    public List<Admin> findAll() {
        return adminFileHandler.readAll();
    }

    public Admin findById(String id) {
        return findAll().stream()
                .filter(a -> a.getId().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }

    public Admin login(String username, String password) {
        Admin admin = findAll().stream()
                .filter(a -> a.getUsername().equalsIgnoreCase(username.trim()))
                .findFirst()
                .orElse(null);
        if (admin == null || !admin.getPassword().equals(password)) {
            throw new IllegalArgumentException("Invalid admin username or password");
        }
        return admin;
    }
}

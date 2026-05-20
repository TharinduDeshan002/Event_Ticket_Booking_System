package com.eventticketbooking.web;

import com.eventticketbooking.model.Admin;
import com.eventticketbooking.model.User;
import com.eventticketbooking.service.AdminService;
import com.eventticketbooking.service.UserService;
import jakarta.servlet.http.HttpSession;

public final class SessionHelper {

    private SessionHelper() {
    }

    public static User currentUser(HttpSession session, UserService userService) {
        Object id = session.getAttribute(SessionKeys.USER_ID);
        if (id == null) {
            return null;
        }
        return userService.findById(id.toString());
    }

    public static void login(HttpSession session, User user) {
        session.setAttribute(SessionKeys.USER_ID, user.getId());
    }

    public static void logout(HttpSession session) {
        session.removeAttribute(SessionKeys.USER_ID);
    }

    public static boolean isLoggedIn(HttpSession session) {
        return session.getAttribute(SessionKeys.USER_ID) != null;
    }

    public static Admin currentAdmin(HttpSession session, AdminService adminService) {
        Object id = session.getAttribute(SessionKeys.ADMIN_ID);
        if (id == null) {
            return null;
        }
        return adminService.findById(id.toString());
    }

    public static void loginAdmin(HttpSession session, Admin admin) {
        session.setAttribute(SessionKeys.ADMIN_ID, admin.getId());
    }

    public static void logoutAdmin(HttpSession session) {
        session.removeAttribute(SessionKeys.ADMIN_ID);
    }

    public static boolean isAdminLoggedIn(HttpSession session) {
        return session.getAttribute(SessionKeys.ADMIN_ID) != null;
    }
}

package com.springboot.MyTodoList.util;

import javax.servlet.http.HttpServletRequest;

public final class UserRequestContext {
    private UserRequestContext() {}

    public static String extractEmail(HttpServletRequest request) {
        String email = request.getHeader("X-User-Email");
        if (email == null || email.trim().isEmpty()) {
            email = request.getParameter("email");
        }
        return email != null ? email.trim() : null;
    }
}

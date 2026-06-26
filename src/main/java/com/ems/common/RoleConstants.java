package com.ems.common;

public class RoleConstants {
    public static final String ADMIN = "ADMIN";
    public static final String USER = "USER";

    private RoleConstants() {
    }

    public static boolean isAdmin(String role) {
        return ADMIN.equalsIgnoreCase(role);
    }
}

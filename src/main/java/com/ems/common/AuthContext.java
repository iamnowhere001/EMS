package com.ems.common;

import java.util.List;

public class AuthContext {

    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> USERNAME = new ThreadLocal<>();
    private static final ThreadLocal<String> ROLE = new ThreadLocal<>();
    private static final ThreadLocal<List<String>> PERMISSIONS = new ThreadLocal<>();
    private static final ThreadLocal<String> IP = new ThreadLocal<>();
    private static final ThreadLocal<String> USER_AGENT = new ThreadLocal<>();

    public static void set(Long userId, String username, String role) {
        set(userId, username, role, null);
    }

    public static void set(Long userId, String username, String role, List<String> permissions) {
        USER_ID.set(userId);
        USERNAME.set(username);
        ROLE.set(role);
        PERMISSIONS.set(permissions);
    }

    public static void setIp(String ip) {
        IP.set(ip);
    }

    public static void setUserAgent(String userAgent) {
        USER_AGENT.set(userAgent);
    }

    public static Long getUserId() {
        return USER_ID.get();
    }

    public static String getUsername() {
        return USERNAME.get();
    }

    public static String getRole() {
        return ROLE.get();
    }

    public static List<String> getPermissions() {
        return PERMISSIONS.get();
    }

    public static String getIp() {
        return IP.get();
    }

    public static String getUserAgent() {
        return USER_AGENT.get();
    }

    public static void clear() {
        USER_ID.remove();
        USERNAME.remove();
        ROLE.remove();
        PERMISSIONS.remove();
        IP.remove();
        USER_AGENT.remove();
    }
}

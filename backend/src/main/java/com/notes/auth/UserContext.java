package com.notes.auth;

/**
 * ThreadLocal 存储当前请求的用户信息
 */
public class UserContext {

    private static final ThreadLocal<Long> CURRENT_USER_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> CURRENT_USERNAME = new ThreadLocal<>();

    public static void setCurrentUserId(Long userId) {
        CURRENT_USER_ID.set(userId);
    }

    public static Long getCurrentUserId() {
        return CURRENT_USER_ID.get();
    }

    public static void setCurrentUsername(String username) {
        CURRENT_USERNAME.set(username);
    }

    public static String getCurrentUsername() {
        return CURRENT_USERNAME.get();
    }

    /**
     * 请求结束后清理，防止内存泄漏
     */
    public static void clear() {
        CURRENT_USER_ID.remove();
        CURRENT_USERNAME.remove();
    }
}

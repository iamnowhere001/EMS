package com.ems.service;

public interface LoginAttemptService {

    void loginFailed(String username, String ip);

    void loginSuccess(String username);

    boolean isAccountLocked(String username);

    int getFailedAttempts(String username);

    void resetAttempts(String username);
}
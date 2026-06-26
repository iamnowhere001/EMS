package com.ems.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ems.entity.User;
import com.ems.vo.UserInfoVO;

public interface UserService extends IService<User> {

    UserInfoVO login(String username, String password, String ip);

    User getByUsername(String username);

    void changePassword(Long userId, String oldPassword, String newPassword);

    IPage<User> pageQuery(Integer page, Integer size, String username, String role, Integer status);

    /**
     * 退出登录：把当前 refreshToken 加入黑名单，使其后续刷新请求失败。
     */
    void logout(Long userId, String refreshToken);
}

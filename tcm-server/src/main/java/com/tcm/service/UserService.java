package com.tcm.service;

import com.tcm.entity.User;

public interface UserService {

    User getUserByPhone(String phone);

    User getUserById(Long id);

    User register(String phone, String nickname);

    String login(String phone, String code);
}

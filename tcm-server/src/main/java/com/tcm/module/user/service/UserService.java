package com.tcm.module.user.service;

import com.tcm.module.user.dto.LoginDTO;
import com.tcm.module.user.dto.RegisterDTO;
import com.tcm.module.user.dto.UserVO;

import java.util.Map;

public interface UserService {
    Map<String, Object> login(LoginDTO dto);
    void register(RegisterDTO dto);
    UserVO getProfile(Long userId);
    void updateProfile(Long userId, UserVO vo);
    void sendSmsCode(String phone);
}

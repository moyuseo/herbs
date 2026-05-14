package com.tcm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tcm.common.BusinessException;
import com.tcm.entity.User;
import com.tcm.mapper.UserMapper;
import com.tcm.security.JwtTokenProvider;
import com.tcm.security.SmsCodeService;
import com.tcm.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final SmsCodeService smsCodeService;
    private final JwtTokenProvider jwtTokenProvider;

    public UserServiceImpl(UserMapper userMapper,
                           SmsCodeService smsCodeService,
                           JwtTokenProvider jwtTokenProvider) {
        this.userMapper = userMapper;
        this.smsCodeService = smsCodeService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public User getUserByPhone(String phone) {
        return userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getPhone, phone)
        );
    }

    @Override
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public User register(String phone, String nickname) {
        User user = new User();
        user.setPhone(phone);
        user.setNickname(nickname);
        user.setUserType(0);
        user.setMembershipLevel(0);
        user.setStatus(1);
        userMapper.insert(user);
        return user;
    }

    @Override
    public String login(String phone, String code) {
        if (!smsCodeService.verifyCode(phone, code)) {
            throw new BusinessException("验证码错误或已过期");
        }
        User user = getUserByPhone(phone);
        if (user == null) {
            user = register(phone, "用户" + phone.substring(phone.length() - 4));
        }
        return jwtTokenProvider.generateToken(user.getId());
    }
}

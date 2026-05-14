package com.tcm.module.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tcm.common.exception.BusinessException;
import com.tcm.common.result.ResultCode;
import com.tcm.common.util.JwtUtil;
import com.tcm.module.user.dto.LoginDTO;
import com.tcm.module.user.dto.RegisterDTO;
import com.tcm.module.user.dto.UserVO;
import com.tcm.module.user.entity.User;
import com.tcm.module.user.mapper.UserMapper;
import com.tcm.module.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public Map<String, Object> login(LoginDTO dto) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getPhone, dto.getPhone())
        );
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }
        user.setLastLoginAt(LocalDateTime.now());
        userMapper.updateById(user);

        String token = jwtUtil.generateToken(user.getId(), user.getPhone(), user.getRole());
        UserVO vo = toUserVO(user);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("user", vo);
        return result;
    }

    @Override
    public void register(RegisterDTO dto) {
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getPhone, dto.getPhone())
        );
        if (count > 0) {
            throw new BusinessException(ResultCode.USER_EXISTS);
        }
        String redisKey = "sms:" + dto.getPhone();
        String cachedCode = stringRedisTemplate.opsForValue().get(redisKey);
        if (cachedCode == null || !cachedCode.equals(dto.getSmsCode())) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "短信验证码错误或已过期");
        }
        User user = new User();
        user.setPhone(dto.getPhone());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setNickname(dto.getNickname());
        user.setCompany(dto.getCompany());
        user.setRole("user");
        user.setMembershipLevel(0);
        user.setStatus("active");
        userMapper.insert(user);

        stringRedisTemplate.delete(redisKey);
    }

    @Override
    public UserVO getProfile(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return toUserVO(user);
    }

    @Override
    public void updateProfile(Long userId, UserVO vo) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        user.setNickname(vo.getNickname());
        user.setAvatar(vo.getAvatar());
        user.setCompany(vo.getCompany());
        userMapper.updateById(user);
    }

    @Override
    public void sendSmsCode(String phone) {
        String code = String.valueOf(ThreadLocalRandom.current().nextInt(100000, 1000000));
        String redisKey = "sms:" + phone;
        stringRedisTemplate.opsForValue().set(redisKey, code, 5, TimeUnit.MINUTES);
    }

    private UserVO toUserVO(User user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}

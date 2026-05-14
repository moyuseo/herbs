package com.tcm.module.user.controller;

import com.tcm.common.result.Result;
import com.tcm.module.user.dto.UserVO;
import com.tcm.module.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    public Result<UserVO> getProfile(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return Result.success(userService.getProfile(userId));
    }

    @PutMapping("/profile")
    public Result<Void> updateProfile(Authentication authentication, @RequestBody UserVO vo) {
        Long userId = (Long) authentication.getPrincipal();
        userService.updateProfile(userId, vo);
        return Result.success();
    }
}

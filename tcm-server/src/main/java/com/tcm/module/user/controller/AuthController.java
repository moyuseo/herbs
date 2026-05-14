package com.tcm.module.user.controller;

import com.tcm.common.result.Result;
import com.tcm.module.user.dto.LoginDTO;
import com.tcm.module.user.dto.RegisterDTO;
import com.tcm.module.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO dto) {
        return Result.success(userService.login(dto));
    }

    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO dto) {
        userService.register(dto);
        return Result.success();
    }

    @PostMapping("/sms-code")
    public Result<Void> sendSmsCode(@RequestParam String phone) {
        userService.sendSmsCode(phone);
        return Result.success();
    }
}

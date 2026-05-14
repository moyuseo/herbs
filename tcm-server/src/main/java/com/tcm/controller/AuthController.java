package com.tcm.controller;

import com.tcm.common.Result;
import com.tcm.dto.LoginDTO;
import com.tcm.security.SmsCodeService;
import com.tcm.service.UserService;
import com.tcm.vo.LoginVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "认证")
public class AuthController {

    private final SmsCodeService smsCodeService;
    private final UserService userService;

    public AuthController(SmsCodeService smsCodeService, UserService userService) {
        this.smsCodeService = smsCodeService;
        this.userService = userService;
    }

    @PostMapping("/sms-code")
    @Operation(summary = "发送验证码")
    public Result<Void> sendSmsCode(@RequestBody LoginDTO dto) {
        smsCodeService.sendCode(dto.getPhone());
        return Result.success();
    }

    @PostMapping("/login")
    @Operation(summary = "登录")
    public Result<LoginVO> login(@RequestBody LoginDTO dto) {
        String token = userService.login(dto.getPhone(), dto.getCode());
        com.tcm.entity.User user = userService.getUserByPhone(dto.getPhone());

        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setUserId(user.getId());
        vo.setPhone(user.getPhone());
        vo.setNickname(user.getNickname());
        return Result.success(vo);
    }
}

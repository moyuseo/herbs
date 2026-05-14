package com.tcm.common.result;

import lombok.Getter;

@Getter
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    PARAM_ERROR(400, "参数错误"),
    USER_EXISTS(1001, "用户已存在"),
    USER_NOT_FOUND(1002, "用户不存在"),
    PASSWORD_ERROR(1003, "密码错误"),
    TOKEN_EXPIRED(1004, "Token已过期");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}

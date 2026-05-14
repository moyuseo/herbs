package com.tcm.module.user.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserVO {
    private Long id;
    private String phone;
    private String nickname;
    private String avatar;
    private String company;
    private String role;
    private Integer membershipLevel;
    private LocalDateTime membershipExpireAt;
}

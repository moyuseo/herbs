package com.tcm.module.market.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("market")
public class Market {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String province;
    private String city;
    private String address;
    private LocalDateTime createdAt;
}

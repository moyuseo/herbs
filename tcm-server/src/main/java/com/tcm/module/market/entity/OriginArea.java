package com.tcm.module.market.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("origin_area")
public class OriginArea {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String province;
    private String city;
    private String county;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Integer herbCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

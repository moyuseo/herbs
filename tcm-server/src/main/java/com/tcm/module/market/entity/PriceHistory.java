package com.tcm.module.market.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("price_history")
public class PriceHistory {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long herbSpecId;
    private Long marketId;
    private Long areaId;
    private String priceType;
    private BigDecimal price;
    private LocalDate recordDate;
    private LocalDateTime createdAt;
}

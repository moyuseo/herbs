package com.tcm.module.market.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("market_price")
public class MarketPrice {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long herbSpecId;
    private Long marketId;
    private BigDecimal price;
    private BigDecimal changeAmount;
    private BigDecimal changePercent;
    private String trend;
    private LocalDate priceDate;
    private String source;
    private LocalDateTime createdAt;
}

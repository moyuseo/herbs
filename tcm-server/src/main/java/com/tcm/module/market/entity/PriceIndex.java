package com.tcm.module.market.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("price_index")
public class PriceIndex {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String indexType;
    private BigDecimal indexValue;
    private BigDecimal changeAmount;
    private BigDecimal changePercent;
    private LocalDate recordDate;
    private LocalDateTime createdAt;
}

package com.tcm.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@TableName("price_index")
public class PriceIndex {

    @TableId(type = IdType.AUTO)
    private Long id;

    private LocalDate indexDate;

    private String indexType;

    private BigDecimal indexValue;

    private BigDecimal changeRate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getIndexDate() {
        return indexDate;
    }

    public void setIndexDate(LocalDate indexDate) {
        this.indexDate = indexDate;
    }

    public String getIndexType() {
        return indexType;
    }

    public void setIndexType(String indexType) {
        this.indexType = indexType;
    }

    public BigDecimal getIndexValue() {
        return indexValue;
    }

    public void setIndexValue(BigDecimal indexValue) {
        this.indexValue = indexValue;
    }

    public BigDecimal getChangeRate() {
        return changeRate;
    }

    public void setChangeRate(BigDecimal changeRate) {
        this.changeRate = changeRate;
    }
}

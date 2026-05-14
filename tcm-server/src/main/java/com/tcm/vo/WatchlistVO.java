package com.tcm.vo;

import java.math.BigDecimal;

public class WatchlistVO {

    private Long id;
    private Long herbId;
    private String herbName;
    private BigDecimal currentPrice;
    private BigDecimal dayChangeRate;
    private String trend;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getHerbId() {
        return herbId;
    }

    public void setHerbId(Long herbId) {
        this.herbId = herbId;
    }

    public String getHerbName() {
        return herbName;
    }

    public void setHerbName(String herbName) {
        this.herbName = herbName;
    }

    public BigDecimal getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(BigDecimal currentPrice) {
        this.currentPrice = currentPrice;
    }

    public BigDecimal getDayChangeRate() {
        return dayChangeRate;
    }

    public void setDayChangeRate(BigDecimal dayChangeRate) {
        this.dayChangeRate = dayChangeRate;
    }

    public String getTrend() {
        return trend;
    }

    public void setTrend(String trend) {
        this.trend = trend;
    }
}

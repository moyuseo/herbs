package com.tcm.vo;

import java.math.BigDecimal;

public class PriceVO {

    private Long herbId;
    private String herbName;
    private String spec;
    private String origin;
    private String market;
    private BigDecimal price;
    private BigDecimal dayChange;
    private BigDecimal dayChangeRate;
    private BigDecimal monthChange;
    private BigDecimal monthChangeRate;
    private String trend;

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

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getDayChange() {
        return dayChange;
    }

    public void setDayChange(BigDecimal dayChange) {
        this.dayChange = dayChange;
    }

    public BigDecimal getDayChangeRate() {
        return dayChangeRate;
    }

    public void setDayChangeRate(BigDecimal dayChangeRate) {
        this.dayChangeRate = dayChangeRate;
    }

    public BigDecimal getMonthChange() {
        return monthChange;
    }

    public void setMonthChange(BigDecimal monthChange) {
        this.monthChange = monthChange;
    }

    public BigDecimal getMonthChangeRate() {
        return monthChangeRate;
    }

    public void setMonthChangeRate(BigDecimal monthChangeRate) {
        this.monthChangeRate = monthChangeRate;
    }

    public String getTrend() {
        return trend;
    }

    public void setTrend(String trend) {
        this.trend = trend;
    }
}

package com.tcm.vo;

import java.math.BigDecimal;
import java.util.List;

public class PriceDetailVO {

    private Long herbId;
    private String herbName;
    private String alias;
    private String pinyin;
    private String medicinalPart;
    private String natureFlavor;
    private String meridianTropism;
    private String efficacy;
    private List<MarketPriceItem> marketPrices;
    private BigDecimal dayChange;
    private BigDecimal dayChangeRate;
    private BigDecimal monthChange;
    private BigDecimal monthChangeRate;
    private BigDecimal yearHigh;
    private BigDecimal yearLow;

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

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getMedicinalPart() {
        return medicinalPart;
    }

    public void setMedicinalPart(String medicinalPart) {
        this.medicinalPart = medicinalPart;
    }

    public String getNatureFlavor() {
        return natureFlavor;
    }

    public void setNatureFlavor(String natureFlavor) {
        this.natureFlavor = natureFlavor;
    }

    public String getMeridianTropism() {
        return meridianTropism;
    }

    public void setMeridianTropism(String meridianTropism) {
        this.meridianTropism = meridianTropism;
    }

    public String getEfficacy() {
        return efficacy;
    }

    public void setEfficacy(String efficacy) {
        this.efficacy = efficacy;
    }

    public List<MarketPriceItem> getMarketPrices() {
        return marketPrices;
    }

    public void setMarketPrices(List<MarketPriceItem> marketPrices) {
        this.marketPrices = marketPrices;
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

    public BigDecimal getYearHigh() {
        return yearHigh;
    }

    public void setYearHigh(BigDecimal yearHigh) {
        this.yearHigh = yearHigh;
    }

    public BigDecimal getYearLow() {
        return yearLow;
    }

    public void setYearLow(BigDecimal yearLow) {
        this.yearLow = yearLow;
    }

    public static class MarketPriceItem {

        private String market;
        private String spec;
        private BigDecimal price;
        private String trend;

        public String getMarket() {
            return market;
        }

        public void setMarket(String market) {
            this.market = market;
        }

        public String getSpec() {
            return spec;
        }

        public void setSpec(String spec) {
            this.spec = spec;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public String getTrend() {
            return trend;
        }

        public void setTrend(String trend) {
            this.trend = trend;
        }
    }
}

package com.tcm.module.market.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class HerbDetailVO {

    private Long id;
    private String name;
    private String pinyin;
    private String alias;
    private String originAreas;
    private String properties;
    private String meridian;
    private String efficacy;
    private String description;
    private String imageUrl;
    private List<SpecPrice> specPrices;
    private List<OriginPriceVO> originPrices;
    private List<MarketPriceVO> marketPrices;

    @Data
    public static class SpecPrice {
        private Long specId;
        private String specName;
        private BigDecimal price;
        private BigDecimal changePercent;
        private String trend;
    }

    @Data
    public static class OriginPriceVO {
        private Long areaId;
        private String areaName;
        private BigDecimal price;
        private BigDecimal changePercent;
        private String trend;
    }

    @Data
    public static class MarketPriceVO {
        private Long marketId;
        private String marketName;
        private BigDecimal price;
        private BigDecimal changePercent;
        private String trend;
    }
}

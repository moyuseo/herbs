package com.tcm.module.market.dto;

import lombok.Data;

@Data
public class PriceQueryDTO {

    private Long herbId;
    private Long specId;
    private Long marketId;
    private Long areaId;
    private String trend;
    private String category;
    private String keyword;
    private Integer page = 1;
    private Integer size = 20;
}

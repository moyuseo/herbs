package com.tcm.controller;

import com.tcm.common.PageResult;
import com.tcm.common.Result;
import com.tcm.dto.PriceQueryDTO;
import com.tcm.service.PriceService;
import com.tcm.vo.PriceDetailVO;
import com.tcm.vo.PriceHistoryVO;
import com.tcm.vo.PriceVO;
import com.tcm.vo.RankingVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/price")
@Tag(name = "价格中心")
public class PriceController {

    private final PriceService priceService;

    public PriceController(PriceService priceService) {
        this.priceService = priceService;
    }

    @GetMapping("/market")
    @Operation(summary = "市场价格列表")
    public Result<PageResult<PriceVO>> getMarketPrices(PriceQueryDTO dto) {
        dto.setPriceType(1);
        return Result.success(priceService.getMarketPrices(dto));
    }

    @GetMapping("/origin")
    @Operation(summary = "产地价格列表")
    public Result<PageResult<PriceVO>> getOriginPrices(PriceQueryDTO dto) {
        dto.setPriceType(2);
        return Result.success(priceService.getOriginPrices(dto));
    }

    @GetMapping("/detail/{herbId}")
    @Operation(summary = "品种价格详情")
    public Result<PriceDetailVO> getPriceDetail(@PathVariable Long herbId) {
        return Result.success(priceService.getPriceDetail(herbId));
    }

    @GetMapping("/history/{herbId}")
    @Operation(summary = "历史价格走势")
    public Result<PriceHistoryVO> getPriceHistory(
            @PathVariable Long herbId,
            @RequestParam(defaultValue = "month") String period) {
        return Result.success(priceService.getPriceHistory(herbId, period));
    }

    @GetMapping("/ranking")
    @Operation(summary = "涨跌排行")
    public Result<PageResult<RankingVO>> getRanking(
            @RequestParam(defaultValue = "day") String period,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return Result.success(priceService.getRanking(period, page, pageSize));
    }

    @GetMapping("/summary")
    @Operation(summary = "今日涨跌概览")
    public Result<Map<String, Object>> getMarketSummary() {
        Map<String, Object> summary = new HashMap<>();
        summary.put("upCount", 3);
        summary.put("downCount", 1);
        summary.put("stableCount", 6);
        summary.put("topGainers", java.util.List.of(
                Map.of("herbId", 6, "herbName", "金银花", "changeRate", 3.85),
                Map.of("herbId", 3, "herbName", "当归", "changeRate", 1.85),
                Map.of("herbId", 1, "herbName", "三七", "changeRate", 1.45)
        ));
        summary.put("topLosers", java.util.List.of(
                Map.of("herbId", 2, "herbName", "白芍", "changeRate", -9.09)
        ));
        return Result.success(summary);
    }
}

package com.tcm.module.market.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tcm.common.result.Result;
import com.tcm.module.market.dto.HerbDetailVO;
import com.tcm.module.market.dto.PriceQueryDTO;
import com.tcm.module.market.entity.Herb;
import com.tcm.module.market.entity.MarketPrice;
import com.tcm.module.market.entity.OriginPrice;
import com.tcm.module.market.entity.PriceIndex;
import com.tcm.module.market.service.MarketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/market")
@RequiredArgsConstructor
public class MarketController {

    private final MarketService marketService;

    @GetMapping("/prices")
    public Result<IPage<MarketPrice>> getMarketPrices(PriceQueryDTO query) {
        return Result.success(marketService.getMarketPrices(query));
    }

    @GetMapping("/origin-prices")
    public Result<IPage<OriginPrice>> getOriginPrices(PriceQueryDTO query) {
        return Result.success(marketService.getOriginPrices(query));
    }

    @GetMapping("/herbs/{id}")
    public Result<HerbDetailVO> getHerbDetail(@PathVariable Long id) {
        return Result.success(marketService.getHerbDetail(id));
    }

    @GetMapping("/herbs/{id}/history")
    public Result<List<Map<String, Object>>> getPriceHistory(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1y") String period) {
        return Result.success(marketService.getPriceHistory(id, period));
    }

    @GetMapping("/ranking")
    public Result<List<Map<String, Object>>> getRanking(
            @RequestParam(defaultValue = "day") String period,
            @RequestParam(defaultValue = "20") Integer limit) {
        return Result.success(marketService.getRanking(period, limit));
    }

    @GetMapping("/index")
    public Result<List<PriceIndex>> getPriceIndex(
            @RequestParam(required = false) String indexType) {
        return Result.success(marketService.getPriceIndex(indexType));
    }

    @GetMapping("/herbs/search")
    public Result<IPage<Herb>> searchHerbs(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        return Result.success(marketService.searchHerbs(keyword, page, size));
    }
}

package com.tcm.controller;

import com.tcm.common.Result;
import com.tcm.service.PriceIndexService;
import com.tcm.vo.PriceIndexVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/data")
@Tag(name = "数据中心")
public class DataController {

    private final PriceIndexService priceIndexService;

    public DataController(PriceIndexService priceIndexService) {
        this.priceIndexService = priceIndexService;
    }

    @GetMapping("/index")
    @Operation(summary = "价格指数历史数据")
    public Result<List<PriceIndexVO>> getIndexHistory(
            @RequestParam(defaultValue = "composite") String indexType,
            @RequestParam(defaultValue = "month") String period) {
        return Result.success(priceIndexService.getIndexHistory(indexType, period));
    }

    @GetMapping("/index/latest")
    @Operation(summary = "最新指数")
    public Result<PriceIndexVO> getLatestIndex(
            @RequestParam(defaultValue = "composite") String indexType) {
        return Result.success(priceIndexService.getLatestIndex(indexType));
    }

    @GetMapping("/map")
    @Operation(summary = "产地地图数据")
    public Result<List<Map<String, Object>>> getMapData() {
        List<Map<String, Object>> mapData = new ArrayList<>();

        String[][] origins = {
                {"文山", "云南", "23.37", "104.24"},
                {"亳州", "安徽", "33.87", "115.78"},
                {"安国", "河北", "38.42", "115.33"},
                {"玉林", "广西", "22.63", "110.15"},
                {"廉桥", "湖南", "27.25", "111.73"},
                {"普宁", "广东", "23.30", "116.17"},
                {"岷县", "甘肃", "34.44", "104.04"},
                {"陇西", "甘肃", "35.00", "104.63"},
                {"平邑", "山东", "35.51", "117.64"},
        };

        double[] avgPrices = {130.00, 55.00, 52.00, 48.00, 42.00, 38.00, 48.00, 15.00, 120.00};
        int[] herbCounts = {12, 28, 22, 18, 15, 14, 8, 6, 10};

        for (int i = 0; i < origins.length; i++) {
            Map<String, Object> item = new HashMap<>();
            item.put("name", origins[i][0]);
            item.put("province", origins[i][1]);
            item.put("lat", Double.parseDouble(origins[i][2]));
            item.put("lng", Double.parseDouble(origins[i][3]));
            item.put("avgPrice", avgPrices[i]);
            item.put("herbCount", herbCounts[i]);
            mapData.add(item);
        }

        return Result.success(mapData);
    }
}

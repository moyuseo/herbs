package com.tcm.controller;

import com.tcm.common.PageResult;
import com.tcm.common.Result;
import com.tcm.dto.SupplyPublishDTO;
import com.tcm.dto.SupplyQueryDTO;
import com.tcm.service.SupplyService;
import com.tcm.vo.SupplyVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/supply")
@Tag(name = "供应信息")
public class SupplyController {

    private final SupplyService supplyService;

    public SupplyController(SupplyService supplyService) {
        this.supplyService = supplyService;
    }

    @GetMapping("/list")
    @Operation(summary = "供应列表")
    public Result<PageResult<SupplyVO>> getSupplyList(SupplyQueryDTO dto) {
        return Result.success(supplyService.getSupplyList(dto));
    }

    @PostMapping("/publish")
    @Operation(summary = "发布供应")
    public Result<Long> publishSupply(@RequestBody SupplyPublishDTO dto,
                                      @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId) {
        return Result.success(supplyService.publishSupply(dto, userId));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除供应")
    public Result<Void> deleteSupply(@PathVariable Long id,
                                     @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId) {
        supplyService.deleteSupply(id, userId);
        return Result.success();
    }
}

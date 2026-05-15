package com.tcm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tcm.common.PageResult;
import com.tcm.common.Result;
import com.tcm.dto.SupplyPublishDTO;
import com.tcm.dto.SupplyQueryDTO;
import com.tcm.entity.Supply;
import com.tcm.mapper.SupplyMapper;
import com.tcm.service.SupplyService;
import com.tcm.vo.SupplyVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/supply")
@Tag(name = "供应信息")
public class SupplyController {

    private final SupplyService supplyService;
    private final SupplyMapper supplyMapper;

    public SupplyController(SupplyService supplyService, SupplyMapper supplyMapper) {
        this.supplyService = supplyService;
        this.supplyMapper = supplyMapper;
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

    @PutMapping("/{id}/refresh")
    @Operation(summary = "刷新供应")
    public Result<Void> refreshSupply(@PathVariable Long id,
                                      @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId) {
        Supply supply = supplyMapper.selectById(id);
        if (supply != null) {
            supply.setCreatedAt(LocalDateTime.now());
            supplyMapper.updateById(supply);
        }
        return Result.success();
    }

    @GetMapping("/user")
    @Operation(summary = "用户的供应列表")
    public Result<List<SupplyVO>> getUserSupplyList(@RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId) {
        SupplyQueryDTO dto = new SupplyQueryDTO();
        dto.setPage(1);
        dto.setPageSize(50);
        PageResult<SupplyVO> result = supplyService.getSupplyList(dto);
        return Result.success(result.getList());
    }
}

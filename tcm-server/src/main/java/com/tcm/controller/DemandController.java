package com.tcm.controller;

import com.tcm.common.PageResult;
import com.tcm.common.Result;
import com.tcm.dto.DemandPublishDTO;
import com.tcm.dto.DemandQueryDTO;
import com.tcm.service.DemandService;
import com.tcm.vo.DemandVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/demand")
@Tag(name = "求购信息")
public class DemandController {

    private final DemandService demandService;

    public DemandController(DemandService demandService) {
        this.demandService = demandService;
    }

    @GetMapping("/list")
    @Operation(summary = "求购列表")
    public Result<PageResult<DemandVO>> getDemandList(DemandQueryDTO dto) {
        return Result.success(demandService.getDemandList(dto));
    }

    @PostMapping("/publish")
    @Operation(summary = "发布求购")
    public Result<Long> publishDemand(@RequestBody DemandPublishDTO dto,
                                      @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId) {
        return Result.success(demandService.publishDemand(dto, userId));
    }
}

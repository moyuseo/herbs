package com.tcm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tcm.common.PageResult;
import com.tcm.common.Result;
import com.tcm.entity.HerbCategory;
import com.tcm.mapper.HerbCategoryMapper;
import com.tcm.service.HerbService;
import com.tcm.vo.HerbDetailVO;
import com.tcm.vo.HerbVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/herb")
@Tag(name = "药材百科")
public class HerbController {

    private final HerbService herbService;
    private final HerbCategoryMapper herbCategoryMapper;

    public HerbController(HerbService herbService, HerbCategoryMapper herbCategoryMapper) {
        this.herbService = herbService;
        this.herbCategoryMapper = herbCategoryMapper;
    }

    @GetMapping("/list")
    @Operation(summary = "药材列表")
    public Result<PageResult<HerbVO>> getHerbList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String pinyin,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(herbService.getHerbList(keyword, categoryId, pinyin, page, pageSize));
    }

    @GetMapping("/detail/{id}")
    @Operation(summary = "药材详情")
    public Result<HerbDetailVO> getHerbDetail(@PathVariable Long id) {
        return Result.success(herbService.getHerbDetail(id));
    }

    @GetMapping("/search")
    @Operation(summary = "搜索品种")
    public Result<List<HerbVO>> searchHerb(@RequestParam String keyword) {
        return Result.success(herbService.searchHerb(keyword));
    }

    @GetMapping("/categories")
    @Operation(summary = "分类列表")
    public Result<List<HerbCategory>> getCategories() {
        LambdaQueryWrapper<HerbCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(HerbCategory::getSortOrder);
        return Result.success(herbCategoryMapper.selectList(wrapper));
    }
}

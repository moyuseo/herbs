package com.tcm.controller;

import com.tcm.common.Result;
import com.tcm.dto.NewsQueryDTO;
import com.tcm.service.NewsService;
import com.tcm.vo.NewsDetailVO;
import com.tcm.vo.NewsVO;
import com.tcm.common.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/news")
@Tag(name = "行情资讯")
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping("/list")
    @Operation(summary = "资讯列表")
    public Result<PageResult<NewsVO>> getNewsList(NewsQueryDTO dto) {
        PageResult<NewsVO> result = newsService.getNewsList(dto);
        return Result.success(result);
    }

    @GetMapping("/detail/{id}")
    @Operation(summary = "资讯详情")
    public Result<NewsDetailVO> getNewsDetail(@PathVariable Long id) {
        NewsDetailVO detail = newsService.getNewsDetail(id);
        if (detail == null) {
            return Result.error(404, "资讯不存在");
        }
        return Result.success(detail);
    }
}

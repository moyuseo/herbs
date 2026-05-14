package com.tcm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tcm.common.PageResult;
import com.tcm.dto.NewsQueryDTO;
import com.tcm.entity.News;
import com.tcm.mapper.NewsMapper;
import com.tcm.service.NewsService;
import com.tcm.vo.NewsDetailVO;
import com.tcm.vo.NewsVO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NewsServiceImpl implements NewsService {

    private final NewsMapper newsMapper;

    public NewsServiceImpl(NewsMapper newsMapper) {
        this.newsMapper = newsMapper;
    }

    @Override
    public PageResult<NewsVO> getNewsList(NewsQueryDTO dto) {
        Page<News> page = new Page<>(dto.getPage(), dto.getPageSize());

        LambdaQueryWrapper<News> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(News::getStatus, 1);

        if (StringUtils.hasText(dto.getCategory())) {
            wrapper.eq(News::getCategory, dto.getCategory());
        }

        if (StringUtils.hasText(dto.getKeyword())) {
            wrapper.like(News::getTitle, dto.getKeyword());
        }

        wrapper.orderByDesc(News::getPublishedAt);

        Page<News> result = newsMapper.selectPage(page, wrapper);

        List<NewsVO> voList = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return new PageResult<>(voList, result.getTotal(), dto.getPage(), dto.getPageSize());
    }

    @Override
    public NewsDetailVO getNewsDetail(Long id) {
        News news = newsMapper.selectById(id);
        if (news == null || news.getStatus() != 1) {
            return null;
        }

        newsMapper.update(null,
                new LambdaUpdateWrapper<News>()
                        .eq(News::getId, id)
                        .setSql("view_count = view_count + 1"));

        NewsDetailVO vo = new NewsDetailVO();
        vo.setId(news.getId());
        vo.setTitle(news.getTitle());
        vo.setContent(news.getContent());
        vo.setSummary(news.getSummary());
        vo.setCoverImage(news.getCoverImage());
        vo.setCategory(news.getCategory());
        vo.setTags(news.getTags());
        vo.setAuthor(news.getAuthor());
        vo.setSource(news.getSource());
        vo.setViewCount(news.getViewCount() + 1);
        vo.setPublishedAt(news.getPublishedAt());

        News prevNews = newsMapper.selectOne(
                new LambdaQueryWrapper<News>()
                        .eq(News::getStatus, 1)
                        .lt(News::getPublishedAt, news.getPublishedAt())
                        .orderByDesc(News::getPublishedAt)
                        .last("LIMIT 1"));
        if (prevNews != null) {
            vo.setPrevNewsId(prevNews.getId());
            vo.setPrevTitle(prevNews.getTitle());
        }

        News nextNews = newsMapper.selectOne(
                new LambdaQueryWrapper<News>()
                        .eq(News::getStatus, 1)
                        .gt(News::getPublishedAt, news.getPublishedAt())
                        .orderByAsc(News::getPublishedAt)
                        .last("LIMIT 1"));
        if (nextNews != null) {
            vo.setNextNewsId(nextNews.getId());
            vo.setNextTitle(nextNews.getTitle());
        }

        return vo;
    }

    private NewsVO convertToVO(News news) {
        NewsVO vo = new NewsVO();
        vo.setId(news.getId());
        vo.setTitle(news.getTitle());
        vo.setSummary(news.getSummary());
        vo.setCoverImage(news.getCoverImage());
        vo.setCategory(news.getCategory());
        vo.setTags(news.getTags());
        vo.setAuthor(news.getAuthor());
        vo.setSource(news.getSource());
        vo.setViewCount(news.getViewCount());
        vo.setPublishedAt(news.getPublishedAt());
        return vo;
    }
}

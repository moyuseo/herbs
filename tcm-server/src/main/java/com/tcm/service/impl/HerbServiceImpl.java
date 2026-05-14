package com.tcm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tcm.common.BusinessException;
import com.tcm.common.PageResult;
import com.tcm.entity.Herb;
import com.tcm.entity.HerbCategory;
import com.tcm.entity.Price;
import com.tcm.mapper.HerbCategoryMapper;
import com.tcm.mapper.HerbMapper;
import com.tcm.mapper.PriceMapper;
import com.tcm.service.HerbService;
import com.tcm.vo.HerbDetailVO;
import com.tcm.vo.HerbVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class HerbServiceImpl implements HerbService {

    private final HerbMapper herbMapper;
    private final HerbCategoryMapper herbCategoryMapper;
    private final PriceMapper priceMapper;

    public HerbServiceImpl(HerbMapper herbMapper, HerbCategoryMapper herbCategoryMapper, PriceMapper priceMapper) {
        this.herbMapper = herbMapper;
        this.herbCategoryMapper = herbCategoryMapper;
        this.priceMapper = priceMapper;
    }

    @Override
    public PageResult<HerbVO> getHerbList(String keyword, Long categoryId, String pinyin, Integer page, Integer pageSize) {
        Page<Herb> herbPage = new Page<>(page, pageSize);

        LambdaQueryWrapper<Herb> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Herb::getStatus, 1);
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(Herb::getName, keyword);
        }
        if (categoryId != null) {
            wrapper.eq(Herb::getCategoryId, categoryId);
        }
        if (pinyin != null && !pinyin.isEmpty()) {
            wrapper.likeRight(Herb::getPinyin, pinyin);
        }
        wrapper.orderByAsc(Herb::getPinyin);

        Page<Herb> result = herbMapper.selectPage(herbPage, wrapper);

        List<HerbVO> voList = convertToVOList(result.getRecords());

        return new PageResult<>(voList, result.getTotal(), page, pageSize);
    }

    @Override
    public HerbDetailVO getHerbDetail(Long herbId) {
        Herb herb = herbMapper.selectById(herbId);
        if (herb == null || herb.getStatus() != 1) {
            throw new BusinessException("药材不存在");
        }

        HerbDetailVO vo = new HerbDetailVO();
        vo.setId(herb.getId());
        vo.setName(herb.getName());
        vo.setAlias(herb.getAlias());
        vo.setPinyin(herb.getPinyin());
        vo.setCategoryId(herb.getCategoryId());
        vo.setMedicinalPart(herb.getMedicinalPart());
        vo.setNatureFlavor(herb.getNatureFlavor());
        vo.setMeridianTropism(herb.getMeridianTropism());
        vo.setEfficacy(herb.getEfficacy());
        vo.setIndication(herb.getIndication());
        vo.setDescription(herb.getDescription());

        if (herb.getCategoryId() != null) {
            HerbCategory category = herbCategoryMapper.selectById(herb.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getName());
            }
        }

        LambdaQueryWrapper<Price> priceWrapper = new LambdaQueryWrapper<>();
        priceWrapper.eq(Price::getHerbId, herbId);
        priceWrapper.orderByDesc(Price::getPriceDate);
        priceWrapper.last("LIMIT 1");
        Price latestPrice = priceMapper.selectOne(priceWrapper);
        if (latestPrice != null) {
            vo.setCurrentPrice(latestPrice.getPrice());
            vo.setPriceTrend(latestPrice.getTrend());
        }

        return vo;
    }

    @Override
    public List<HerbVO> searchHerb(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return new ArrayList<>();
        }

        LambdaQueryWrapper<Herb> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Herb::getStatus, 1);
        wrapper.like(Herb::getName, keyword);
        wrapper.last("LIMIT 10");

        List<Herb> herbs = herbMapper.selectList(wrapper);
        return convertToVOList(herbs);
    }

    private List<HerbVO> convertToVOList(List<Herb> herbs) {
        if (herbs == null || herbs.isEmpty()) {
            return new ArrayList<>();
        }

        Set<Long> categoryIds = herbs.stream()
                .map(Herb::getCategoryId)
                .filter(id -> id != null)
                .collect(Collectors.toSet());

        Map<Long, String> categoryNameMap = new java.util.HashMap<>();
        if (!categoryIds.isEmpty()) {
            List<HerbCategory> categories = herbCategoryMapper.selectBatchIds(categoryIds);
            categoryNameMap = categories.stream()
                    .collect(Collectors.toMap(HerbCategory::getId, HerbCategory::getName));
        }

        Map<Long, String> finalCategoryNameMap = categoryNameMap;
        return herbs.stream().map(herb -> {
            HerbVO vo = new HerbVO();
            vo.setId(herb.getId());
            vo.setName(herb.getName());
            vo.setAlias(herb.getAlias());
            vo.setPinyin(herb.getPinyin());
            vo.setCategoryId(herb.getCategoryId());
            vo.setCategoryName(herb.getCategoryId() != null ? finalCategoryNameMap.get(herb.getCategoryId()) : null);
            vo.setMedicinalPart(herb.getMedicinalPart());
            vo.setNatureFlavor(herb.getNatureFlavor());
            vo.setMeridianTropism(herb.getMeridianTropism());
            vo.setEfficacy(herb.getEfficacy());
            return vo;
        }).collect(Collectors.toList());
    }
}

package com.tcm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tcm.common.PageResult;
import com.tcm.dto.DemandPublishDTO;
import com.tcm.dto.DemandQueryDTO;
import com.tcm.entity.Demand;
import com.tcm.entity.Herb;
import com.tcm.mapper.DemandMapper;
import com.tcm.mapper.HerbMapper;
import com.tcm.service.DemandService;
import com.tcm.vo.DemandVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DemandServiceImpl implements DemandService {

    private final DemandMapper demandMapper;
    private final HerbMapper herbMapper;

    public DemandServiceImpl(DemandMapper demandMapper, HerbMapper herbMapper) {
        this.demandMapper = demandMapper;
        this.herbMapper = herbMapper;
    }

    @Override
    public PageResult<DemandVO> getDemandList(DemandQueryDTO dto) {
        Page<Demand> page = new Page<>(dto.getPage(), dto.getPageSize());

        LambdaQueryWrapper<Demand> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Demand::getStatus, 1);
        if (dto.getHerbId() != null) {
            wrapper.eq(Demand::getHerbId, dto.getHerbId());
        }
        wrapper.orderByDesc(Demand::getCreatedAt);

        Page<Demand> result = demandMapper.selectPage(page, wrapper);

        List<DemandVO> voList = convertToVOList(result.getRecords());

        return new PageResult<>(voList, result.getTotal(), dto.getPage(), dto.getPageSize());
    }

    @Override
    public Long publishDemand(DemandPublishDTO dto, Long userId) {
        Demand demand = new Demand();
        demand.setUserId(userId);
        demand.setHerbId(dto.getHerbId());
        demand.setSpec(dto.getSpec());
        demand.setQuantity(dto.getQuantity());
        demand.setUnit(dto.getUnit());
        demand.setDeliveryAddress(dto.getDeliveryAddress());
        demand.setDescription(dto.getDescription());
        demand.setQuoteCount(0);
        demand.setStatus(1);
        demand.setExpireAt(java.time.LocalDateTime.now().plusDays(30));

        demandMapper.insert(demand);
        return demand.getId();
    }

    private List<DemandVO> convertToVOList(List<Demand> demands) {
        if (demands == null || demands.isEmpty()) {
            return new ArrayList<>();
        }

        Set<Long> herbIds = demands.stream()
                .map(Demand::getHerbId)
                .collect(Collectors.toSet());

        Map<Long, String> herbNameMap = herbMapper.selectBatchIds(herbIds).stream()
                .collect(Collectors.toMap(Herb::getId, Herb::getName));

        return demands.stream().map(demand -> {
            DemandVO vo = new DemandVO();
            vo.setId(demand.getId());
            vo.setHerbId(demand.getHerbId());
            vo.setHerbName(herbNameMap.get(demand.getHerbId()));
            vo.setSpec(demand.getSpec());
            vo.setQuantity(demand.getQuantity());
            vo.setUnit(demand.getUnit());
            vo.setDeliveryAddress(demand.getDeliveryAddress());
            vo.setQuoteCount(demand.getQuoteCount());
            vo.setDescription(demand.getDescription());
            vo.setExpireAt(demand.getExpireAt());
            vo.setCreatedAt(demand.getCreatedAt());
            return vo;
        }).collect(Collectors.toList());
    }
}

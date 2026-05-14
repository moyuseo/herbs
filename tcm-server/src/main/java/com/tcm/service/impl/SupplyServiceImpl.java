package com.tcm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tcm.common.BusinessException;
import com.tcm.common.PageResult;
import com.tcm.dto.SupplyPublishDTO;
import com.tcm.dto.SupplyQueryDTO;
import com.tcm.entity.Herb;
import com.tcm.entity.Supply;
import com.tcm.mapper.HerbMapper;
import com.tcm.mapper.SupplyMapper;
import com.tcm.service.SupplyService;
import com.tcm.vo.SupplyVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SupplyServiceImpl implements SupplyService {

    private final SupplyMapper supplyMapper;
    private final HerbMapper herbMapper;

    public SupplyServiceImpl(SupplyMapper supplyMapper, HerbMapper herbMapper) {
        this.supplyMapper = supplyMapper;
        this.herbMapper = herbMapper;
    }

    @Override
    public PageResult<SupplyVO> getSupplyList(SupplyQueryDTO dto) {
        Page<Supply> page = new Page<>(dto.getPage(), dto.getPageSize());

        LambdaQueryWrapper<Supply> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Supply::getStatus, 1);
        if (dto.getHerbId() != null) {
            wrapper.eq(Supply::getHerbId, dto.getHerbId());
        }
        if (dto.getOrigin() != null && !dto.getOrigin().isEmpty()) {
            wrapper.eq(Supply::getOrigin, dto.getOrigin());
        }
        wrapper.orderByDesc(Supply::getCreatedAt);

        Page<Supply> result = supplyMapper.selectPage(page, wrapper);

        List<SupplyVO> voList = convertToVOList(result.getRecords());

        return new PageResult<>(voList, result.getTotal(), dto.getPage(), dto.getPageSize());
    }

    @Override
    public Long publishSupply(SupplyPublishDTO dto, Long userId) {
        Supply supply = new Supply();
        supply.setUserId(userId);
        supply.setHerbId(dto.getHerbId());
        supply.setSpec(dto.getSpec());
        supply.setOrigin(dto.getOrigin());
        supply.setQuantity(dto.getQuantity());
        supply.setUnit(dto.getUnit());
        supply.setPriceType(dto.getPriceType());
        supply.setPrice(dto.getPrice());
        supply.setContactName(dto.getContactName());
        supply.setContactPhone(dto.getContactPhone());
        supply.setImages(dto.getImages());
        supply.setDescription(dto.getDescription());
        supply.setIsTop(0);
        supply.setStatus(1);
        supply.setExpireAt(LocalDateTime.now().plusDays(30));

        supplyMapper.insert(supply);
        return supply.getId();
    }

    @Override
    public void deleteSupply(Long id, Long userId) {
        Supply supply = supplyMapper.selectById(id);
        if (supply == null) {
            throw new BusinessException("供应信息不存在");
        }
        if (!supply.getUserId().equals(userId)) {
            throw new BusinessException("无权删除此供应信息");
        }
        supply.setStatus(0);
        supplyMapper.updateById(supply);
    }

    private List<SupplyVO> convertToVOList(List<Supply> supplies) {
        if (supplies == null || supplies.isEmpty()) {
            return new ArrayList<>();
        }

        Set<Long> herbIds = supplies.stream()
                .map(Supply::getHerbId)
                .collect(Collectors.toSet());

        Map<Long, String> herbNameMap = herbMapper.selectBatchIds(herbIds).stream()
                .collect(Collectors.toMap(Herb::getId, Herb::getName));

        return supplies.stream().map(supply -> {
            SupplyVO vo = new SupplyVO();
            vo.setId(supply.getId());
            vo.setHerbId(supply.getHerbId());
            vo.setHerbName(herbNameMap.get(supply.getHerbId()));
            vo.setSpec(supply.getSpec());
            vo.setOrigin(supply.getOrigin());
            vo.setQuantity(supply.getQuantity());
            vo.setUnit(supply.getUnit());
            vo.setPriceType(supply.getPriceType());
            vo.setPrice(supply.getPrice());
            vo.setContactName(supply.getContactName());
            vo.setContactPhone(supply.getContactPhone());
            vo.setDescription(supply.getDescription());
            vo.setCreatedAt(supply.getCreatedAt());
            return vo;
        }).collect(Collectors.toList());
    }
}

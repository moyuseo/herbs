package com.tcm.module.market.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tcm.common.result.ResultCode;
import com.tcm.module.market.dto.HerbDetailVO;
import com.tcm.module.market.dto.PriceQueryDTO;
import com.tcm.module.market.entity.*;
import com.tcm.module.market.mapper.*;
import com.tcm.module.market.service.MarketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarketServiceImpl implements MarketService {

    private final MarketPriceMapper marketPriceMapper;
    private final OriginPriceMapper originPriceMapper;
    private final HerbMapper herbMapper;
    private final HerbSpecMapper herbSpecMapper;
    private final MarketMapper marketMapper;
    private final OriginAreaMapper originAreaMapper;
    private final PriceHistoryMapper priceHistoryMapper;
    private final PriceIndexMapper priceIndexMapper;

    @Override
    public IPage<MarketPrice> getMarketPrices(PriceQueryDTO query) {
        Page<MarketPrice> page = new Page<>(query.getPage(), query.getSize());
        LambdaQueryWrapper<MarketPrice> wrapper = new LambdaQueryWrapper<>();
        if (query.getSpecId() != null) {
            wrapper.eq(MarketPrice::getHerbSpecId, query.getSpecId());
        }
        if (query.getMarketId() != null) {
            wrapper.eq(MarketPrice::getMarketId, query.getMarketId());
        }
        if (query.getTrend() != null) {
            wrapper.eq(MarketPrice::getTrend, query.getTrend());
        }
        if (query.getHerbId() != null) {
            List<Long> specIds = getSpecIdsByHerbId(query.getHerbId());
            if (specIds.isEmpty()) {
                return page;
            }
            wrapper.in(MarketPrice::getHerbSpecId, specIds);
        }
        wrapper.orderByDesc(MarketPrice::getPriceDate);
        IPage<MarketPrice> result = marketPriceMapper.selectPage(page, wrapper);
        fillMarketName(result);
        return result;
    }

    @Override
    public IPage<OriginPrice> getOriginPrices(PriceQueryDTO query) {
        Page<OriginPrice> page = new Page<>(query.getPage(), query.getSize());
        LambdaQueryWrapper<OriginPrice> wrapper = new LambdaQueryWrapper<>();
        if (query.getSpecId() != null) {
            wrapper.eq(OriginPrice::getHerbSpecId, query.getSpecId());
        }
        if (query.getAreaId() != null) {
            wrapper.eq(OriginPrice::getAreaId, query.getAreaId());
        }
        if (query.getTrend() != null) {
            wrapper.eq(OriginPrice::getTrend, query.getTrend());
        }
        if (query.getHerbId() != null) {
            List<Long> specIds = getSpecIdsByHerbId(query.getHerbId());
            if (specIds.isEmpty()) {
                return page;
            }
            wrapper.in(OriginPrice::getHerbSpecId, specIds);
        }
        wrapper.orderByDesc(OriginPrice::getPriceDate);
        IPage<OriginPrice> result = originPriceMapper.selectPage(page, wrapper);
        fillAreaName(result);
        return result;
    }

    @Override
    public HerbDetailVO getHerbDetail(Long herbId) {
        Herb herb = herbMapper.selectById(herbId);
        if (herb == null) {
            throw new com.tcm.common.exception.BusinessException(ResultCode.NOT_FOUND, "药材不存在");
        }
        HerbDetailVO vo = new HerbDetailVO();
        vo.setId(herb.getId());
        vo.setName(herb.getName());
        vo.setPinyin(herb.getPinyin());
        vo.setAlias(herb.getAlias());
        vo.setOriginAreas(herb.getOriginAreas());
        vo.setProperties(herb.getProperties());
        vo.setMeridian(herb.getMeridian());
        vo.setEfficacy(herb.getEfficacy());
        vo.setDescription(herb.getDescription());
        vo.setImageUrl(herb.getImageUrl());

        List<HerbSpec> specs = herbSpecMapper.selectList(
                new LambdaQueryWrapper<HerbSpec>().eq(HerbSpec::getHerbId, herbId));
        List<HerbDetailVO.SpecPrice> specPrices = new ArrayList<>();
        for (HerbSpec spec : specs) {
            MarketPrice latestPrice = marketPriceMapper.selectOne(
                    new LambdaQueryWrapper<MarketPrice>()
                            .eq(MarketPrice::getHerbSpecId, spec.getId())
                            .orderByDesc(MarketPrice::getPriceDate)
                            .last("LIMIT 1"));
            HerbDetailVO.SpecPrice sp = new HerbDetailVO.SpecPrice();
            sp.setSpecId(spec.getId());
            sp.setSpecName(spec.getSpecName());
            if (latestPrice != null) {
                sp.setPrice(latestPrice.getPrice());
                sp.setChangePercent(latestPrice.getChangePercent());
                sp.setTrend(latestPrice.getTrend());
            }
            specPrices.add(sp);
        }
        vo.setSpecPrices(specPrices);

        List<HerbDetailVO.OriginPriceVO> originPrices = new ArrayList<>();
        if (!specs.isEmpty()) {
            List<Long> specIds = specs.stream().map(HerbSpec::getId).collect(Collectors.toList());
            List<OriginPrice> originPriceList = originPriceMapper.selectList(
                    new LambdaQueryWrapper<OriginPrice>()
                            .in(OriginPrice::getHerbSpecId, specIds)
                            .orderByDesc(OriginPrice::getPriceDate));
            Map<Long, OriginPrice> latestByArea = new LinkedHashMap<>();
            for (OriginPrice op : originPriceList) {
                latestByArea.putIfAbsent(op.getAreaId(), op);
            }
            for (Map.Entry<Long, OriginPrice> entry : latestByArea.entrySet()) {
                OriginPrice op = entry.getValue();
                OriginArea area = originAreaMapper.selectById(op.getAreaId());
                HerbDetailVO.OriginPriceVO opVO = new HerbDetailVO.OriginPriceVO();
                opVO.setAreaId(op.getAreaId());
                opVO.setAreaName(area != null ? area.getName() : "");
                opVO.setPrice(op.getPrice());
                opVO.setChangePercent(op.getChangePercent());
                opVO.setTrend(op.getTrend());
                originPrices.add(opVO);
            }
        }
        vo.setOriginPrices(originPrices);

        List<HerbDetailVO.MarketPriceVO> marketPrices = new ArrayList<>();
        if (!specs.isEmpty()) {
            List<Long> specIds = specs.stream().map(HerbSpec::getId).collect(Collectors.toList());
            List<MarketPrice> marketPriceList = marketPriceMapper.selectList(
                    new LambdaQueryWrapper<MarketPrice>()
                            .in(MarketPrice::getHerbSpecId, specIds)
                            .orderByDesc(MarketPrice::getPriceDate));
            Map<Long, MarketPrice> latestByMarket = new LinkedHashMap<>();
            for (MarketPrice mp : marketPriceList) {
                latestByMarket.putIfAbsent(mp.getMarketId(), mp);
            }
            for (Map.Entry<Long, MarketPrice> entry : latestByMarket.entrySet()) {
                MarketPrice mp = entry.getValue();
                Market market = marketMapper.selectById(mp.getMarketId());
                HerbDetailVO.MarketPriceVO mpVO = new HerbDetailVO.MarketPriceVO();
                mpVO.setMarketId(mp.getMarketId());
                mpVO.setMarketName(market != null ? market.getName() : "");
                mpVO.setPrice(mp.getPrice());
                mpVO.setChangePercent(mp.getChangePercent());
                mpVO.setTrend(mp.getTrend());
                marketPrices.add(mpVO);
            }
        }
        vo.setMarketPrices(marketPrices);

        return vo;
    }

    @Override
    public List<Map<String, Object>> getPriceHistory(Long herbSpecId, String period) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = switch (period) {
            case "1m" -> endDate.minusMonths(1);
            case "3m" -> endDate.minusMonths(3);
            case "6m" -> endDate.minusMonths(6);
            case "1y" -> endDate.minusYears(1);
            case "3y" -> endDate.minusYears(3);
            default -> LocalDate.of(2000, 1, 1);
        };
        List<PriceHistory> historyList = priceHistoryMapper.selectList(
                new LambdaQueryWrapper<PriceHistory>()
                        .eq(PriceHistory::getHerbSpecId, herbSpecId)
                        .ge(PriceHistory::getRecordDate, startDate)
                        .le(PriceHistory::getRecordDate, endDate)
                        .orderByAsc(PriceHistory::getRecordDate));
        return historyList.stream().map(h -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("date", h.getRecordDate().toString());
            map.put("price", h.getPrice());
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getRanking(String period, Integer limit) {
        LocalDate targetDate = switch (period) {
            case "week" -> LocalDate.now().minusWeeks(1);
            case "month" -> LocalDate.now().minusMonths(1);
            default -> LocalDate.now();
        };
        LambdaQueryWrapper<MarketPrice> wrapper = new LambdaQueryWrapper<MarketPrice>()
                .ge(MarketPrice::getPriceDate, targetDate)
                .orderByDesc(MarketPrice::getChangePercent)
                .last("LIMIT " + limit);
        List<MarketPrice> priceList = marketPriceMapper.selectList(wrapper);
        return priceList.stream().map(mp -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("herbSpecId", mp.getHerbSpecId());
            map.put("marketId", mp.getMarketId());
            map.put("price", mp.getPrice());
            map.put("changePercent", mp.getChangePercent());
            map.put("trend", mp.getTrend());
            HerbSpec spec = herbSpecMapper.selectById(mp.getHerbSpecId());
            if (spec != null) {
                Herb herb = herbMapper.selectById(spec.getHerbId());
                map.put("specName", spec.getSpecName());
                map.put("herbName", herb != null ? herb.getName() : "");
            }
            Market market = marketMapper.selectById(mp.getMarketId());
            map.put("marketName", market != null ? market.getName() : "");
            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public List<PriceIndex> getPriceIndex(String indexType) {
        String type = (indexType == null || indexType.isBlank()) ? "composite" : indexType;
        return priceIndexMapper.selectList(
                new LambdaQueryWrapper<PriceIndex>()
                        .eq(PriceIndex::getIndexType, type)
                        .orderByDesc(PriceIndex::getRecordDate));
    }

    @Override
    public IPage<Herb> searchHerbs(String keyword, Integer page, Integer size) {
        Page<Herb> p = new Page<>(page, size);
        LambdaQueryWrapper<Herb> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            wrapper.and(w -> w.like(Herb::getName, keyword).or().like(Herb::getPinyin, keyword));
        }
        wrapper.eq(Herb::getStatus, 1);
        wrapper.orderByAsc(Herb::getPinyin);
        return herbMapper.selectPage(p, wrapper);
    }

    private List<Long> getSpecIdsByHerbId(Long herbId) {
        List<HerbSpec> specs = herbSpecMapper.selectList(
                new LambdaQueryWrapper<HerbSpec>().eq(HerbSpec::getHerbId, herbId));
        return specs.stream().map(HerbSpec::getId).collect(Collectors.toList());
    }

    private void fillMarketName(IPage<MarketPrice> result) {
        for (MarketPrice mp : result.getRecords()) {
            Market market = marketMapper.selectById(mp.getMarketId());
            if (market != null) {
                mp.setTrend(mp.getTrend() != null ? mp.getTrend() : "stable");
            }
        }
    }

    private void fillAreaName(IPage<OriginPrice> result) {
        for (OriginPrice op : result.getRecords()) {
            OriginArea area = originAreaMapper.selectById(op.getAreaId());
            if (area != null) {
                op.setTrend(op.getTrend() != null ? op.getTrend() : "stable");
            }
        }
    }
}

package com.tcm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tcm.common.PageResult;
import com.tcm.dto.PriceQueryDTO;
import com.tcm.entity.Herb;
import com.tcm.entity.Price;
import com.tcm.mapper.HerbMapper;
import com.tcm.mapper.PriceMapper;
import com.tcm.service.PriceService;
import com.tcm.vo.PriceDetailVO;
import com.tcm.vo.PriceHistoryVO;
import com.tcm.vo.PriceVO;
import com.tcm.vo.RankingVO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PriceServiceImpl implements PriceService {

    private final PriceMapper priceMapper;
    private final HerbMapper herbMapper;

    public PriceServiceImpl(PriceMapper priceMapper, HerbMapper herbMapper) {
        this.priceMapper = priceMapper;
        this.herbMapper = herbMapper;
    }

    @Override
    public PageResult<PriceVO> getMarketPrices(PriceQueryDTO dto) {
        return getPriceList(dto, 1);
    }

    @Override
    public PageResult<PriceVO> getOriginPrices(PriceQueryDTO dto) {
        return getPriceList(dto, 2);
    }

    private PageResult<PriceVO> getPriceList(PriceQueryDTO dto, int priceType) {
        LocalDate latestDate = getLatestDate(priceType);
        if (latestDate == null) {
            return new PageResult<>(new ArrayList<>(), 0, dto.getPage(), dto.getPageSize());
        }

        LambdaQueryWrapper<Price> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Price::getPriceType, priceType)
                .eq(Price::getPriceDate, latestDate);
        if (dto.getMarket() != null && !dto.getMarket().isEmpty()) {
            wrapper.eq(Price::getMarket, dto.getMarket());
        }
        if (dto.getOrigin() != null && !dto.getOrigin().isEmpty()) {
            wrapper.eq(Price::getOrigin, dto.getOrigin());
        }
        if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
            List<Long> herbIds = herbMapper.selectList(
                    new LambdaQueryWrapper<Herb>().like(Herb::getName, dto.getKeyword())
            ).stream().map(Herb::getId).collect(Collectors.toList());
            if (herbIds.isEmpty()) {
                return new PageResult<>(new ArrayList<>(), 0, dto.getPage(), dto.getPageSize());
            }
            wrapper.in(Price::getHerbId, herbIds);
        }
        if (dto.getCategoryId() != null) {
            List<Long> herbIds = herbMapper.selectList(
                    new LambdaQueryWrapper<Herb>().eq(Herb::getCategoryId, dto.getCategoryId())
            ).stream().map(Herb::getId).collect(Collectors.toList());
            if (herbIds.isEmpty()) {
                return new PageResult<>(new ArrayList<>(), 0, dto.getPage(), dto.getPageSize());
            }
            wrapper.in(Price::getHerbId, herbIds);
        }

        Page<Price> page = new Page<>(dto.getPage(), dto.getPageSize());
        Page<Price> result = priceMapper.selectPage(page, wrapper);

        LocalDate prevDay = latestDate.minusDays(1);
        LocalDate prevMonth = latestDate.minusMonths(1);

        Map<Long, Herb> herbMap = herbMapper.selectList(null).stream()
                .collect(Collectors.toMap(Herb::getId, h -> h));

        List<PriceVO> voList = result.getRecords().stream().map(p -> {
            PriceVO vo = new PriceVO();
            vo.setHerbId(p.getHerbId());
            Herb herb = herbMap.get(p.getHerbId());
            vo.setHerbName(herb != null ? herb.getName() : null);
            vo.setSpec(p.getSpec());
            vo.setOrigin(p.getOrigin());
            vo.setMarket(p.getMarket());
            vo.setPrice(p.getPrice());
            vo.setTrend(p.getTrend());

            BigDecimal prevDayPrice = getPriceOnDate(p.getHerbId(), priceType, p.getMarket(), prevDay);
            if (prevDayPrice != null && p.getPrice() != null) {
                vo.setDayChange(p.getPrice().subtract(prevDayPrice));
                if (prevDayPrice.compareTo(BigDecimal.ZERO) != 0) {
                    vo.setDayChangeRate(p.getPrice().subtract(prevDayPrice)
                            .divide(prevDayPrice, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100)));
                }
            }

            BigDecimal prevMonthPrice = getPriceOnDate(p.getHerbId(), priceType, p.getMarket(), prevMonth);
            if (prevMonthPrice != null && p.getPrice() != null) {
                vo.setMonthChange(p.getPrice().subtract(prevMonthPrice));
                if (prevMonthPrice.compareTo(BigDecimal.ZERO) != 0) {
                    vo.setMonthChangeRate(p.getPrice().subtract(prevMonthPrice)
                            .divide(prevMonthPrice, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100)));
                }
            }

            return vo;
        }).collect(Collectors.toList());

        return new PageResult<>(voList, result.getTotal(), dto.getPage(), dto.getPageSize());
    }

    @Override
    public PriceDetailVO getPriceDetail(Long herbId) {
        Herb herb = herbMapper.selectById(herbId);
        if (herb == null) {
            return null;
        }

        PriceDetailVO vo = new PriceDetailVO();
        vo.setHerbId(herb.getId());
        vo.setHerbName(herb.getName());
        vo.setAlias(herb.getAlias());
        vo.setPinyin(herb.getPinyin());
        vo.setMedicinalPart(herb.getMedicinalPart());
        vo.setNatureFlavor(herb.getNatureFlavor());
        vo.setMeridianTropism(herb.getMeridianTropism());
        vo.setEfficacy(herb.getEfficacy());

        LocalDate latestDate = getLatestDate(1);
        if (latestDate != null) {
            List<Price> marketPriceList = priceMapper.selectList(
                    new LambdaQueryWrapper<Price>()
                            .eq(Price::getHerbId, herbId)
                            .eq(Price::getPriceType, 1)
                            .eq(Price::getPriceDate, latestDate)
            );
            List<PriceDetailVO.MarketPriceItem> items = marketPriceList.stream().map(p -> {
                PriceDetailVO.MarketPriceItem item = new PriceDetailVO.MarketPriceItem();
                item.setMarket(p.getMarket());
                item.setSpec(p.getSpec());
                item.setPrice(p.getPrice());
                item.setTrend(p.getTrend());
                return item;
            }).collect(Collectors.toList());
            vo.setMarketPrices(items);

            if (!marketPriceList.isEmpty()) {
                BigDecimal avgPrice = marketPriceList.stream()
                        .map(Price::getPrice)
                        .filter(java.util.Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(marketPriceList.stream()
                                .map(Price::getPrice)
                                .filter(java.util.Objects::nonNull)
                                .count()), 2, RoundingMode.HALF_UP);

                LocalDate prevDay = latestDate.minusDays(1);
                BigDecimal prevDayAvg = getAvgPriceOnDate(herbId, 1, prevDay);
                if (prevDayAvg != null) {
                    vo.setDayChange(avgPrice.subtract(prevDayAvg));
                    if (prevDayAvg.compareTo(BigDecimal.ZERO) != 0) {
                        vo.setDayChangeRate(avgPrice.subtract(prevDayAvg)
                                .divide(prevDayAvg, 4, RoundingMode.HALF_UP)
                                .multiply(BigDecimal.valueOf(100)));
                    }
                }

                LocalDate prevMonth = latestDate.minusMonths(1);
                BigDecimal prevMonthAvg = getAvgPriceOnDate(herbId, 1, prevMonth);
                if (prevMonthAvg != null) {
                    vo.setMonthChange(avgPrice.subtract(prevMonthAvg));
                    if (prevMonthAvg.compareTo(BigDecimal.ZERO) != 0) {
                        vo.setMonthChangeRate(avgPrice.subtract(prevMonthAvg)
                                .divide(prevMonthAvg, 4, RoundingMode.HALF_UP)
                                .multiply(BigDecimal.valueOf(100)));
                    }
                }
            }
        }

        LocalDate oneYearAgo = LocalDate.now().minusYears(1);
        List<Price> yearPrices = priceMapper.selectList(
                new LambdaQueryWrapper<Price>()
                        .eq(Price::getHerbId, herbId)
                        .eq(Price::getPriceType, 1)
                        .ge(Price::getPriceDate, oneYearAgo)
        );
        if (!yearPrices.isEmpty()) {
            vo.setYearHigh(yearPrices.stream()
                    .map(Price::getPrice)
                    .filter(java.util.Objects::nonNull)
                    .max(Comparator.naturalOrder()).orElse(null));
            vo.setYearLow(yearPrices.stream()
                    .map(Price::getPrice)
                    .filter(java.util.Objects::nonNull)
                    .min(Comparator.naturalOrder()).orElse(null));
        }

        return vo;
    }

    @Override
    public PriceHistoryVO getPriceHistory(Long herbId, String period) {
        Herb herb = herbMapper.selectById(herbId);
        if (herb == null) {
            return null;
        }

        LocalDate endDate = LocalDate.now();
        LocalDate startDate;
        switch (period != null ? period : "month") {
            case "week":
                startDate = endDate.minusWeeks(1);
                break;
            case "year":
                startDate = endDate.minusYears(1);
                break;
            case "day":
                startDate = endDate.minusDays(30);
                break;
            default:
                startDate = endDate.minusMonths(6);
                break;
        }

        List<Price> priceList = priceMapper.selectList(
                new LambdaQueryWrapper<Price>()
                        .eq(Price::getHerbId, herbId)
                        .eq(Price::getPriceType, 1)
                        .ge(Price::getPriceDate, startDate)
                        .le(Price::getPriceDate, endDate)
                        .orderByAsc(Price::getPriceDate)
        );

        Map<LocalDate, BigDecimal> datePriceMap = priceList.stream()
                .collect(Collectors.groupingBy(
                        Price::getPriceDate,
                        Collectors.mapping(Price::getPrice,
                                Collectors.reducing(BigDecimal.ZERO, (a, b) -> a.add(b).divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP)))
                ));

        List<String> dates = new ArrayList<>();
        List<BigDecimal> prices = new ArrayList<>();
        datePriceMap.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(e -> {
                    dates.add(e.getKey().toString());
                    prices.add(e.getValue());
                });

        PriceHistoryVO vo = new PriceHistoryVO();
        vo.setHerbId(herbId);
        vo.setHerbName(herb.getName());
        vo.setDates(dates);
        vo.setPrices(prices);

        if (!prices.isEmpty()) {
            vo.setHighestPrice(prices.stream().max(Comparator.naturalOrder()).orElse(null));
            vo.setLowestPrice(prices.stream().min(Comparator.naturalOrder()).orElse(null));
            BigDecimal sum = prices.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            vo.setAvgPrice(sum.divide(BigDecimal.valueOf(prices.size()), 2, RoundingMode.HALF_UP));
        }

        return vo;
    }

    @Override
    public PageResult<RankingVO> getRanking(String period, Integer page, Integer pageSize) {
        LocalDate latestDate = getLatestDate(1);
        if (latestDate == null) {
            return new PageResult<>(new ArrayList<>(), 0, page, pageSize);
        }

        List<Price> latestPrices = priceMapper.selectList(
                new LambdaQueryWrapper<Price>()
                        .eq(Price::getPriceType, 1)
                        .eq(Price::getPriceDate, latestDate)
        );

        LocalDate compareDate;
        switch (period != null ? period : "day") {
            case "week":
                compareDate = latestDate.minusWeeks(1);
                break;
            case "month":
                compareDate = latestDate.minusMonths(1);
                break;
            default:
                compareDate = latestDate.minusDays(1);
                break;
        }

        Map<Long, BigDecimal> herbLatestPriceMap = latestPrices.stream()
                .filter(p -> p.getPrice() != null)
                .collect(Collectors.groupingBy(
                        Price::getHerbId,
                        Collectors.reducing(BigDecimal.ZERO, Price::getPrice, (a, b) -> a.add(b).divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP))
                ));

        List<Price> comparePrices = priceMapper.selectList(
                new LambdaQueryWrapper<Price>()
                        .eq(Price::getPriceType, 1)
                        .eq(Price::getPriceDate, compareDate)
        );
        Map<Long, BigDecimal> herbComparePriceMap = comparePrices.stream()
                .filter(p -> p.getPrice() != null)
                .collect(Collectors.groupingBy(
                        Price::getHerbId,
                        Collectors.reducing(BigDecimal.ZERO, Price::getPrice, (a, b) -> a.add(b).divide(BigDecimal.valueOf(2), 2, RoundingMode.HALF_UP))
                ));

        Map<Long, String> herbTrendMap = latestPrices.stream()
                .collect(Collectors.toMap(
                        Price::getHerbId,
                        p -> p.getTrend() != null ? p.getTrend() : "flat",
                        (a, b) -> a
                ));

        Map<Long, Herb> herbMap = herbMapper.selectList(null).stream()
                .collect(Collectors.toMap(Herb::getId, h -> h));

        List<RankingVO> allRanking = herbLatestPriceMap.entrySet().stream()
                .map(entry -> {
                    RankingVO vo = new RankingVO();
                    vo.setHerbId(entry.getKey());
                    Herb herb = herbMap.get(entry.getKey());
                    vo.setHerbName(herb != null ? herb.getName() : null);
                    vo.setCurrentPrice(entry.getValue());
                    vo.setTrend(herbTrendMap.get(entry.getKey()));

                    BigDecimal comparePrice = herbComparePriceMap.get(entry.getKey());
                    if (comparePrice != null && comparePrice.compareTo(BigDecimal.ZERO) != 0) {
                        vo.setChangeRate(entry.getValue().subtract(comparePrice)
                                .divide(comparePrice, 4, RoundingMode.HALF_UP)
                                .multiply(BigDecimal.valueOf(100)));
                    }
                    return vo;
                })
                .sorted(Comparator.comparing(
                        (RankingVO v) -> v.getChangeRate() != null ? v.getChangeRate().abs() : BigDecimal.ZERO,
                        Comparator.reverseOrder()))
                .collect(Collectors.toList());

        int total = allRanking.size();
        int fromIndex = (page - 1) * pageSize;
        if (fromIndex >= total) {
            return new PageResult<>(new ArrayList<>(), total, page, pageSize);
        }
        int toIndex = Math.min(fromIndex + pageSize, total);
        List<RankingVO> pagedList = allRanking.subList(fromIndex, toIndex);

        return new PageResult<>(pagedList, total, page, pageSize);
    }

    private LocalDate getLatestDate(int priceType) {
        Price latest = priceMapper.selectOne(
                new LambdaQueryWrapper<Price>()
                        .eq(Price::getPriceType, priceType)
                        .orderByDesc(Price::getPriceDate)
                        .last("LIMIT 1")
        );
        return latest != null ? latest.getPriceDate() : null;
    }

    private BigDecimal getPriceOnDate(Long herbId, int priceType, String market, LocalDate date) {
        LambdaQueryWrapper<Price> wrapper = new LambdaQueryWrapper<Price>()
                .eq(Price::getHerbId, herbId)
                .eq(Price::getPriceType, priceType)
                .eq(Price::getPriceDate, date);
        if (market != null && !market.isEmpty()) {
            wrapper.eq(Price::getMarket, market);
        }
        wrapper.last("LIMIT 1");
        Price p = priceMapper.selectOne(wrapper);
        return p != null ? p.getPrice() : null;
    }

    private BigDecimal getAvgPriceOnDate(Long herbId, int priceType, LocalDate date) {
        List<Price> prices = priceMapper.selectList(
                new LambdaQueryWrapper<Price>()
                        .eq(Price::getHerbId, herbId)
                        .eq(Price::getPriceType, priceType)
                        .eq(Price::getPriceDate, date)
        );
        if (prices.isEmpty()) {
            return null;
        }
        return prices.stream()
                .map(Price::getPrice)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(prices.stream()
                        .map(Price::getPrice)
                        .filter(java.util.Objects::nonNull)
                        .count()), 2, RoundingMode.HALF_UP);
    }
}

package com.tcm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tcm.entity.PriceIndex;
import com.tcm.mapper.PriceIndexMapper;
import com.tcm.service.PriceIndexService;
import com.tcm.vo.PriceIndexVO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PriceIndexServiceImpl implements PriceIndexService {

    private final PriceIndexMapper priceIndexMapper;

    private static final Map<String, String> INDEX_TYPE_NAME_MAP = new HashMap<>();

    static {
        INDEX_TYPE_NAME_MAP.put("composite", "综合指数");
        INDEX_TYPE_NAME_MAP.put("root", "根茎类指数");
        INDEX_TYPE_NAME_MAP.put("fruit", "果实类指数");
        INDEX_TYPE_NAME_MAP.put("flower", "花类指数");
        INDEX_TYPE_NAME_MAP.put("herb", "全草类指数");
    }

    public PriceIndexServiceImpl(PriceIndexMapper priceIndexMapper) {
        this.priceIndexMapper = priceIndexMapper;
    }

    @Override
    public List<PriceIndexVO> getIndexHistory(String indexType, String period) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = getStartDate(period, endDate);

        List<String> types = resolveIndexTypes(indexType);
        List<PriceIndexVO> result = new ArrayList<>();

        for (String type : types) {
            List<PriceIndex> records = priceIndexMapper.selectList(
                    new LambdaQueryWrapper<PriceIndex>()
                            .eq(PriceIndex::getIndexType, type)
                            .ge(PriceIndex::getIndexDate, startDate)
                            .le(PriceIndex::getIndexDate, endDate)
                            .orderByAsc(PriceIndex::getIndexDate)
            );

            PriceIndexVO vo = new PriceIndexVO();
            vo.setIndexType(type);
            vo.setIndexTypeName(INDEX_TYPE_NAME_MAP.getOrDefault(type, type));

            List<String> dates = new ArrayList<>();
            List<BigDecimal> values = new ArrayList<>();
            for (PriceIndex record : records) {
                dates.add(record.getIndexDate().toString());
                values.add(record.getIndexValue());
            }
            vo.setDates(dates);
            vo.setValues(values);

            if (!records.isEmpty()) {
                PriceIndex latest = records.get(records.size() - 1);
                vo.setIndexValue(latest.getIndexValue());
                vo.setChangeRate(latest.getChangeRate());
            }

            result.add(vo);
        }

        return result;
    }

    @Override
    public PriceIndexVO getLatestIndex(String indexType) {
        List<String> types = resolveIndexTypes(indexType);

        if (types.size() == 1) {
            PriceIndex latest = priceIndexMapper.selectOne(
                    new LambdaQueryWrapper<PriceIndex>()
                            .eq(PriceIndex::getIndexType, types.get(0))
                            .orderByDesc(PriceIndex::getIndexDate)
                            .last("LIMIT 1")
            );
            if (latest == null) {
                return null;
            }
            PriceIndexVO vo = new PriceIndexVO();
            vo.setIndexType(latest.getIndexType());
            vo.setIndexTypeName(INDEX_TYPE_NAME_MAP.getOrDefault(latest.getIndexType(), latest.getIndexType()));
            vo.setIndexValue(latest.getIndexValue());
            vo.setChangeRate(latest.getChangeRate());
            return vo;
        }

        List<PriceIndexVO> result = new ArrayList<>();
        for (String type : types) {
            PriceIndex latest = priceIndexMapper.selectOne(
                    new LambdaQueryWrapper<PriceIndex>()
                            .eq(PriceIndex::getIndexType, type)
                            .orderByDesc(PriceIndex::getIndexDate)
                            .last("LIMIT 1")
            );
            if (latest != null) {
                PriceIndexVO vo = new PriceIndexVO();
                vo.setIndexType(latest.getIndexType());
                vo.setIndexTypeName(INDEX_TYPE_NAME_MAP.getOrDefault(latest.getIndexType(), latest.getIndexType()));
                vo.setIndexValue(latest.getIndexValue());
                vo.setChangeRate(latest.getChangeRate());
                result.add(vo);
            }
        }

        if (result.isEmpty()) {
            return null;
        }

        return result.get(0);
    }

    private LocalDate getStartDate(String period, LocalDate endDate) {
        if (period == null) {
            period = "month";
        }
        switch (period) {
            case "3month":
                return endDate.minusMonths(3);
            case "6month":
                return endDate.minusMonths(6);
            case "year":
                return endDate.minusYears(1);
            default:
                return endDate.minusMonths(1);
        }
    }

    private List<String> resolveIndexTypes(String indexType) {
        List<String> types = new ArrayList<>();
        if (indexType == null || indexType.isEmpty() || "composite".equals(indexType)) {
            types.add("composite");
        } else {
            types.add(indexType);
        }
        return types;
    }
}

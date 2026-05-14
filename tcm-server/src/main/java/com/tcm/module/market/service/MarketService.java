package com.tcm.module.market.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tcm.module.market.dto.HerbDetailVO;
import com.tcm.module.market.dto.PriceQueryDTO;
import com.tcm.module.market.entity.Herb;
import com.tcm.module.market.entity.MarketPrice;
import com.tcm.module.market.entity.OriginPrice;
import com.tcm.module.market.entity.PriceIndex;

import java.util.List;
import java.util.Map;

public interface MarketService {

    IPage<MarketPrice> getMarketPrices(PriceQueryDTO query);

    IPage<OriginPrice> getOriginPrices(PriceQueryDTO query);

    HerbDetailVO getHerbDetail(Long herbId);

    List<Map<String, Object>> getPriceHistory(Long herbSpecId, String period);

    List<Map<String, Object>> getRanking(String period, Integer limit);

    List<PriceIndex> getPriceIndex(String indexType);

    IPage<Herb> searchHerbs(String keyword, Integer page, Integer size);
}

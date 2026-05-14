package com.tcm.service;

import com.tcm.common.PageResult;
import com.tcm.dto.PriceQueryDTO;
import com.tcm.vo.PriceDetailVO;
import com.tcm.vo.PriceHistoryVO;
import com.tcm.vo.PriceVO;
import com.tcm.vo.RankingVO;

public interface PriceService {

    PageResult<PriceVO> getMarketPrices(PriceQueryDTO dto);

    PageResult<PriceVO> getOriginPrices(PriceQueryDTO dto);

    PriceDetailVO getPriceDetail(Long herbId);

    PriceHistoryVO getPriceHistory(Long herbId, String period);

    PageResult<RankingVO> getRanking(String period, Integer page, Integer pageSize);
}

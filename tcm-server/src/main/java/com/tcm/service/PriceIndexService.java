package com.tcm.service;

import com.tcm.vo.PriceIndexVO;
import java.util.List;

public interface PriceIndexService {
    List<PriceIndexVO> getIndexHistory(String indexType, String period);
    PriceIndexVO getLatestIndex(String indexType);
}

package com.tcm.service;

import com.tcm.common.PageResult;
import com.tcm.dto.SupplyQueryDTO;
import com.tcm.dto.SupplyPublishDTO;
import com.tcm.vo.SupplyVO;

public interface SupplyService {
    PageResult<SupplyVO> getSupplyList(SupplyQueryDTO dto);

    Long publishSupply(SupplyPublishDTO dto, Long userId);

    void deleteSupply(Long id, Long userId);
}

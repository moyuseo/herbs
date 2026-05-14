package com.tcm.service;

import com.tcm.common.PageResult;
import com.tcm.dto.DemandQueryDTO;
import com.tcm.dto.DemandPublishDTO;
import com.tcm.vo.DemandVO;

public interface DemandService {
    PageResult<DemandVO> getDemandList(DemandQueryDTO dto);

    Long publishDemand(DemandPublishDTO dto, Long userId);
}

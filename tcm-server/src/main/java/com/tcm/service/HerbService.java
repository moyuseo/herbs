package com.tcm.service;

import com.tcm.common.PageResult;
import com.tcm.vo.HerbDetailVO;
import com.tcm.vo.HerbVO;

import java.util.List;

public interface HerbService {
    PageResult<HerbVO> getHerbList(String keyword, Long categoryId, String pinyin, Integer page, Integer pageSize);
    HerbDetailVO getHerbDetail(Long herbId);
    List<HerbVO> searchHerb(String keyword);
}

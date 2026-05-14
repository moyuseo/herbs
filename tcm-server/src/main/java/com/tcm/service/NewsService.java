package com.tcm.service;

import com.tcm.common.PageResult;
import com.tcm.dto.NewsQueryDTO;
import com.tcm.vo.NewsDetailVO;
import com.tcm.vo.NewsVO;

public interface NewsService {
    PageResult<NewsVO> getNewsList(NewsQueryDTO dto);

    NewsDetailVO getNewsDetail(Long id);
}

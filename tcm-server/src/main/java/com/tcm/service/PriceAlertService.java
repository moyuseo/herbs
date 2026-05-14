package com.tcm.service;

import com.tcm.vo.PriceAlertVO;
import java.math.BigDecimal;
import java.util.List;

public interface PriceAlertService {

    List<PriceAlertVO> getUserAlerts(Long userId);

    Long createAlert(Long userId, Long herbId, Integer conditionType, BigDecimal threshold);

    void deleteAlert(Long id, Long userId);

    void toggleAlert(Long id, Long userId, Boolean active);

    void checkAndTriggerAlerts();
}

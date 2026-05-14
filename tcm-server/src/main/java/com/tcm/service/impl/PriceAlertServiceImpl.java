package com.tcm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tcm.common.BusinessException;
import com.tcm.entity.Herb;
import com.tcm.entity.Price;
import com.tcm.entity.PriceAlert;
import com.tcm.entity.User;
import com.tcm.mapper.HerbMapper;
import com.tcm.mapper.PriceAlertMapper;
import com.tcm.mapper.PriceMapper;
import com.tcm.mapper.UserMapper;
import com.tcm.service.PriceAlertService;
import com.tcm.vo.PriceAlertVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PriceAlertServiceImpl implements PriceAlertService {

    private static final Logger log = LoggerFactory.getLogger(PriceAlertServiceImpl.class);

    private static final int FREE_USER_MAX_ALERTS = 3;

    private static final String[] CONDITION_NAMES = {"", "价格高于", "价格低于", "日涨幅超过", "日跌幅超过"};

    private final PriceAlertMapper priceAlertMapper;
    private final UserMapper userMapper;
    private final HerbMapper herbMapper;
    private final PriceMapper priceMapper;

    public PriceAlertServiceImpl(PriceAlertMapper priceAlertMapper,
                                 UserMapper userMapper,
                                 HerbMapper herbMapper,
                                 PriceMapper priceMapper) {
        this.priceAlertMapper = priceAlertMapper;
        this.userMapper = userMapper;
        this.herbMapper = herbMapper;
        this.priceMapper = priceMapper;
    }

    @Override
    public List<PriceAlertVO> getUserAlerts(Long userId) {
        List<PriceAlert> alerts = priceAlertMapper.selectList(
                new LambdaQueryWrapper<PriceAlert>()
                        .eq(PriceAlert::getUserId, userId)
                        .orderByDesc(PriceAlert::getCreatedAt)
        );
        if (alerts.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> herbIds = alerts.stream()
                .map(PriceAlert::getHerbId)
                .distinct()
                .collect(Collectors.toList());
        Map<Long, Herb> herbMap = herbMapper.selectBatchIds(herbIds).stream()
                .collect(Collectors.toMap(Herb::getId, h -> h));

        Price latestPrice = priceMapper.selectOne(
                new LambdaQueryWrapper<Price>()
                        .eq(Price::getPriceType, 1)
                        .orderByDesc(Price::getPriceDate)
                        .last("LIMIT 1")
        );
        LocalDate latestDate = latestPrice != null ? latestPrice.getPriceDate() : null;

        List<PriceAlertVO> voList = new ArrayList<>();
        for (PriceAlert alert : alerts) {
            PriceAlertVO vo = new PriceAlertVO();
            vo.setId(alert.getId());
            vo.setHerbId(alert.getHerbId());

            Herb herb = herbMap.get(alert.getHerbId());
            vo.setHerbName(herb != null ? herb.getName() : null);

            vo.setConditionType(alert.getConditionType());
            vo.setConditionTypeName(getConditionTypeName(alert.getConditionType()));
            vo.setThreshold(alert.getThreshold());
            vo.setIsActive(alert.getIsActive() != null && alert.getIsActive() == 1);
            vo.setLastTriggered(alert.getLastTriggered());
            vo.setCreatedAt(alert.getCreatedAt());

            if (latestDate != null) {
                Price currentPrice = priceMapper.selectOne(
                        new LambdaQueryWrapper<Price>()
                                .eq(Price::getHerbId, alert.getHerbId())
                                .eq(Price::getPriceType, 1)
                                .eq(Price::getPriceDate, latestDate)
                                .last("LIMIT 1")
                );
                if (currentPrice != null) {
                    vo.setCurrentPrice(currentPrice.getPrice());
                }
            }

            voList.add(vo);
        }
        return voList;
    }

    @Override
    public Long createAlert(Long userId, Long herbId, Integer conditionType, BigDecimal threshold) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        boolean isMember = isMember(user);
        if (!isMember) {
            Long count = priceAlertMapper.selectCount(
                    new LambdaQueryWrapper<PriceAlert>()
                            .eq(PriceAlert::getUserId, userId)
            );
            if (count >= FREE_USER_MAX_ALERTS) {
                throw new BusinessException("免费用户最多创建" + FREE_USER_MAX_ALERTS + "条预警，请升级会员");
            }
        }

        if (conditionType < 1 || conditionType > 4) {
            throw new BusinessException("无效的条件类型");
        }
        if (threshold == null || threshold.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("阈值必须大于0");
        }

        Herb herb = herbMapper.selectById(herbId);
        if (herb == null) {
            throw new BusinessException("品种不存在");
        }

        PriceAlert alert = new PriceAlert();
        alert.setUserId(userId);
        alert.setHerbId(herbId);
        alert.setConditionType(conditionType);
        alert.setThreshold(threshold);
        alert.setIsActive(1);
        priceAlertMapper.insert(alert);
        return alert.getId();
    }

    @Override
    public void deleteAlert(Long id, Long userId) {
        PriceAlert alert = priceAlertMapper.selectById(id);
        if (alert == null || !alert.getUserId().equals(userId)) {
            throw new BusinessException("预警记录不存在");
        }
        priceAlertMapper.deleteById(id);
    }

    @Override
    public void toggleAlert(Long id, Long userId, Boolean active) {
        PriceAlert alert = priceAlertMapper.selectById(id);
        if (alert == null || !alert.getUserId().equals(userId)) {
            throw new BusinessException("预警记录不存在");
        }
        alert.setIsActive(active ? 1 : 0);
        priceAlertMapper.updateById(alert);
    }

    @Override
    @Scheduled(cron = "0 0 * * * *")
    public void checkAndTriggerAlerts() {
        List<PriceAlert> activeAlerts = priceAlertMapper.selectList(
                new LambdaQueryWrapper<PriceAlert>()
                        .eq(PriceAlert::getIsActive, 1)
        );
        if (activeAlerts.isEmpty()) {
            return;
        }

        Price latestPrice = priceMapper.selectOne(
                new LambdaQueryWrapper<Price>()
                        .eq(Price::getPriceType, 1)
                        .orderByDesc(Price::getPriceDate)
                        .last("LIMIT 1")
        );
        if (latestPrice == null) {
            return;
        }
        LocalDate latestDate = latestPrice.getPriceDate();
        LocalDate prevDate = latestDate.minusDays(1);

        for (PriceAlert alert : activeAlerts) {
            Price currentPrice = priceMapper.selectOne(
                    new LambdaQueryWrapper<Price>()
                            .eq(Price::getHerbId, alert.getHerbId())
                            .eq(Price::getPriceType, 1)
                            .eq(Price::getPriceDate, latestDate)
                            .last("LIMIT 1")
            );
            if (currentPrice == null || currentPrice.getPrice() == null) {
                continue;
            }

            boolean triggered = false;
            BigDecimal price = currentPrice.getPrice();

            switch (alert.getConditionType()) {
                case 1:
                    triggered = price.compareTo(alert.getThreshold()) > 0;
                    break;
                case 2:
                    triggered = price.compareTo(alert.getThreshold()) < 0;
                    break;
                case 3:
                case 4:
                    Price prevPrice = priceMapper.selectOne(
                            new LambdaQueryWrapper<Price>()
                                    .eq(Price::getHerbId, alert.getHerbId())
                                    .eq(Price::getPriceType, 1)
                                    .eq(Price::getPriceDate, prevDate)
                                    .last("LIMIT 1")
                    );
                    if (prevPrice != null && prevPrice.getPrice() != null
                            && prevPrice.getPrice().compareTo(BigDecimal.ZERO) != 0) {
                        BigDecimal changeRate = price.subtract(prevPrice.getPrice())
                                .divide(prevPrice.getPrice(), 4, RoundingMode.HALF_UP)
                                .multiply(BigDecimal.valueOf(100));
                        if (alert.getConditionType() == 3) {
                            triggered = changeRate.compareTo(alert.getThreshold()) > 0;
                        } else {
                            triggered = changeRate.abs().compareTo(alert.getThreshold()) > 0
                                    && changeRate.compareTo(BigDecimal.ZERO) < 0;
                        }
                    }
                    break;
                default:
                    break;
            }

            if (triggered) {
                alert.setLastTriggered(LocalDateTime.now());
                priceAlertMapper.updateById(alert);
                log.info("价格预警触发: userId={}, herbId={}, conditionType={}, threshold={}, currentPrice={}",
                        alert.getUserId(), alert.getHerbId(), alert.getConditionType(),
                        alert.getThreshold(), price);
            }
        }
    }

    private boolean isMember(User user) {
        if (user.getMembershipLevel() == null || user.getMembershipLevel() == 0) {
            return false;
        }
        if (user.getMembershipExpire() != null && user.getMembershipExpire().isAfter(LocalDateTime.now())) {
            return true;
        }
        return false;
    }

    private String getConditionTypeName(Integer conditionType) {
        if (conditionType == null || conditionType < 1 || conditionType > 4) {
            return "未知";
        }
        return CONDITION_NAMES[conditionType];
    }
}

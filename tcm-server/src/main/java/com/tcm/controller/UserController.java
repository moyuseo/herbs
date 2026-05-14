package com.tcm.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tcm.common.BusinessException;
import com.tcm.common.Result;
import com.tcm.entity.Herb;
import com.tcm.entity.Price;
import com.tcm.entity.User;
import com.tcm.entity.UserWatchlist;
import com.tcm.mapper.HerbMapper;
import com.tcm.mapper.PriceMapper;
import com.tcm.mapper.UserMapper;
import com.tcm.mapper.UserWatchlistMapper;
import com.tcm.service.PriceAlertService;
import com.tcm.vo.PriceAlertVO;
import com.tcm.vo.UserVO;
import com.tcm.vo.WatchlistVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/user")
@Tag(name = "用户中心")
public class UserController {

    private final UserMapper userMapper;
    private final UserWatchlistMapper userWatchlistMapper;
    private final HerbMapper herbMapper;
    private final PriceMapper priceMapper;
    private final PriceAlertService priceAlertService;

    public UserController(UserMapper userMapper,
                          UserWatchlistMapper userWatchlistMapper,
                          HerbMapper herbMapper,
                          PriceMapper priceMapper,
                          PriceAlertService priceAlertService) {
        this.userMapper = userMapper;
        this.userWatchlistMapper = userWatchlistMapper;
        this.herbMapper = herbMapper;
        this.priceMapper = priceMapper;
        this.priceAlertService = priceAlertService;
    }

    @GetMapping("/profile")
    @Operation(summary = "用户信息")
    public Result<UserVO> getProfile(@RequestHeader("X-User-Id") Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setPhone(user.getPhone());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setUserType(user.getUserType());
        vo.setCompany(user.getCompany());
        vo.setMembershipLevel(user.getMembershipLevel());
        return Result.success(vo);
    }

    @PutMapping("/profile")
    @Operation(summary = "更新信息")
    public Result<UserVO> updateProfile(@RequestHeader("X-User-Id") Long userId,
                                        @RequestBody UserVO userVO) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (userVO.getNickname() != null) {
            user.setNickname(userVO.getNickname());
        }
        if (userVO.getAvatar() != null) {
            user.setAvatar(userVO.getAvatar());
        }
        if (userVO.getCompany() != null) {
            user.setCompany(userVO.getCompany());
        }
        userMapper.updateById(user);

        UserVO result = new UserVO();
        result.setId(user.getId());
        result.setPhone(user.getPhone());
        result.setNickname(user.getNickname());
        result.setAvatar(user.getAvatar());
        result.setUserType(user.getUserType());
        result.setCompany(user.getCompany());
        result.setMembershipLevel(user.getMembershipLevel());
        return Result.success(result);
    }

    @GetMapping("/watchlist")
    @Operation(summary = "关注列表")
    public Result<List<WatchlistVO>> getWatchlist(@RequestHeader("X-User-Id") Long userId) {
        List<UserWatchlist> watchlist = userWatchlistMapper.selectList(
                new LambdaQueryWrapper<UserWatchlist>()
                        .eq(UserWatchlist::getUserId, userId)
                        .orderByDesc(UserWatchlist::getCreatedAt)
        );
        if (watchlist.isEmpty()) {
            return Result.success(new ArrayList<>());
        }

        List<Long> herbIds = watchlist.stream()
                .map(UserWatchlist::getHerbId)
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

        List<WatchlistVO> voList = new ArrayList<>();
        for (UserWatchlist wl : watchlist) {
            WatchlistVO vo = new WatchlistVO();
            vo.setId(wl.getId());
            vo.setHerbId(wl.getHerbId());

            Herb herb = herbMap.get(wl.getHerbId());
            vo.setHerbName(herb != null ? herb.getName() : null);

            if (latestDate != null) {
                Price currentPrice = priceMapper.selectOne(
                        new LambdaQueryWrapper<Price>()
                                .eq(Price::getHerbId, wl.getHerbId())
                                .eq(Price::getPriceType, 1)
                                .eq(Price::getPriceDate, latestDate)
                                .last("LIMIT 1")
                );
                if (currentPrice != null) {
                    vo.setCurrentPrice(currentPrice.getPrice());
                    vo.setTrend(currentPrice.getTrend());

                    LocalDate prevDay = latestDate.minusDays(1);
                    Price prevPrice = priceMapper.selectOne(
                            new LambdaQueryWrapper<Price>()
                                    .eq(Price::getHerbId, wl.getHerbId())
                                    .eq(Price::getPriceType, 1)
                                    .eq(Price::getPriceDate, prevDay)
                                    .last("LIMIT 1")
                    );
                    if (prevPrice != null && prevPrice.getPrice() != null
                            && currentPrice.getPrice() != null
                            && prevPrice.getPrice().compareTo(BigDecimal.ZERO) != 0) {
                        vo.setDayChangeRate(currentPrice.getPrice()
                                .subtract(prevPrice.getPrice())
                                .divide(prevPrice.getPrice(), 4, RoundingMode.HALF_UP)
                                .multiply(BigDecimal.valueOf(100)));
                    }
                }
            }
            voList.add(vo);
        }
        return Result.success(voList);
    }

    @PostMapping("/watchlist")
    @Operation(summary = "添加关注")
    public Result<Void> addWatchlist(@RequestHeader("X-User-Id") Long userId,
                                     @RequestBody UserWatchlist body) {
        Long herbId = body.getHerbId();
        UserWatchlist existing = userWatchlistMapper.selectOne(
                new LambdaQueryWrapper<UserWatchlist>()
                        .eq(UserWatchlist::getUserId, userId)
                        .eq(UserWatchlist::getHerbId, herbId)
        );
        if (existing != null) {
            throw new BusinessException("已关注该品种");
        }
        UserWatchlist wl = new UserWatchlist();
        wl.setUserId(userId);
        wl.setHerbId(herbId);
        userWatchlistMapper.insert(wl);
        return Result.success();
    }

    @DeleteMapping("/watchlist/{id}")
    @Operation(summary = "取消关注")
    public Result<Void> removeWatchlist(@RequestHeader("X-User-Id") Long userId,
                                        @PathVariable Long id) {
        UserWatchlist wl = userWatchlistMapper.selectById(id);
        if (wl == null || !wl.getUserId().equals(userId)) {
            throw new BusinessException("关注记录不存在");
        }
        userWatchlistMapper.deleteById(id);
        return Result.success();
    }

    @GetMapping("/alerts")
    @Operation(summary = "预警列表")
    public Result<List<PriceAlertVO>> getAlerts(@RequestHeader("X-User-Id") Long userId) {
        return Result.success(priceAlertService.getUserAlerts(userId));
    }

    @PostMapping("/alerts")
    @Operation(summary = "创建预警")
    public Result<Long> createAlert(@RequestHeader("X-User-Id") Long userId,
                                    @RequestBody CreateAlertRequest body) {
        Long id = priceAlertService.createAlert(userId, body.getHerbId(),
                body.getConditionType(), body.getThreshold());
        return Result.success(id);
    }

    @DeleteMapping("/alerts/{id}")
    @Operation(summary = "删除预警")
    public Result<Void> deleteAlert(@RequestHeader("X-User-Id") Long userId,
                                    @PathVariable Long id) {
        priceAlertService.deleteAlert(id, userId);
        return Result.success();
    }

    @PutMapping("/alerts/{id}/toggle")
    @Operation(summary = "启用/禁用预警")
    public Result<Void> toggleAlert(@RequestHeader("X-User-Id") Long userId,
                                    @PathVariable Long id,
                                    @RequestBody ToggleAlertRequest body) {
        priceAlertService.toggleAlert(id, userId, body.getActive());
        return Result.success();
    }

    public static class CreateAlertRequest {
        private Long herbId;
        private Integer conditionType;
        private BigDecimal threshold;

        public Long getHerbId() {
            return herbId;
        }

        public void setHerbId(Long herbId) {
            this.herbId = herbId;
        }

        public Integer getConditionType() {
            return conditionType;
        }

        public void setConditionType(Integer conditionType) {
            this.conditionType = conditionType;
        }

        public BigDecimal getThreshold() {
            return threshold;
        }

        public void setThreshold(BigDecimal threshold) {
            this.threshold = threshold;
        }
    }

    public static class ToggleAlertRequest {
        private Boolean active;

        public Boolean getActive() {
            return active;
        }

        public void setActive(Boolean active) {
            this.active = active;
        }
    }
}

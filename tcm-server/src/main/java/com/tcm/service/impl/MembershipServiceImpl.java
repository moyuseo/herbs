package com.tcm.service.impl;

import com.tcm.common.BusinessException;
import com.tcm.entity.User;
import com.tcm.mapper.UserMapper;
import com.tcm.service.MembershipService;
import com.tcm.vo.MembershipVO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MembershipServiceImpl implements MembershipService {

    private final UserMapper userMapper;

    public MembershipServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public List<MembershipVO> getMembershipPlans() {
        List<MembershipVO> plans = new ArrayList<>();

        MembershipVO monthly = new MembershipVO();
        monthly.setLevel(1);
        monthly.setName("月度会员");
        monthly.setPrice(new BigDecimal("29.90"));
        monthly.setDurationDays(30);
        monthly.setFeatures(new String[]{
                "价格预警无限制",
                "历史价格数据导出",
                "市场行情深度分析",
                "专属客服支持"
        });
        plans.add(monthly);

        MembershipVO quarterly = new MembershipVO();
        quarterly.setLevel(2);
        quarterly.setName("季度会员");
        quarterly.setPrice(new BigDecimal("79.90"));
        quarterly.setDurationDays(90);
        quarterly.setFeatures(new String[]{
                "价格预警无限制",
                "历史价格数据导出",
                "市场行情深度分析",
                "专属客服支持",
                "产地直供对接",
                "价格走势预测"
        });
        plans.add(quarterly);

        MembershipVO yearly = new MembershipVO();
        yearly.setLevel(3);
        yearly.setName("年度会员");
        yearly.setPrice(new BigDecimal("299.00"));
        yearly.setDurationDays(365);
        yearly.setFeatures(new String[]{
                "价格预警无限制",
                "历史价格数据导出",
                "市场行情深度分析",
                "专属客服支持",
                "产地直供对接",
                "价格走势预测",
                "行业报告订阅",
                "API接口调用"
        });
        plans.add(yearly);

        return plans;
    }

    @Override
    public MembershipVO getCurrentMembership(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        MembershipVO vo = new MembershipVO();
        vo.setLevel(user.getMembershipLevel() != null ? user.getMembershipLevel() : 0);

        if (vo.getLevel() == 0) {
            vo.setName("免费用户");
            vo.setPrice(BigDecimal.ZERO);
            vo.setDurationDays(0);
            vo.setFeatures(new String[]{
                    "价格预警最多3条",
                    "基础行情查看",
                    "供求信息发布"
            });
        } else {
            List<MembershipVO> plans = getMembershipPlans();
            for (MembershipVO plan : plans) {
                if (plan.getLevel().equals(vo.getLevel())) {
                    vo.setName(plan.getName());
                    vo.setPrice(plan.getPrice());
                    vo.setDurationDays(plan.getDurationDays());
                    vo.setFeatures(plan.getFeatures());
                    break;
                }
            }
        }

        return vo;
    }

    @Override
    public void upgradeMembership(Long userId, Integer level) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (level == null || level < 1 || level > 3) {
            throw new BusinessException("无效的会员等级");
        }

        List<MembershipVO> plans = getMembershipPlans();
        MembershipVO targetPlan = null;
        for (MembershipVO plan : plans) {
            if (plan.getLevel().equals(level)) {
                targetPlan = plan;
                break;
            }
        }
        if (targetPlan == null) {
            throw new BusinessException("套餐不存在");
        }

        user.setMembershipLevel(level);
        user.setMembershipExpire(LocalDateTime.now().plusDays(targetPlan.getDurationDays()));
        user.setUpdatedAt(LocalDateTime.now());
        userMapper.updateById(user);
    }
}

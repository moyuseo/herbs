package com.tcm.controller;

import com.tcm.common.Result;
import com.tcm.service.MembershipService;
import com.tcm.vo.MembershipVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/membership")
@Tag(name = "会员体系")
public class MembershipController {

    private final MembershipService membershipService;

    public MembershipController(MembershipService membershipService) {
        this.membershipService = membershipService;
    }

    @GetMapping("/plans")
    @Operation(summary = "套餐列表")
    public Result<List<MembershipVO>> getPlans() {
        return Result.success(membershipService.getMembershipPlans());
    }

    @GetMapping("/current")
    @Operation(summary = "当前会员状态")
    public Result<MembershipVO> getCurrentMembership(@RequestHeader("X-User-Id") Long userId) {
        return Result.success(membershipService.getCurrentMembership(userId));
    }

    @PostMapping("/upgrade")
    @Operation(summary = "升级会员")
    public Result<Void> upgradeMembership(@RequestHeader("X-User-Id") Long userId,
                                          @RequestBody UpgradeRequest body) {
        membershipService.upgradeMembership(userId, body.getLevel());
        return Result.success();
    }

    public static class UpgradeRequest {
        private Integer level;

        public Integer getLevel() {
            return level;
        }

        public void setLevel(Integer level) {
            this.level = level;
        }
    }
}

package com.tcm.service;

import com.tcm.vo.MembershipVO;
import java.util.List;

public interface MembershipService {

    List<MembershipVO> getMembershipPlans();

    MembershipVO getCurrentMembership(Long userId);

    void upgradeMembership(Long userId, Integer level);
}

package com.kalafche.service.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.kalafche.service.ProtectPlusDiscountPolicyService;

@Component
public class ProtectPlusDiscountPolicyScheduler {

	@Autowired
	private ProtectPlusDiscountPolicyService protectPlusDiscountPolicyService;

	@Scheduled(cron = "0 10 0 * * *", zone = "Europe/Sofia")
	public void deactivateExpiredNonDefaultPolicies() {
		protectPlusDiscountPolicyService.deactivateExpiredNonDefaultPolicies();
	}
}

package com.kalafche.service.impl;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.kalafche.dao.DiscountDao;
import com.kalafche.exceptions.DuplicationException;
import com.kalafche.model.DiscountCampaign;
import com.kalafche.model.DiscountCode;
import com.kalafche.model.DiscountType;
import com.kalafche.service.DateService;
import com.kalafche.service.DiscountService;
import com.kalafche.service.EmployeeService;

@Service
public class DiscountServiceImpl implements DiscountService {

	@Autowired
	DiscountDao discountDao;
	
	@Autowired
	DateService dateService;
	
	@Autowired
	EmployeeService employeeService;
	
	@Override
	public DiscountCampaign createDiscountCampaign(DiscountCampaign discountCampaign) throws SQLException {
		discountCampaign.setCreateTimestamp(dateService.getCurrentMillisBGTimezone());
		discountCampaign.setCreatedByEmployeeId(employeeService.getLoggedInEmployee().getId());
		
		Integer discountCampaignId = discountDao.insertDiscountCampaign(discountCampaign);
		discountCampaign.setId(discountCampaignId);
		
		return discountCampaign;
	}

	@Override
	public DiscountCampaign getDiscountCampaign(Integer discountCampaignId) {
		return discountDao.selectDiscountCampaignById(discountCampaignId);
	}

	@Override
	public List<DiscountType> getDiscountTypes() {
		return discountDao.selectDiscountTypes();
	}

	@Override
	public DiscountCode createDiscountCode(DiscountCode discountCode) throws SQLException {
		validateDiscountCode(discountCode);
		Integer discountCodeId = discountDao.insertDiscountCode(discountCode);
		discountCode.setId(discountCodeId);
		
		return discountCode;
	}

	@Override
	public List<DiscountCampaign> getAllDiscountCampaigns() {
		return discountDao.selectAllDiscountCampaigns();
	}

	@Override
	public void updateDiscountCampaign(DiscountCampaign discountCampaign) {
		if (discountCampaign.getCode() != null && discountCampaign.getCode() != ""
				&& !StringUtils.isEmpty(discountCampaign.getDiscountValue()) && discountCampaign.getDiscountValue() != "0") {
			discountDao.updateDiscountCampaign(discountCampaign);
		}
	}

	@Override
	public void updateDiscountCode(DiscountCode discountCode) {
		discountDao.updateDiscountCode(discountCode);
	}
	
	private void validateDiscountCode(DiscountCode discountCode) {
		if (discountDao.checkIfDiscountCodeExists(discountCode)) {
			throw new DuplicationException("code", "Съществуващ код.");
		}
	}

	@Override
	public List<DiscountCode> getDiscountCodes() {
		return discountDao.selectAllDiscountCodes();
	}

	@Override
	public DiscountCode getDiscountCode(Integer code) {
		return discountDao.selectDiscountCode(code);
	}

	@Override
	public List<DiscountCode> getAvailableDiscountCodesForPartnerCampaign() {
		return discountDao.selectAvailableDiscountCodesForPartnerCampaign();
	}

	@Override
	public List<DiscountCode> getAvailableDiscountCodesForLoyalCampaign() {
		return discountDao.selectAvailableDiscountCodesForLoyalCampaign();
	}

}

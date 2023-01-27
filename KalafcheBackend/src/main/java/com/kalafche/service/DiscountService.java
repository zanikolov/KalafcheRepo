package com.kalafche.service;

import java.sql.SQLException;
import java.util.List;

import com.kalafche.model.DiscountCampaign;
import com.kalafche.model.DiscountCode;
import com.kalafche.model.DiscountType;

public interface DiscountService {

	DiscountCampaign createDiscountCampaign(DiscountCampaign discountCampaign) throws SQLException;

	DiscountCampaign getDiscountCampaign(Integer discountCampaignId);

	List<DiscountType> getDiscountTypes();

	DiscountCode createDiscountCode(DiscountCode discountCode) throws SQLException;

	List<DiscountCampaign> getAllDiscountCampaigns();

	void updateDiscountCampaign(DiscountCampaign discountCampaign);

	void updateDiscountCode(DiscountCode discountCode);

	List<DiscountCode> getDiscountCodes();

	DiscountCode getDiscountCode(Integer code);

	List<DiscountCode> getAvailableDiscountCodesForPartnerCampaign();
	
	List<DiscountCode> getAvailableDiscountCodesForLoyalCampaign();

}

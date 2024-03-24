package com.kalafche.dao;

import java.sql.SQLException;
import java.util.List;

import com.kalafche.model.discount.DiscountCampaign;
import com.kalafche.model.discount.DiscountCode;
import com.kalafche.model.discount.DiscountType;

public interface DiscountDao {

	Integer insertDiscountCampaign(DiscountCampaign discountCampaign) throws SQLException;

	DiscountCampaign selectDiscountCampaignById(Integer discountCampaignId);

	List<DiscountType> selectDiscountTypes();

	Integer insertDiscountCode(DiscountCode discountCode) throws SQLException;

	DiscountCode selectDiscountCode(Integer discountCode);

	List<DiscountCampaign> selectAllDiscountCampaigns();

	void updateDiscountCampaign(DiscountCampaign discountCampaign);

	Boolean checkIfDiscountCodeExists(DiscountCode discountCode);

	void updateDiscountCode(DiscountCode discountCode);

	List<DiscountCode> selectAllDiscountCodes();

	List<DiscountCode> selectAvailableDiscountCodesForPartnerCampaign();

	List<DiscountCode> selectAvailableDiscountCodesForLoyalCampaign();

}

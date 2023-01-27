package com.kalafche.dao;

import java.util.List;

import com.kalafche.model.PhoneHomeProduct;

public interface PhoneHomeProductDao {

	List<PhoneHomeProduct> getPhoneHomeProductsByCode(int code);

	List<PhoneHomeProduct> getPhoneHomeProductsForSaleByBrand(int code);

}

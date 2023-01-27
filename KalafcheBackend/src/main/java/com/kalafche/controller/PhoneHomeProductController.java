package com.kalafche.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kalafche.dao.PhoneHomeProductDao;
import com.kalafche.model.PhoneHomeProduct;
import com.kalafche.model.PhoneHomeProductList;

@CrossOrigin
@RestController
@RequestMapping({ "/service/phoneHomeProduct" })
public class PhoneHomeProductController {

//	@Autowired
//	PhoneHomeProductDao phoneHomeProductDao;
//	
//	@RequestMapping(value = { "/getPhoneHomeProducts" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
//	public PhoneHomeProductList getPhoneHomeProducts() {
//		int code = 0;
//		
//		List<PhoneHomeProduct> product = this.phoneHomeProductDao.getPhoneHomeProductsByCode(code);
//		PhoneHomeProductList productList = new PhoneHomeProductList();
//		
//		productList.setProduct(product);
//		
//		return productList;
//	}
//	
//	@RequestMapping(value = { "/getPhoneHomeProductsForSaleByBrand" }, method = { org.springframework.web.bind.annotation.RequestMethod.GET })
//	public PhoneHomeProductList getPhoneHomeProductsForSaleByBrand() {
//		int code = 0;
//		
//		List<PhoneHomeProduct> product = this.phoneHomeProductDao.getPhoneHomeProductsForSaleByBrand(code);
//		PhoneHomeProductList productList = new PhoneHomeProductList();
//		
//		productList.setProduct(product);
//		
//		return productList;
//	}
	
}



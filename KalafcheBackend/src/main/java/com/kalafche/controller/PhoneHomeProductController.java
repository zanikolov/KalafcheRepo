package com.kalafche.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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



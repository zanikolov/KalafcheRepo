package com.kalafche.service;

import java.util.List;

import com.kalafche.model.Product;
import com.kalafche.model.ProductMasterType;
import com.kalafche.model.ProductSpecificPrice;
import com.kalafche.model.ProductType;

public interface ProductService {

	List<Product> getAllProducts();

	void submitProduct(Product product);

	void updateProduct(Product product);

	Product getProduct(String code);

	List<ProductSpecificPrice> getProductSpecificPrice(Integer productId);

	List<ProductType> getProductTypes();

	void submitProductType(ProductType productType);

	void updateProductType(ProductType productType);

	ProductSpecificPrice getProductSpecificPrice(Integer productId, Integer storeId);

	List<ProductMasterType> getProductMasterTypes();

}

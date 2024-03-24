package com.kalafche.service;

import java.util.List;

import com.kalafche.model.product.Product;
import com.kalafche.model.product.ProductMasterType;
import com.kalafche.model.product.ProductSpecificPrice;
import com.kalafche.model.product.ProductType;

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

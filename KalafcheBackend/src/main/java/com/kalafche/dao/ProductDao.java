package com.kalafche.dao;

import java.util.List;

import com.kalafche.model.Product;
import com.kalafche.model.ProductMasterType;
import com.kalafche.model.ProductSpecificPrice;
import com.kalafche.model.ProductType;

public abstract interface ProductDao {
	
	public abstract List<Product> getAllProducts();

	public abstract void insertProduct(Product product);

	public abstract void updateProduct(Product product);

	public abstract Product getProduct(String code);

	public abstract boolean checkIfProductCodeExists(Product product);

	public abstract boolean checkIfProductNameExists(Product product);

	public abstract List<ProductSpecificPrice> getProductSpecificPrice(Integer productId);

	public abstract void updateProductSpecificPrice(ProductSpecificPrice specificPrice);

	public abstract void deleteProductSpecificPrices(Integer productId);

	public abstract List<ProductType> getAllProductTypes();

	public abstract boolean checkIfProductTypeNameExists(ProductType productType);

	public abstract void insertProductType(ProductType productType);

	public abstract void updateProductType(ProductType productType);

	public abstract ProductSpecificPrice getProductSpecificPrice(Integer productId, Integer storeId);

	public abstract List<ProductMasterType> getAllProductMasterTypes();
	
}

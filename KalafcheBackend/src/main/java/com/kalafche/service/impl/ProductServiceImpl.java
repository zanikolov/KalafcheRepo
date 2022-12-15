package com.kalafche.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kalafche.dao.ProductDao;
import com.kalafche.exceptions.DuplicationException;
import com.kalafche.model.Product;
import com.kalafche.model.ProductMasterType;
import com.kalafche.model.ProductSpecificPrice;
import com.kalafche.model.ProductType;
import com.kalafche.service.ProductService;
import com.kalafche.service.StockService;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	ProductDao productDao;
	
	@Autowired
	StockService stockService;
	
	@Override
	public List<Product> getAllProducts() {
		return productDao.getAllProducts();
	}

	@Override
	@Transactional
	public void submitProduct(Product product) {
		validateProductCode(product);
		validateProductName(product);
		productDao.insertProduct(product);
	}

	@Override
	@Transactional
	public void updateProduct(Product product) {
		validateProductCode(product);
		validateProductName(product);
		productDao.updateProduct(product);
		productDao.deleteProductSpecificPrices(product.getId());
		product.getSpecificPrices().forEach(specificPrice -> productDao.updateProductSpecificPrice(specificPrice));
	}

	@Override
	public Product getProduct(String code) {
		return productDao.getProduct(code);
	}
	
	@Override
	public List<ProductSpecificPrice> getProductSpecificPrice(Integer productId) {
		return productDao.getProductSpecificPrice(productId);
	}
	
	private void validateProductCode(Product product) {
		if (productDao.checkIfProductCodeExists(product)) {
			throw new DuplicationException("code", "Code duplication.");
		}
	}
	
	private void validateProductName(Product product) {
		if (productDao.checkIfProductNameExists(product)) {
			throw new DuplicationException("name", "Name duplication.");
		}
	}

	@Override
	public List<ProductType> getProductTypes() {
		return productDao.getAllProductTypes();
	}
	
	@Override
	public List<ProductMasterType> getProductMasterTypes() {
		return productDao.getAllProductMasterTypes();
	}

	@Override
	@Transactional
	public void submitProductType(ProductType productType) {
		validateProductTypeName(productType);
		productDao.insertProductType(productType);
	}

	@Override
	@Transactional
	public void updateProductType(ProductType productType) {
		validateProductTypeName(productType);
		productDao.updateProductType(productType);
	}
	
	private void validateProductTypeName(ProductType productType) {
		if (productDao.checkIfProductTypeNameExists(productType)) {
			throw new DuplicationException("name", "Name duplication.");
		}
	}
	
	@Override
	public ProductSpecificPrice getProductSpecificPrice(Integer productId, Integer storeId) {
		return productDao.getProductSpecificPrice(productId, storeId);
	}

}

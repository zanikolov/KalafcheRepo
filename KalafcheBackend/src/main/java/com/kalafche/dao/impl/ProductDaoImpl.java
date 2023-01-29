package com.kalafche.dao.impl;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Service;

import com.kalafche.dao.ProductDao;
import com.kalafche.model.Product;
import com.kalafche.model.ProductMasterType;
import com.kalafche.model.ProductSpecificPrice;
import com.kalafche.model.ProductType;

@Service
public class ProductDaoImpl extends JdbcDaoSupport implements ProductDao {
	
	private static final String GET_ALL_PRODUCTS_QUERY = "select p.id, p.name, p.code, p.price, p.purchase_price, p.fabric, p.type_id, pt.name as type_name, pmt.id as master_type_id, pmt.name as master_type_name from product p left join product_type pt on p.type_id = pt.id left join product_master_type pmt on pmt.id = pt.master_type_id order by code desc";
	private static final String INSERT_PRODUCT = "insert into product(name, code, description, price, purchase_price, fabric, type_id) values (?, ?, ?, ?, ?, ?, ?)";
	private static final String UPDATE_PRODUCT = "update product set name = ?, description = ?, fabric = ?, price = ?, purchase_price = ?, type_id = ? where id = ?";
	private static final String GET_PRODUCT_BY_CODE_QUERY = "select * from product where code = ?";
	private static final String CHECK_IF_PRODUCT_NAME_EXISTS = "select count(*) from product where name = ?";
	private static final String CHECK_IF_PRODUCT_CODE_EXISTS = "select count(*) from product where code = ?";
	private static final String ID_CLAUSE = " and id <> ?";
	private static final String GET_PRODUCT_SPECIFIC_PRICE = "select " +
			"? as product_id, " +
			"psp.specific_price as price, " +
			"concat(ks.city, ', ', ks.name) as store_name, " +
			"ks.id as store_id " +
			"from store ks " +
			"left join product_specific_price psp on psp.store_id = ks.id " +
			"and psp.product_id = ? " +
			"where ks.is_store is true ";
	private static final String STORE_ID_CLAUSE = "and ks.id = ? ";
	private static final String UPSERT_PRODUCT_SPECIFIC_PRICE = "insert into product_specific_price "
			+ "(product_id, store_id, specific_price) values "
			+ "(?, ?, ?) "
			+ "on duplicate key update specific_price = ?";
	
	private static final String DELETE_PRODUCT_SPECIFIC_PRICE = "delete from product_specific_price "
			+ " where product_id = ? ";
	
	private static final String GET_ALL_PRODUCT_TYPES = "select pt.id as id, pt.name as name, pmt.id as master_type_id, pmt.name as master_type_name from product_type pt left join product_master_type pmt on pt.master_type_id = pmt.id order by pt.id asc";
	private static final String GET_ALL_PRODUCT_MASTER_TYPES = "select * from product_master_type order by id asc";
	private static final String CHECK_IF_PRODUCT_TYPE_NAME_EXISTS = "select count(*) from product_type where name = ?";
	private static final String INSERT_PRODUCT_TYPE = "insert into product_type(name, master_type_id) values (?, ?)";
	private static final String UPDATE_PRODUCT_TYPE = "update product_type set name = ?, master_type_id = ? where id = ?";
	
	private BeanPropertyRowMapper<Product> productRowMapper;
	private BeanPropertyRowMapper<ProductSpecificPrice> specificPriceRowMapper;
	private BeanPropertyRowMapper<ProductType> productTypeRowMapper;
	private BeanPropertyRowMapper<ProductMasterType> productMasterTypeRowMapper;
	
	@Autowired
	public ProductDaoImpl(DataSource dataSource) {
		super();
		setDataSource(dataSource);
	}
	
	private BeanPropertyRowMapper<Product> getProductRowMapper() {
		if (productRowMapper == null) {
			productRowMapper = new BeanPropertyRowMapper<Product>(Product.class);
			productRowMapper.setPrimitivesDefaultedForNullValue(true);
		}
		
		return productRowMapper;
	}

	private BeanPropertyRowMapper<ProductSpecificPrice> getSpecificPriceRowMapper() {
		if (specificPriceRowMapper == null) {
			specificPriceRowMapper = new BeanPropertyRowMapper<ProductSpecificPrice>(ProductSpecificPrice.class);
			specificPriceRowMapper.setPrimitivesDefaultedForNullValue(true);
		}

		return specificPriceRowMapper;
	}
	
	private BeanPropertyRowMapper<ProductType> getProductTypeRowMapper() {
		if (productTypeRowMapper == null) {
			productTypeRowMapper = new BeanPropertyRowMapper<ProductType>(ProductType.class);
			productTypeRowMapper.setPrimitivesDefaultedForNullValue(true);
		}

		return productTypeRowMapper;
	}
	
	private BeanPropertyRowMapper<ProductMasterType> getProductMasterTypeRowMapper() {
		if (productMasterTypeRowMapper == null) {
			productMasterTypeRowMapper = new BeanPropertyRowMapper<ProductMasterType>(ProductMasterType.class);
			productMasterTypeRowMapper.setPrimitivesDefaultedForNullValue(true);
		}
		
		return productMasterTypeRowMapper;
	}
	
	@Override
	public List<Product> getAllProducts() {
		return getJdbcTemplate().query(GET_ALL_PRODUCTS_QUERY, getProductRowMapper());
	}

	@Override
	public void insertProduct(Product product) {
		getJdbcTemplate().update(INSERT_PRODUCT, product.getName(), product.getCode(), product.getDescription(), product.getPrice(), product.getPurchasePrice(), product.getFabric(), product.getTypeId());	
	}
	
	@Override
	public void updateProduct(Product product) {
		getJdbcTemplate().update(UPDATE_PRODUCT, product.getName(), product.getDescription(), product.getFabric(), product.getPrice(), product.getPurchasePrice(), product.getTypeId(), product.getId());
	}

	@Override
	public Product getProduct(String code) {
		List<Product> products = getJdbcTemplate().query(GET_PRODUCT_BY_CODE_QUERY, getProductRowMapper(), code);
		return products.isEmpty() ? null : products.get(0);
	}

	@Override
	public boolean checkIfProductCodeExists(Product product) {
		Integer exists = null;
		if (product.getId() == null) {
			exists = getJdbcTemplate().queryForObject(CHECK_IF_PRODUCT_CODE_EXISTS, Integer.class, product.getCode());
		} else {
			exists = getJdbcTemplate().queryForObject(CHECK_IF_PRODUCT_CODE_EXISTS + ID_CLAUSE, Integer.class, product.getCode(), product.getId());
		}
			
		return exists != null && exists > 0 ;
	}
	
	@Override
	public boolean checkIfProductNameExists(Product product) {
		Integer exists = null;
		if (product.getId() == null) {
			exists = getJdbcTemplate().queryForObject(CHECK_IF_PRODUCT_NAME_EXISTS, Integer.class, product.getName());
		} else {
			exists = getJdbcTemplate().queryForObject(CHECK_IF_PRODUCT_NAME_EXISTS + ID_CLAUSE, Integer.class, product.getName(), product.getId());
		}
		
		return exists != null && exists > 0 ;
	}

	@Override
	public List<ProductSpecificPrice> getProductSpecificPrice(Integer productId) {
		return getJdbcTemplate().query(GET_PRODUCT_SPECIFIC_PRICE, getSpecificPriceRowMapper(), productId, productId);
	}

	@Override
	public void updateProductSpecificPrice(ProductSpecificPrice specificPrice) {
		getJdbcTemplate().update(UPSERT_PRODUCT_SPECIFIC_PRICE, 
				specificPrice.getProductId(), specificPrice.getStoreId(), specificPrice.getPrice(), specificPrice.getPrice());
	}
	
	@Override
	public void deleteProductSpecificPrices(Integer productId) {
		getJdbcTemplate().update(DELETE_PRODUCT_SPECIFIC_PRICE, productId);
	}

	@Override
	public List<ProductType> getAllProductTypes() {
		return getJdbcTemplate().query(GET_ALL_PRODUCT_TYPES, getProductTypeRowMapper());
	}
	
	@Override
	public List<ProductMasterType> getAllProductMasterTypes() {
		return getJdbcTemplate().query(GET_ALL_PRODUCT_MASTER_TYPES, getProductMasterTypeRowMapper());
	}

	@Override
	public boolean checkIfProductTypeNameExists(ProductType productType) {
		Integer exists = null;
		if (productType.getId() == null) {
			exists = getJdbcTemplate().queryForObject(CHECK_IF_PRODUCT_TYPE_NAME_EXISTS, Integer.class, productType.getName());
		} else {
			exists = getJdbcTemplate().queryForObject(CHECK_IF_PRODUCT_TYPE_NAME_EXISTS + ID_CLAUSE, Integer.class, productType.getName(), productType.getId());
		}
		
		return exists != null && exists > 0 ;
	}

	@Override
	public void insertProductType(ProductType productType) {
		getJdbcTemplate().update(INSERT_PRODUCT_TYPE, productType.getName(), productType.getMasterTypeId());	
	}

	@Override
	public void updateProductType(ProductType productType) {
		getJdbcTemplate().update(UPDATE_PRODUCT_TYPE, productType.getName(), productType.getMasterTypeId(), productType.getId());
	}

	@Override
	public ProductSpecificPrice getProductSpecificPrice(Integer productId, Integer storeId) {
		List<ProductSpecificPrice> result = getJdbcTemplate().query(GET_PRODUCT_SPECIFIC_PRICE + STORE_ID_CLAUSE, getSpecificPriceRowMapper(), productId, productId, storeId);
		
		return result.isEmpty() ? null : result.get(0);
	}
	
}

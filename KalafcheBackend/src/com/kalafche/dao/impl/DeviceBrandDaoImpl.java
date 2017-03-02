package com.kalafche.dao.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.kalafche.dao.AbstractDao;
import com.kalafche.dao.DeviceBrandDao;
import com.kalafche.model.device.DeviceBrand;

@Repository
@Transactional
public class DeviceBrandDaoImpl extends AbstractDao  implements DeviceBrandDao {
	
    @Autowired
    private SessionFactory sessionFactory;	
	
	@SuppressWarnings("unchecked")
	public List<DeviceBrand> getAllBrands() {		
		return sessionFactory.getCurrentSession().createCriteria(DeviceBrand.class).list();
	}

	@Override
	public void insertBrand(DeviceBrand brand) {
		save(brand);
	}
}

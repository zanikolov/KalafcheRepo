package com.kalafche.service.impl;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kalafche.dao.MonthlyScheduleDao;
import com.kalafche.model.MonthlySchedule;
import com.kalafche.service.MonthlyScheduleService;

@Service
public class MonthlyScheduleServiceImpl implements MonthlyScheduleService {

	@Autowired
	MonthlyScheduleDao monthlyScheduleDao;
	
	@Override
	public MonthlySchedule generateMonthlySchedule(Integer storeId, Integer month, Integer year) throws SQLException {
		MonthlySchedule monthlySchedule = new MonthlySchedule();
		monthlySchedule.setStoreId(storeId);
		monthlySchedule.setMonth(month);
		monthlySchedule.setYear(year);
		monthlySchedule.setId(monthlyScheduleDao.insertMonthlySchedule(monthlySchedule));
		
		return monthlySchedule;
	}

	@Override
	public MonthlySchedule getMonthlySchedule(Integer storeId, Integer month, Integer year) {
		return monthlyScheduleDao.getMonthlySchedule(storeId, month, year);
	}

}

package com.kalafche.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kalafche.dao.LoginHistoryDao;
import com.kalafche.model.LoginHistory;
import com.kalafche.service.DateService;
import com.kalafche.service.LoginHistoryService;

@Service
public class LoginHistoryServiceImpl implements LoginHistoryService {
	
	@Autowired
	LoginHistoryDao loginHistoryDao;
	
	@Autowired
	DateService dateService;
	
	@Override
	public void trackLoginHistory(int employeeId) {	
		long loginTimestamp = dateService.getCurrentMillisBGTimezone();
		loginHistoryDao.insertLoginHistoryRecord(employeeId, loginTimestamp);	
	}

	@Override
	public List<LoginHistory> getLoginHistoryRecords(long dateMillis) {
		return loginHistoryDao.selectFirstLoginForDate(dateMillis);
	}

}

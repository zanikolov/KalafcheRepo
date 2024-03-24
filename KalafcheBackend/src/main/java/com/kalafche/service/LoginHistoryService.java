package com.kalafche.service;

import java.util.List;

import com.kalafche.model.employee.LoginHistory;

public interface LoginHistoryService {

	void trackLoginHistory(int employeeId);
	
	List<LoginHistory> getLoginHistoryRecords(long dateMillis);
	
}

package com.kalafche.dao;

import java.util.List;

import com.kalafche.model.employee.LoginHistory;

public interface LoginHistoryDao {
	
	void insertLoginHistoryRecord(int employeeId, long loginTimestamp);
	
	List<LoginHistory> selectFirstLoginForDate(long dateMillis);

}

package com.kalafche.service.impl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kalafche.dao.ItemDao;
import com.kalafche.dao.StoreDao;
import com.kalafche.dao.WasteDao;
import com.kalafche.model.Waste;
import com.kalafche.model.WasteReport;
import com.kalafche.model.employee.Employee;
import com.kalafche.service.DateService;
import com.kalafche.service.EmployeeService;
import com.kalafche.service.EntityService;
import com.kalafche.service.StockService;
import com.kalafche.service.WasteService;
import com.kalafche.service.fileutil.ImageUploadService;

@Service
public class WasteServiceImpl implements WasteService {

	@Autowired
	WasteDao wasteDao;
	
	@Autowired
	ItemDao itemDao;
	
	@Autowired
	EntityService entityService;
	
	@Autowired
	EmployeeService employeeService;
	
	@Autowired
	DateService dateService;
	
	@Autowired
	StockService stockService;
	
	@Autowired
	ImageUploadService imageUploadService;
	
	@Autowired
	StoreDao storeDao;
	
	@Override
	public WasteReport searchWastes(Long startDateMilliseconds, Long endDateMilliseconds, String storeIds,
			String productCode, Integer deviceBrandId, Integer deviceModelId) {
		WasteReport wasteReport = new WasteReport();
		
		List<Waste> wastes = wasteDao.searchWastes(startDateMilliseconds, endDateMilliseconds,
				entityService.getConcatenatedStoreIdsForFiltering(storeIds), productCode, deviceBrandId, deviceModelId);
		
		if (wastes != null && !wastes.isEmpty()) {
			calculateTotalAmountAndCount(wastes, wasteReport);
		} else {
			wasteReport.setCount(0);
			wasteReport.setTotalAmount(BigDecimal.ZERO);
		}	
		wasteReport.setWastes(wastes);
		
		return wasteReport;
	}

	private void calculateTotalAmountAndCount(List<Waste> wastes, WasteReport wasteReport) {
		BigDecimal totalAmount = wastes.stream()
		        .map(waste -> waste.getPrice())
		        .reduce(BigDecimal.ZERO, BigDecimal::add);
		
		wasteReport.setCount(wastes.size());
		wasteReport.setTotalAmount(totalAmount);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void submitWaste(Waste waste, MultipartFile wasteImage) throws SQLException {
		String fileId = imageUploadService.uploadWasteImage(wasteImage);
		Employee loggedInEmployee = employeeService.getLoggedInEmployee();
		waste.setEmployeeId(loggedInEmployee.getId());
		waste.setStoreId(loggedInEmployee.getStoreId());
		waste.setTimestamp(dateService.getCurrentMillisBGTimezone());
		waste.setPrice(itemDao.getItemPriceByStoreId(waste.getItemId(), waste.getStoreId()));
		waste.setFileId(fileId);
		
		wasteDao.insertWaste(waste);
		stockService.updateTheQuantitiyOfSoldStock(waste.getItemId(), loggedInEmployee.getStoreId());
	}

}

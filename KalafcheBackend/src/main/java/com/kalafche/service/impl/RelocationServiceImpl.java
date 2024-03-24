package com.kalafche.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kalafche.dao.RelocationDao;
import com.kalafche.dao.impl.StockDaoImpl;
import com.kalafche.enums.RelocationStatus;
import com.kalafche.model.NewStock;
import com.kalafche.model.Relocation;
import com.kalafche.service.DateService;
import com.kalafche.service.EmployeeService;
import com.kalafche.service.EntityService;
import com.kalafche.service.ItemService;
import com.kalafche.service.RelocationService;

@Service
public class RelocationServiceImpl implements RelocationService {


	@Autowired
	DateService dateService;
	
	@Autowired
	EmployeeService employeeService;
	
	@Autowired
	EntityService entityService;
	
	@Autowired
	ItemService itemService;
	
	@Autowired
	StockDaoImpl stockDao;
	
	@Autowired
	RelocationDao relocationDao;

	@Transactional
	@Override
	public void submitRelocation(Relocation relocation) {
		relocation.setRelocationRequestTimestamp(dateService.getCurrentMillisBGTimezone());
		relocation.setEmployeeId(employeeService.getLoggedInEmployee().getId());
		relocation.setStatus(RelocationStatus.INITIATED);
		
		this.relocationDao.insertRelocation(relocation);
		this.stockDao.updateQuantityOfApprovedStock(relocation.getItemId(), relocation.getSourceStoreId(), relocation.getQuantity() * (-1));
	}
	
	@Transactional
	@Override
	public void changeRelocationStatus(Relocation relocation) {
		this.relocationDao.updateRelocationStatus(relocation.getId(), relocation.getStatus());

		if (relocation.getStatus().equals(RelocationStatus.REJECTED)) {
			processCompletedRelocation(relocation.getId(), relocation.getQuantity(), relocation.getItemId(),
					relocation.getSourceStoreId());
		} else if (relocation.getStatus().equals(RelocationStatus.DELIVERED)) {
			processCompletedRelocation(relocation.getId(), relocation.getQuantity(), relocation.getItemId(),
					relocation.getDestStoreId());
		}
	}

	private void processCompletedRelocation(int relocationId, int quantity, int itemId, int storeId) {
		this.relocationDao.updateRelocationCompleteTimestamp(relocationId, dateService.getCurrentMillisBGTimezone());
		stockDao.insertOrUpdateQuantityOfInStock(itemId, storeId, quantity);
	}

	@Override
	public void archiveRelocation(Integer stockRelocationId) {
		relocationDao.archiveStockRelocation(stockRelocationId);	
	}

	@Override
	public List<Relocation> getRelocations(Integer sourceStoreId, Integer destStoreId, boolean isCompleted) {
		return relocationDao.getRelocations(sourceStoreId, destStoreId, isCompleted);
	}

	@Override
	public void generateRelocationsForNewStock(List<NewStock> newStocks) {
		for (NewStock newStock : newStocks) {
			for (int i = 0; i < newStock.getQuantity(); i++) {
				Relocation relocation = new Relocation();
				relocation.setRelocationRequestTimestamp(dateService.getCurrentMillisBGTimezone());
				relocation.setEmployeeId(employeeService.getLoggedInEmployee().getId());
				relocation.setStatus(RelocationStatus.SENT);
				relocation.setItemId(newStock.getItemId());
				relocation.setQuantity(1);
				relocation.setSourceStoreId(entityService.getStoreByCode("RU_WH").getId());
				relocation.setDestStoreId(newStock.getStoreId());
				
				relocationDao.insertRelocation(relocation);
			}
		}		
	}

}

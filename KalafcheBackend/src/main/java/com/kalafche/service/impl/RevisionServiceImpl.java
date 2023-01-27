package com.kalafche.service.impl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.kalafche.dao.RevisionDao;
import com.kalafche.exceptions.DomainObjectNotFoundException;
import com.kalafche.model.DeviceModel;
import com.kalafche.model.Employee;
import com.kalafche.model.Item;
import com.kalafche.model.ProductSpecificPrice;
import com.kalafche.model.Revision;
import com.kalafche.model.RevisionItem;
import com.kalafche.model.RevisionType;
import com.kalafche.service.DateService;
import com.kalafche.service.DeviceModelService;
import com.kalafche.service.EmployeeService;
import com.kalafche.service.ItemService;
import com.kalafche.service.ProductService;
import com.kalafche.service.RevisionService;
import com.kalafche.service.StockService;

@Transactional
@Service
public class RevisionServiceImpl implements RevisionService {

	@Autowired
	RevisionDao revisionDao;
	
	@Autowired
	DeviceModelService deviceModelService;
	
	@Autowired
	StockService stockService;
	
	@Autowired
	DateService dateService;
	
	@Autowired
	EmployeeService employeeService;

	@Autowired
	ItemService itemService;
	
	@Autowired
	ProductService productService;
	
	@Transactional
	@Override
	public Revision initiateRevision(Revision revision) throws SQLException {
		Employee currentEmployee = employeeService.getLoggedInEmployee();
		if (!employeeService.isLoggedInEmployeeAdmin()) {
			revision.setStoreId(currentEmployee.getStoreId());
		}
		revision.setCreateTimestamp(dateService.getCurrentMillisBGTimezone());
		revision.setCreatedByEmployeeId(currentEmployee.getId());
		
		Integer revisionId = revisionDao.insertRevision(revision);
		
		revisionDao.insertRevisers(revisionId, revision.getRevisers());
		List<DeviceModel> deviceModels = createRevisionDeviceModels(revisionId, revision.getTypeId(), revision.getStoreId());
		List<RevisionItem> revisionItems = createRevisionItems(revisionId, revision.getStoreId(), deviceModels);
		String revisionTypeCode = revisionDao.selectRevisionTypeCode(revision.getTypeId());
		
		revision.setId(revisionId);
		revision.setTypeCode(revisionTypeCode);
		revision.setDeviceModels(deviceModels);
		revision.setRevisionItems(revisionItems);
		
		return revision;
	}

	private List<RevisionItem> createRevisionItems(Integer revisionId, Integer storeId, List<DeviceModel> deviceModels) {
		List<RevisionItem> revisionItems = revisionDao.getItemsForRevision(storeId, deviceModels);
		revisionDao.insertRevisionItems(revisionId, revisionItems);
		
		return revisionDao.getRevisionItemByRevisionId(revisionId, false);
	}

	private List<DeviceModel> createRevisionDeviceModels(Integer revisionId, Integer revisionTypeId, Integer storeId) {
		List<Integer> deviceModelIds = Lists.newArrayList();
		
		if ("DAILY".equals(revisionDao.selectRevisionTypeCode(revisionTypeId))) {
			Integer lastRevisedDeviceModelId = revisionDao.getLastDeviceIdFromLastRevisionByStoreId(storeId);
			deviceModelIds = deviceModelService.getDeviceModelIdsForDailyRevision(lastRevisedDeviceModelId, 10);
			
			//in case we reach the end of the device models' table, we start from the beginning
			if (deviceModelIds.size() < 10) {
				deviceModelIds.addAll(deviceModelService.getDeviceModelIdsForDailyRevision(0, 10 - deviceModelIds.size()));
			}
		} else {
			deviceModelIds = deviceModelService.getDeviceModelIdsForFullRevision();
		}
		
		revisionDao.insertRevisionDeviceModels(revisionId, deviceModelIds);
		
		return deviceModelService.getDeviceModelsByIds(deviceModelIds);
	}

	@Override
	public Revision getRevision(Integer revisionId) {
		Revision revision = revisionDao.getRevision(revisionId);
		populateRevisionDataInfo(revision, true);
		
		return revision;
	}

	@Override
	public Revision getCurrentRevision(Integer storeId) {
		Revision currentRevision = null;
		if (employeeService.isLoggedInEmployeeAdmin()) {
			currentRevision = revisionDao.getCurrentRevision(storeId);
		} else {
			Employee currentEmployee = employeeService.getLoggedInEmployee();
			currentRevision = revisionDao.getCurrentRevision(currentEmployee.getStoreId());
		}
		
		if (currentRevision == null) {
			return null;
		}
		
		populateRevisionDataInfo(currentRevision, false);
		
		return currentRevision;
	}

	private void populateRevisionDataInfo(Revision revision, Boolean onlyMismatches) {
		Integer revisionId = revision.getId();
		
		List<RevisionItem> revisionItems = revisionDao.getRevisionItemByRevisionId(revisionId, onlyMismatches);
		revision.setRevisionItems(revisionItems);
		
		List<Employee> revisers = getRevisers(revisionId);
		revision.setRevisers(revisers);
		
		List<DeviceModel> deviceModels = getRevisionDeviceModels(revisionId);	
		revision.setDeviceModels(deviceModels);
	}

	private List<DeviceModel> getRevisionDeviceModels(Integer revisionId) {
		List<Integer> deviceModelIds = revisionDao.getDeviceModelIdByRevisionId(revisionId);
		List<DeviceModel> deviceModels = deviceModelService.getDeviceModelsByIds(deviceModelIds);
		return deviceModels;
	}

	private List<Employee> getRevisers(Integer revisionId) {
		List<Integer> reviserEmployeeIds = revisionDao.getReviserEmployeeIds(revisionId);
		List<Employee> revisers = employeeService.getEmployeesByIds(reviserEmployeeIds);
		return revisers;
	}

	@Override
	public List<RevisionType> getRevisionTypes() {
		return revisionDao.getAllRevisionTypes();
	}

	@Override
	public RevisionItem getRevisionItemByBarcode(Integer revisionId, String barcode) {
		RevisionItem revisionItem = revisionDao.selectRevisionItemByBarcode(revisionId, barcode);

		if (revisionItem == null) {
			Item item = itemService.getItemByBarcode(barcode);

			if (item != null) {
				Revision revision = revisionDao.getRevision(revisionId);
				ProductSpecificPrice specificItemPriceForStore = productService.getProductSpecificPrice(item.getProductId(), revision.getStoreId());
				if (specificItemPriceForStore != null && specificItemPriceForStore.getPrice() != null
						&& specificItemPriceForStore.getPrice() != BigDecimal.ZERO) {
					item.setProductPrice(specificItemPriceForStore.getPrice());
				}

				revisionItem = new RevisionItem(revisionId, item, 0, 0);
				List<Integer> deviceModelIds = revisionDao.getDeviceModelIdByRevisionId(revisionId);
				if (!deviceModelIds.contains(revisionItem.getDeviceModelId())) {
					revisionItem.setPartOfTheCurrentRevision(false);
				}
				
			} else {
				throw new DomainObjectNotFoundException("barcode", "Unexisting item.");
			}
		}

		return revisionItem;
	}

	@Override
	public Integer findRevisionItem(RevisionItem revisionItem) throws SQLException {
		Integer revisionItemId = revisionItem.getId();
		if (revisionItemId != null) {
			revisionDao.updateRevisionItemActual(revisionItemId, 1);
		} else {
			List<Integer> deviceModelIds = revisionDao.getDeviceModelIdByRevisionId(revisionItem.getRevisionId());
			if (deviceModelIds.contains(revisionItem.getDeviceModelId())) {
				revisionItem.setActual(1);
				revisionItemId = revisionDao.insertNonExpectedRevisionItem(revisionItem);	
			} else {
				throw new DomainObjectNotFoundException("deviceModelId", "This item is not part of the revision.");
			}
		}
		
		return revisionItemId;
	}

	@Transactional
	@Override
	public Revision submitRevision(Revision revision) {
		List<RevisionItem> revisionItems = revisionDao.getRevisionItemByRevisionId(revision.getId(), false);
		
		Integer totalActual = 0;
		Integer totalExpected = 0;
		BigDecimal totalActualAmount = BigDecimal.ZERO;
		BigDecimal totalExpectedAmount = BigDecimal.ZERO;
		for (RevisionItem revisionItem : revisionItems) {
			totalActual += revisionItem.getActual();
			totalExpected += revisionItem.getExpected();
			
			BigDecimal actualAmount = revisionItem.getProductPrice().multiply(new BigDecimal(revisionItem.getActual()));
			totalActualAmount = totalActualAmount.add(actualAmount);
			System.out.println(">>>> act: " + actualAmount);
			
			BigDecimal expectedAmount = revisionItem.getProductPrice().multiply(new BigDecimal(revisionItem.getExpected()));
			totalExpectedAmount = totalExpectedAmount.add(expectedAmount);
			System.out.println(">>>> exp: " + expectedAmount);
		}		
		BigDecimal totalAmount = totalActualAmount.subtract(totalExpectedAmount);
		
		revision.setSubmitTimestamp(dateService.getCurrentMillisBGTimezone());
		revision.setUpdatedByEmployeeId(employeeService.getLoggedInEmployee().getId());
		revision.setTotalActual(totalActual);
		revision.setTotalExpected(totalExpected);
		revision.setBalance(totalAmount);
	
		syncRevisionItemsActualWithStockQuantities(revision);
		revision.setActualSynced(true);
		revisionDao.submitRevision(revision);
		
		return revision;
	}

	private void syncRevisionItemsActualWithStockQuantities(Revision revision) {
		List<RevisionItem> mismatchedRevisionItems = revisionDao.getRevisionItemByRevisionId(revision.getId(), true);
		revisionDao.syncRevisionItemsActualWithStockQuantities(revision.getStoreId(), mismatchedRevisionItems);
	}

	@Override
	public List<Revision> searchRevisions(Long startDateMilliseconds, Long endDateMilliseconds, Integer storeId) {
		return revisionDao.selectRevisions(startDateMilliseconds, endDateMilliseconds, storeId);
	}

	@Override
	public Integer correctionOfItemQuantityAfterRevision(RevisionItem revisionItem) {
		RevisionItem persistedRevisionItem = revisionDao.getRevisionItemById(revisionItem.getId());
		if (persistedRevisionItem != null && !persistedRevisionItem.getSynced()) {
			Integer difference = revisionItem.getActual() - revisionItem.getExpected();
			stockService.updateTheQuantitiyOfRevisedStock(revisionItem.getItemId(), revisionItem.getRevisionId(), difference);
		}
		return null;
	}

}

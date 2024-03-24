package com.kalafche.service.impl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.kalafche.dao.RevisionDao;
import com.kalafche.exceptions.CommonException;
import com.kalafche.exceptions.DomainObjectNotFoundException;
import com.kalafche.exceptions.DuplicationException;
import com.kalafche.exceptions.NegativeItemQuantityException;
import com.kalafche.model.device.DeviceModel;
import com.kalafche.model.employee.Employee;
import com.kalafche.model.product.Item;
import com.kalafche.model.product.ProductSpecificPrice;
import com.kalafche.model.revision.MissingRevisionItem;
import com.kalafche.model.revision.Revision;
import com.kalafche.model.revision.RevisionItem;
import com.kalafche.model.revision.RevisionReport;
import com.kalafche.model.revision.RevisionType;
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
	public Revision initiateRevision(Revision revision) throws SQLException, CommonException {
		if (getCurrentRevision(revision.getStoreId()) != null) {
			throw new DuplicationException("currentRevision",
					String.format("The last revision for store with id %s is not finalized", revision.getStoreId()));
		}
		Employee currentEmployee = employeeService.getLoggedInEmployee();
		if (!employeeService.isLoggedInEmployeeAdmin() && !employeeService.isLoggedInEmployeeManager()) {
			revision.setStoreId(currentEmployee.getStoreId());
		}
		RevisionType revisionType = revisionDao.selectRevisionTypeByCode(revision.getTypeCode());
		revision.setTypeId(revisionType.getId());
		revision.setCreateTimestamp(dateService.getCurrentMillisBGTimezone());
		revision.setCreatedByEmployeeId(currentEmployee.getId());
		revision.setActualSynced(false);
		revision.setIsFinalized(false);	
		Integer revisionId = revisionDao.insertRevision(revision);
		revision.setId(revisionId);
		
		
		revisionDao.insertRevisers(revisionId, revision.getRevisers());
		List<DeviceModel> deviceModels = createRevisionDeviceModels(revision, revisionType.getStrategy(), revision.getStoreId());
		List<RevisionItem> revisionItems = createRevisionItems(revision, deviceModels);
			
		revision.setTypeCode(revisionType.getCode());
		revision.setDeviceModels(deviceModels);
		revision.setRevisionItems(revisionItems);
		
		return revision;
	}

	private List<RevisionItem> createRevisionItems(Revision revision, List<DeviceModel> deviceModels) {
		String productCode = null;
		if ("CHECK".equals(revision.getTypeCode()) && deviceModels.size() == 1) {
			productCode = revision.getProductCode();
		}
		List<RevisionItem> revisionItems = revisionDao.getItemsForRevision(revision.getStoreId(), deviceModels, productCode);
		revisionDao.insertRevisionItems(revision.getId(), revisionItems);
		
		return revisionDao.getRevisionItemsByRevisionId(revision.getId(), false);
	}

	private List<DeviceModel> createRevisionDeviceModels(Revision revision, String revisionTypeStrategy,
			Integer storeId) throws CommonException {
		List<Integer> deviceModelIds = Lists.newArrayList();

		switch (revisionTypeStrategy) {
		case "CONSECUTIVE":
			deviceModelIds = chooseConsecutiveDeviceModelIds(storeId);
			break;
		case "ALL":
			deviceModelIds = deviceModelService.getAllDeviceModelIds();
			break;
		case "RANDOM":
			deviceModelIds = chooseRandomDeviceModelIds(revision.getStoreId());
			break;
		case "SELECTED":
			deviceModelIds = revision.getDeviceModels().stream().map(deviceModel -> deviceModel.getId())
					.collect(Collectors.toList());
			break;
		default:
			throw new CommonException(String.format("Revision Strategy %s is not supported", revisionTypeStrategy));
		}

		revisionDao.insertRevisionDeviceModels(revision.getId(), deviceModelIds);

		return deviceModelService.getDeviceModelsByIds(deviceModelIds);
	}

	private List<Integer> chooseRandomDeviceModelIds(Integer storeId) {
		List<Integer> deviceModelIds = deviceModelService.getAllDeviceModelIds();
		List<Integer> deviceModelIdsFromLastRevisions = revisionDao.getDeviceModelIdsFromLastRevisionByStore(storeId, 30);

		deviceModelIds.removeAll(deviceModelIdsFromLastRevisions);

		Random rand = new Random();
		List<Integer> randomChoosedDeviceModelIds = new ArrayList<Integer>();
		int randomIndex;
		for (int i = 0; i < 10; i++) {
			randomIndex = rand.nextInt(deviceModelIds.size());
			randomChoosedDeviceModelIds.add(deviceModelIds.get(randomIndex));
			deviceModelIds.remove(randomIndex);
		}

		return randomChoosedDeviceModelIds;
	}

	private List<Integer> chooseConsecutiveDeviceModelIds(Integer storeId) {
		List<Integer> deviceModelIds;
		Integer lastRevisedDeviceModelId = revisionDao.getLastDeviceIdFromLastRevisionByStoreId(storeId);
		deviceModelIds = deviceModelService.getDeviceModelIdsForDailyRevision(lastRevisedDeviceModelId, 10);
		
		//in case we reach the end of the device models' table, we start from the beginning
		if (deviceModelIds.size() < 10) {
			deviceModelIds.addAll(deviceModelService.getDeviceModelIdsForDailyRevision(0, 10 - deviceModelIds.size()));
		}
		return deviceModelIds;
	}

	@Override
	public Revision getRevision(Integer revisionId) {
		Revision revision = revisionDao.getRevision(revisionId);
		populateRevisionDataInfo(revision, true);
		
		return revision;
	}

	@Override
	public Revision getCurrentRevision(Integer storeId) {
		if (!employeeService.isLoggedInEmployeeAdmin() && !employeeService.isLoggedInEmployeeManager()) {
			Employee currentEmployee = employeeService.getLoggedInEmployee();
			storeId = currentEmployee.getStoreId();
		}
		Revision currentRevision = revisionDao.getCurrentRevision(storeId);
		
		if (currentRevision != null) {
			populateRevisionDataInfo(currentRevision, false);
		}
				
		return currentRevision;
	}

	private void populateRevisionDataInfo(Revision revision, Boolean onlyMismatches) {
		Integer revisionId = revision.getId();
		
		List<Employee> revisers = getRevisers(revisionId);
		revision.setRevisers(revisers);
		
		List<DeviceModel> deviceModels = getRevisionDeviceModels(revisionId);	
		revision.setDeviceModels(deviceModels);
		
		List<RevisionItem> revisionItems = revisionDao.getRevisionItemsByRevisionId(revisionId, onlyMismatches);
		revision.setRevisionItems(revisionItems);

		if (onlyMismatches) {
			for (RevisionItem item : revisionItems) {
				if (item.getActual() > item.getExpected()) {
					List<MissingRevisionItem> missingRevisionItems = revisionDao
							.findLastRevisionTheItemIsMissing(item.getItemId(), revision.getStoreId(), dateService.getTodayInMillis(-30).getStartDateTime());
					String shortageInfo = missingRevisionItems.stream().map(missingItem -> {
						String date = dateService.convertMillisToDateTimeString(missingItem.getRevisionDate(), "dd-MM-yyyy", false);
						return  date + ": " + missingItem.getMissingCount();
					}).collect(Collectors.joining(", "));
					
					item.setShortageInfo(shortageInfo);
				}
			}
		}
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
	public Integer updateRevisionItem(RevisionItem revisionItem, int actualChange) throws SQLException {
		Integer revisionItemId = revisionItem.getId();
		if (revisionItemId != null) {
			if (revisionItem.getActual() + actualChange < 0) {
				throw new NegativeItemQuantityException("actual", "The actual count of the revision item could not be less than zero.");
			}
			revisionDao.updateRevisionItemActual(revisionItemId, actualChange);
		} else {
			List<Integer> deviceModelIds = revisionDao.getDeviceModelIdByRevisionId(revisionItem.getRevisionId());
			if (!deviceModelIds.contains(revisionItem.getDeviceModelId())) {
				throw new DomainObjectNotFoundException("deviceModelId", "This item is not part of the revision.");
			}
			revisionItem.setActual(1);
			revisionItemId = revisionDao.insertNonExpectedRevisionItem(revisionItem);
		}

		return revisionItemId;
	}

	@Transactional
	@Override
	public Revision finalizeRevision(Revision revision) {
		List<RevisionItem> revisionItems = revisionDao.getRevisionItemsByRevisionId(revision.getId(), false);
		
		BigDecimal shortageAmount = BigDecimal.ZERO;
		Integer shortageCount = 0;
		BigDecimal surplusAmount = BigDecimal.ZERO;
		Integer surplusCount = 0;
		for (RevisionItem revisionItem : revisionItems) {
			int discrepancy = revisionItem.getActual() - revisionItem.getExpected();
			
			if (discrepancy < 0) {
				shortageCount += discrepancy;
				shortageAmount = shortageAmount.add(revisionItem.getProductPrice().multiply(new BigDecimal(discrepancy)));
			}
			
			if (discrepancy > 0) {
				surplusCount += discrepancy;
				surplusAmount = surplusAmount.add(revisionItem.getProductPrice().multiply(new BigDecimal(discrepancy)));
			}
		}
		
		revision.setShortageAmount(shortageAmount);
		revision.setShortageCount(shortageCount);
		revision.setSurplusAmount(surplusAmount);
		revision.setSurplusCount(surplusCount);
		revision.setAbsoluteAmountBalance(surplusAmount.add(shortageAmount.abs()));
		revision.setAbsoluteCountBalance(surplusCount + Math.abs(shortageCount));
		revision.setTotalAmount(surplusAmount.add(shortageAmount));
		
		Long currentMillis = dateService.getCurrentMillisBGTimezone();
		revision.setLastUpdateTimestamp(currentMillis);
		revision.setSubmitTimestamp(currentMillis);
		revision.setUpdatedByEmployeeId(employeeService.getLoggedInEmployee().getId());
		
		syncRevisionItemsActualWithStockQuantities(revision);
		revision.setIsFinalized(true);
		revision.setActualSynced(true);
		revisionDao.finalizeRevision(revision);
		
		return revision;
	}

	public void syncRevisionItemsActualWithStockQuantities(Revision revision) {
		List<RevisionItem> mismatchedRevisionItems = revisionDao.getRevisionItemsByRevisionId(revision.getId(), true);
		revisionDao.syncRevisionItemsActualWithStockQuantities(revision.getId(), revision.getStoreId(), mismatchedRevisionItems);
	}

	@Override
	public RevisionReport searchRevisions(Long startDateMilliseconds, Long endDateMilliseconds, Integer storeId, Integer typeId) {
		List<Revision> revisions = revisionDao.selectRevisions(startDateMilliseconds, endDateMilliseconds, storeId, typeId);
		RevisionReport report = new RevisionReport();
		
		report.setRevisions(revisions);
		report.setStartDate(startDateMilliseconds);
		report.setEndDate(endDateMilliseconds);
		
		BigDecimal totalShortageAmount = BigDecimal.ZERO;
		Integer totalShortageCount = 0;
		BigDecimal totalSurplusAmount = BigDecimal.ZERO;
		Integer totalSurplusCount = 0;
		BigDecimal totalAbsoluteAmountBalance = BigDecimal.ZERO;
		BigDecimal totalAmount = BigDecimal.ZERO;
		Integer totalAbsoluteCountBalance = 0;
		for (Revision revision : revisions) {
			totalShortageAmount = totalShortageAmount.add(revision.getShortageAmount());
			totalShortageCount += revision.getShortageCount();
			totalSurplusAmount = totalSurplusAmount.add(revision.getSurplusAmount());
			totalSurplusCount += revision.getSurplusCount();
			totalAbsoluteAmountBalance = totalAbsoluteAmountBalance.add(revision.getAbsoluteAmountBalance());
			totalAbsoluteCountBalance += revision.getAbsoluteCountBalance();
			totalAmount = totalAmount.add(revision.getTotalAmount());
		}
		
		report.setTotalShortageAmount(totalShortageAmount);
		report.setTotalShortageCount(totalShortageCount);
		report.setTotalSurplusAmount(totalSurplusAmount);
		report.setTotalSurplusCount(totalSurplusCount);
		report.setTotalAbsoluteAmountBalance(totalAbsoluteAmountBalance);
		report.setTotalAbsoluteCountBalance(totalAbsoluteCountBalance);
		report.setTotalAmount(totalAmount);
		
		return report;
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

	@Override
	public List<RevisionItem> getRevisionItemsByRevisionId(Integer revisionId) {
		return revisionDao.getRevisionItemsByRevisionId(revisionId, false);
	}

}

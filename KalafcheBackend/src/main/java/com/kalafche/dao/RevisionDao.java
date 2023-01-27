package com.kalafche.dao;

import java.sql.SQLException;
import java.util.List;

import com.kalafche.model.DeviceModel;
import com.kalafche.model.Employee;
import com.kalafche.model.Revision;
import com.kalafche.model.RevisionItem;
import com.kalafche.model.RevisionType;

public interface RevisionDao {

	Integer insertRevision(Revision revision) throws SQLException;

	Revision getRevision(Integer revisionId);
	
	Integer getLastDeviceIdFromLastRevisionByStoreId(Integer storeId);

	void insertRevisionDeviceModels(Integer id, List<Integer> deviceModelIds);

	List<RevisionItem> getItemsForRevision(Integer storeId, List<DeviceModel> deviceModels);

	void insertRevisionItems(Integer revisionId, List<RevisionItem> revisionItems);

	List<RevisionItem> getRevisionItemByRevisionId(Integer revisionId, Boolean onlyMismatches);

	void insertRevisers(Integer revisionId, List<Employee> revisers);

	String selectRevisionTypeCode(Integer revisionTypeId);

	Revision getCurrentRevision(Integer storeId);

	List<Integer> getReviserEmployeeIds(Integer revisionId);

	List<Integer> getDeviceModelIdByRevisionId(Integer revisionId);

	List<RevisionType> getAllRevisionTypes();

	RevisionItem selectRevisionItemByBarcode(Integer revisionId, String barcode);

	void updateRevisionItemActual(Integer revisionItemId, Integer actualIncrement);

	Integer insertNonExpectedRevisionItem(RevisionItem revisionItem) throws SQLException;

	void submitRevision(Revision revision);

	List<Revision> selectRevisions(Long startDateMilliseconds, Long endDateMilliseconds, Integer storeId);

	void syncRevisionItemsActualWithStockQuantities(Integer storeId, List<RevisionItem> mismatchedRevisionItems);

	RevisionItem getRevisionItemById(Integer revisionItemId);
	
}

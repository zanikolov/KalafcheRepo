package com.kalafche.dao;

import java.sql.SQLException;
import java.util.List;

import com.kalafche.model.device.DeviceModel;
import com.kalafche.model.employee.Employee;
import com.kalafche.model.revision.MissingRevisionItem;
import com.kalafche.model.revision.Revision;
import com.kalafche.model.revision.RevisionItem;
import com.kalafche.model.revision.RevisionType;

public interface RevisionDao {

	Integer insertRevision(Revision revision) throws SQLException;

	Revision getRevision(Integer revisionId);
	
	Integer getLastDeviceIdFromLastRevisionByStoreId(Integer storeId);

	void insertRevisionDeviceModels(Integer id, List<Integer> deviceModelIds);

	List<RevisionItem> getItemsForRevision(Integer storeId, List<DeviceModel> deviceModels, String productCode);

	void insertRevisionItems(Integer revisionId, List<RevisionItem> revisionItems);

	List<RevisionItem> getRevisionItemsByRevisionId(Integer revisionId, Boolean onlyMismatches);

	void insertRevisers(Integer revisionId, List<Employee> revisers);

	String selectRevisionTypeCode(Integer revisionTypeId);

	Revision getCurrentRevision(Integer storeId);

	List<Integer> getReviserEmployeeIds(Integer revisionId);

	List<Integer> getDeviceModelIdByRevisionId(Integer revisionId);

	List<RevisionType> getAllRevisionTypes();

	RevisionItem selectRevisionItemByBarcode(Integer revisionId, String barcode);

	void updateRevisionItemActual(Integer revisionItemId, Integer actualIncrement);

	Integer insertNonExpectedRevisionItem(RevisionItem revisionItem) throws SQLException;

	void finalizeRevision(Revision revision);

	List<Revision> selectRevisions(Long startDateMilliseconds, Long endDateMilliseconds, Integer storeId, Integer typeId);

	RevisionItem getRevisionItemById(Integer revisionItemId);

	RevisionType selectRevisionType(Integer typeId);

	List<Integer> getDeviceModelIdsFromLastRevisionByStore(Integer storeId, int pastRevisionsCount);

	void syncRevisionItemsActualWithStockQuantities(Integer revisionId, Integer storeId,
			List<RevisionItem> mismatchedRevisionItems);

	RevisionType selectRevisionTypeByCode(String typeCode);

	List<MissingRevisionItem> findLastRevisionTheItemIsMissing(Integer itemId, Integer storeId, Long submitDate);
	
}

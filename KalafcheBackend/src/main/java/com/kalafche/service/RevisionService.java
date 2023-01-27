package com.kalafche.service;

import java.sql.SQLException;
import java.util.List;

import com.kalafche.model.Revision;
import com.kalafche.model.RevisionItem;
import com.kalafche.model.RevisionType;

public interface RevisionService {

	Revision initiateRevision(Revision revision) throws SQLException;

	Revision getRevision(Integer revisionId);

	Revision getCurrentRevision(Integer storeId);

	List<RevisionType> getRevisionTypes();

	RevisionItem getRevisionItemByBarcode(Integer revisionId, String barcode);

	Integer findRevisionItem(RevisionItem revisionItem) throws SQLException;

	Revision submitRevision(Revision revision);

	List<Revision> searchRevisions(Long startDateMilliseconds, Long endDateMilliseconds, Integer storeId);

	Integer correctionOfItemQuantityAfterRevision(RevisionItem revisionItem);

}

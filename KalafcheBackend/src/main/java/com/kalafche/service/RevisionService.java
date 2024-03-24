package com.kalafche.service;

import java.sql.SQLException;
import java.util.List;

import com.kalafche.exceptions.CommonException;
import com.kalafche.model.revision.Revision;
import com.kalafche.model.revision.RevisionItem;
import com.kalafche.model.revision.RevisionReport;
import com.kalafche.model.revision.RevisionType;

public interface RevisionService {

	Revision initiateRevision(Revision revision) throws SQLException, CommonException;

	Revision getRevision(Integer revisionId);

	Revision getCurrentRevision(Integer storeId);

	List<RevisionType> getRevisionTypes();

	RevisionItem getRevisionItemByBarcode(Integer revisionId, String barcode);

	Revision finalizeRevision(Revision revision);

	RevisionReport searchRevisions(Long startDateMilliseconds, Long endDateMilliseconds, Integer storeId, Integer typeId);

	Integer correctionOfItemQuantityAfterRevision(RevisionItem revisionItem);

	Integer updateRevisionItem(RevisionItem revisionItem, int actualChange) throws SQLException;

	List<RevisionItem> getRevisionItemsByRevisionId(Integer revisionId);

}

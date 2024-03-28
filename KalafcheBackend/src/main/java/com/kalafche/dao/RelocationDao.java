package com.kalafche.dao;

import java.util.List;

import com.kalafche.enums.RelocationStatus;
import com.kalafche.model.Relocation;
import com.kalafche.model.invoice.InvoiceItem;

public abstract interface RelocationDao {
	
	public abstract List<Relocation> getRelocations(Integer sourceStoreId, Integer destStoreId, boolean isCompleted);

	public abstract void insertRelocation(Relocation stockRelocation);

	public abstract void archiveStockRelocation(Integer stockRelocationId);
	
	public abstract void updateRelocationStatus(Integer relocationId, RelocationStatus status);
	
	public abstract RelocationStatus getRelocationStatus(Integer relocationId);

	public abstract void updateRelocationCompleteTimestamp(Integer relocationId, long completeTimestamp);
	
	public abstract List<InvoiceItem> selectInvoiceItems(int recipientCompanyId, int issuerCompanyId, long startDate, long endDate);
	
}

package com.kalafche.dao;

import java.util.List;

import com.kalafche.enums.RelocationStatus;
import com.kalafche.model.Relocation;

public abstract interface RelocationDao {
	
	public abstract List<Relocation> getRelocations(Integer sourceStoreId, Integer destStoreId, boolean isCompleted);

	public abstract void insertRelocation(Relocation stockRelocation);

	public abstract void archiveStockRelocation(Integer stockRelocationId);
	
	public abstract void updateRelocationStatus(Integer relocationId, RelocationStatus status);
	
	public abstract RelocationStatus getRelocationStatus(Integer relocationId);

	public abstract void updateRelocationCompleteTimestamp(Integer relocationId, long completeTimestamp);
	
}

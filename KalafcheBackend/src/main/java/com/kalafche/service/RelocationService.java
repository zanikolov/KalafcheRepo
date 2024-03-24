package com.kalafche.service;

import java.util.List;

import com.kalafche.model.NewStock;
import com.kalafche.model.Relocation;

public interface RelocationService {

	public abstract void submitRelocation(Relocation relocation);

	public abstract void changeRelocationStatus(Relocation relocation);

	public abstract void archiveRelocation(Integer stockRelocationId);

	public abstract List<Relocation> getRelocations(Integer sourceStoreId, Integer destStoreId, boolean isCompleted);

	public abstract void generateRelocationsForNewStock(List<NewStock> newStock);
	
}

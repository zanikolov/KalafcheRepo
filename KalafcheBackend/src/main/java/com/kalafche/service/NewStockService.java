package com.kalafche.service;

import java.io.IOException;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.web.multipart.MultipartFile;

import com.kalafche.model.NewStock;
import com.kalafche.service.fileutil.ExcelItem;

public interface NewStockService {
	
	List<NewStock> getAllNewStocks();

	void deleteNewStock(Integer newStockId);

	void submitNewStockFromFile(MultipartFile newStockFile, Integer storeId) throws EncryptedDocumentException, InvalidFormatException, IOException;
	
	List<ExcelItem> validateNewStockFile(MultipartFile newStockFile) throws EncryptedDocumentException, InvalidFormatException, IOException;

	void approveNewStock(NewStock newStock);

	void approveNewStock(List<NewStock> newStocks);

	byte[] printNewStockStickers(Integer storeId);

	void submitNewStock(Integer productId, Integer deviceModelId, Integer quantity, Integer storeId);

	List<NewStock> getNewStockByStoreId(Integer storeId);

	void relocateNewStock(Integer storeId);

	byte[] printNewStockPartialStickers(Integer storeId);
	
}

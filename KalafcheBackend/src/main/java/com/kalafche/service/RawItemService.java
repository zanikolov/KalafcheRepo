package com.kalafche.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.kalafche.model.RawItem;

public interface RawItemService {

	List<RawItem> submitRawItemsFromFile(MultipartFile rawItemFile);

	void updateItemBarcode(Integer productId, Integer deviceModelId, String barcode);

}

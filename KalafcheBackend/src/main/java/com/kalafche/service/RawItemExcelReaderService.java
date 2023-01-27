package com.kalafche.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.kalafche.model.RawItem;

public interface RawItemExcelReaderService {

	List<RawItem> parseExcelData(MultipartFile rawItemFile);

}

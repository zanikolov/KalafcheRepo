package com.kalafche.service.fileutil;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public interface NewStockExcelReaderService {

	List<ExcelItem> parseExcelData(MultipartFile file);
}

package com.kalafche.service.fileutil;

import java.util.List;

import com.kalafche.model.Stock;

public interface InStockExcelReportService {

	byte[] generateExcel(List<Stock> stocks);
}

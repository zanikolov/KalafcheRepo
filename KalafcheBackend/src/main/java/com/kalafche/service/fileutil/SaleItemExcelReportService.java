package com.kalafche.service.fileutil;

import com.kalafche.model.SaleItemExcelReportRequest;

public interface SaleItemExcelReportService {

	byte[] generateExcel(SaleItemExcelReportRequest saleItemExcelReportRequest);
}

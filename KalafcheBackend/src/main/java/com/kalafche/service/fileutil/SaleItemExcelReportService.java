package com.kalafche.service.fileutil;

import com.kalafche.model.sale.SaleItemExcelReportRequest;

public interface SaleItemExcelReportService {

	byte[] generateExcel(SaleItemExcelReportRequest saleItemExcelReportRequest);
}

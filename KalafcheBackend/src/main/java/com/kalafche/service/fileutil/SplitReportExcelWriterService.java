package com.kalafche.service.fileutil;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.kalafche.model.SalesByStoreByDayByProductType;
import com.kalafche.model.TransactionsByStoreByDay;

public interface SplitReportExcelWriterService {

	public abstract byte[] createProductTypeSplitReportExcel(
			Map<String, LinkedHashMap<String, List<SalesByStoreByDayByProductType>>> splitReport) throws IOException;

	public abstract byte[] createTransactionSplitReportExcel(
			Map<String, LinkedHashMap<String, List<TransactionsByStoreByDay>>> splitReport) throws IOException;
}

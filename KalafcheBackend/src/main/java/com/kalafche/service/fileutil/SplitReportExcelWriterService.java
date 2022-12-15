package com.kalafche.service.fileutil;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.kalafche.model.SalesByStoreByDayByProductType;

public interface SplitReportExcelWriterService {

	public abstract byte[] createSplitReportExcel(Map<String, LinkedHashMap<String, List<SalesByStoreByDayByProductType>>> splitReport) throws IOException;
}

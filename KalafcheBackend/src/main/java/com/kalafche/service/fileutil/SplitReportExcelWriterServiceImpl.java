package com.kalafche.service.fileutil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.kalafche.model.SalesByStoreByDayByProductType;

@Service
public class SplitReportExcelWriterServiceImpl implements SplitReportExcelWriterService {

	@Override
	public byte[] createSplitReportExcel(
			Map<String, LinkedHashMap<String, List<SalesByStoreByDayByProductType>>> splitReport) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();    
         
        splitReport.keySet().stream().forEach(store -> {
        	int rowCount = 0;
        	XSSFSheet sheet = workbook.createSheet(store);
        	Map<String, List<SalesByStoreByDayByProductType>> byDayMap = splitReport.get(store);
        	
        	boolean headerCreated = false;
        	for (Entry<String, List<SalesByStoreByDayByProductType>> entry : byDayMap.entrySet()) {
        		if (!headerCreated) {
        			Row headerRow = sheet.createRow(rowCount++);
        			int headerColumnCount = 0;
        			Cell headerDayCell = headerRow.createCell(headerColumnCount++);
        			headerDayCell.setCellValue("Date");
            		for (SalesByStoreByDayByProductType soldItems : byDayMap.get(entry.getKey())) {
            			Cell cell = headerRow.createCell(++headerColumnCount);
            			if (!StringUtils.isEmpty(soldItems.getProductTypeName())) {
            				cell.setCellValue((soldItems.getProductTypeName()));
            			} else {
            				cell.setCellValue("");
            			}
            		}
            		headerCreated = true;
        		}
        		Row row = sheet.createRow(rowCount++);
        		        		
        		int columnCount = 0;
        		Cell dayCell = row.createCell(columnCount++);
        		dayCell.setCellValue((String) entry.getKey());
        		
        		for (SalesByStoreByDayByProductType soldItems : byDayMap.get(entry.getKey())) {
        			Cell cell = row.createCell(++columnCount);
        			if (!StringUtils.isEmpty(soldItems.getSoldItemsCount())) {
        				cell.setCellValue((Integer) soldItems.getSoldItemsCount());
        			} else {
        				cell.setCellValue(0);
        			}
        		}
        	}
        	
    	});        
         
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();) {
            workbook.write(outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {            
            workbook.close(); 
        }
	}

}

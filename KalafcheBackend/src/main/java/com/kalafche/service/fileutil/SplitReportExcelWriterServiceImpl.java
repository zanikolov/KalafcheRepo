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
import com.kalafche.model.TransactionsByStoreByDay;

@Service
public class SplitReportExcelWriterServiceImpl implements SplitReportExcelWriterService {

	@Override
	public byte[] createProductTypeSplitReportExcel(
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

	@Override
	public byte[] createTransactionSplitReportExcel(
			Map<String, LinkedHashMap<String, List<TransactionsByStoreByDay>>> splitReport) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook();    
        
        splitReport.keySet().stream().forEach(store -> {
        	int rowCount = 0;
        	XSSFSheet sheet = workbook.createSheet(store);
        	Map<String, List<TransactionsByStoreByDay>> byDayMap = splitReport.get(store);
        	
        	boolean headerCreated = false;
        	for (Entry<String, List<TransactionsByStoreByDay>> entry : byDayMap.entrySet()) {
        		if (!headerCreated) {
        			Row headerRow = sheet.createRow(rowCount++);
        			//int headerColumnCount = 0;
        			Cell headerDayCell = headerRow.createCell(0);
        			headerDayCell.setCellValue("Date");
        			Cell headerPrevYearIncomeCell = headerRow.createCell(1);
        			headerPrevYearIncomeCell.setCellValue("PrevYear Income");
        			Cell headerIncomeCell = headerRow.createCell(2);
        			headerIncomeCell.setCellValue("Income");
        			Cell headerSoldItemsCountPrevYearCell = headerRow.createCell(3);
        			headerSoldItemsCountPrevYearCell.setCellValue("PrevYear Sales");
        			Cell headerSoldItemsCountCell = headerRow.createCell(4);
        			headerSoldItemsCountCell.setCellValue("Sales");
        			Cell headerTransactionsCountPrevYearCell = headerRow.createCell(5);
        			headerTransactionsCountPrevYearCell.setCellValue("PrevYear Transactions");       			
        			Cell headerTransactionsCountCell = headerRow.createCell(6);
        			headerTransactionsCountCell.setCellValue("Transactions");       			
            		headerCreated = true;
        		}
        		Row row = sheet.createRow(rowCount++);
        		        		
        		Cell dayCell = row.createCell(0);
        		dayCell.setCellValue((String) entry.getKey());
        		
        		TransactionsByStoreByDay data = entry.getValue().get(0);
        		Cell incomePrevYearCell = row.createCell(1);
        		if (data.getTurnoverPrevYear() != null) {
        			incomePrevYearCell.setCellValue(data.getTurnoverPrevYear().doubleValue());
        		} else {
        			incomePrevYearCell.setCellValue(0);
        		}
        		Cell incomeCell = row.createCell(2);
        		if (data.getTurnover() != null) {
        			incomeCell.setCellValue(data.getTurnover().doubleValue());
        		} else {
        			incomeCell.setCellValue(0);
        		}
    			Cell soldItemsPrevYearCell = row.createCell(3);
    			if (data.getSoldItemsCountPrevYear() != null) {
    				soldItemsPrevYearCell.setCellValue((Integer) data.getSoldItemsCountPrevYear());
    			} else {
    				soldItemsPrevYearCell.setCellValue(0);
    			}
    			Cell soldItemsCell = row.createCell(4);
    			if (data.getSoldItemsCount() != null) {
    				soldItemsCell.setCellValue((Integer) data.getSoldItemsCount());
    			} else {
    				soldItemsCell.setCellValue(0);
    			}
    			Cell transactionsCellPrevYear = row.createCell(5);
    			if (data.getTransactionsCountPrevYear() != null) {
    				transactionsCellPrevYear.setCellValue((Integer) data.getTransactionsCountPrevYear());
    			} else {
    				transactionsCellPrevYear.setCellValue(0);
    			}
    			Cell transactionsCell = row.createCell(6);
    			if (data.getTransactionsCount() != null) {
    				transactionsCell.setCellValue((Integer) data.getTransactionsCount());
    			} else {
    				transactionsCell.setCellValue(0);
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

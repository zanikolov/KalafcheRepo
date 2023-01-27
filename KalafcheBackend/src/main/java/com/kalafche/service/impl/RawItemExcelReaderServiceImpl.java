package com.kalafche.service.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.kalafche.exceptions.ExcelInvalidFormatException;
import com.kalafche.model.RawItem;
import com.kalafche.service.RawItemExcelReaderService;

@Service
public class RawItemExcelReaderServiceImpl implements RawItemExcelReaderService {
	
	@Override
	public List<RawItem> parseExcelData(MultipartFile file) {
		List<RawItem> items = new ArrayList<>();

        Workbook workbook;
		try {
			workbook = WorkbookFactory.create(file.getInputStream());

	        Sheet worksheet = workbook.getSheetAt(0);
	        
	        worksheet.forEach(row -> {
	        	RawItem item = new RawItem();
	       	
	        	item.setName(getCellValue(row.getCell(0)));
	        	if (row.getCell(0) != null) {
		        	String barcode = getCellValue(row.getCell(1));
		        	try {
			        	BigDecimal barcodeDecimal = new BigDecimal(barcode);
			        	String barcodePlainString = barcodeDecimal.toPlainString();
			            item.setBarcode(barcodePlainString);
		        	} catch (NumberFormatException ex) {
		        		System.out.println(barcode);
		        	}
	        	}
	            
	            if (!StringUtils.isEmpty(item.getBarcode()) && !StringUtils.isEmpty(item.getName())) {
	            	items.add(item);
	            }
	        	
	        });
		} catch (IllegalStateException | InvalidFormatException | IOException e) {
			e.printStackTrace();
			throw new ExcelInvalidFormatException("file", "Invalid file content.");
		}
		
		return items;
	}
	
	private String getCellValue(Cell cell) {
		if (cell == null) {
			return "";
		}
	    if(cell.getCellTypeEnum() == CellType.STRING) {
            return cell.getStringCellValue();
	    } else {
            return cell.getNumericCellValue() + "";
	    }
	}
	
//	private static void printCellValue(Cell cell) {
//	    switch (cell.getCellTypeEnum()) {
//	        case STRING:
//	            System.out.print(cell.getColumnIndex() + " " + cell.getStringCellValue() + "    ");
//	            break;
//	        case NUMERIC:
//                System.out.print(cell.getColumnIndex() + " " + cell.getNumericCellValue() + "    ");
//	            break;
//	        case BLANK:
//	            System.out.print(cell.getColumnIndex() + " " + "^^    ");
//	            break;
//	        default:
//	            System.out.print(cell.getColumnIndex() + " " + "<>    ");
//	    }
//	}

}

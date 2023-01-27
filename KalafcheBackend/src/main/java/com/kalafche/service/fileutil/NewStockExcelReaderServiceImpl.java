package com.kalafche.service.fileutil;

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

@Service
public class NewStockExcelReaderServiceImpl implements NewStockExcelReaderService {

	@Override
	public List<ExcelItem> parseExcelData(MultipartFile file) {
		List<ExcelItem> items = new ArrayList<>();

        Workbook workbook;
		try {
			workbook = WorkbookFactory.create(file.getInputStream());

	        Sheet worksheet = workbook.getSheetAt(0);
	        
	        worksheet.forEach(row -> {
	        	ExcelItem item = new ExcelItem();
	
	        	row.forEach(cell -> {
	        		printCellValue(cell);
	        	});
	        	System.out.println();
	        	
	        	if (!StringUtils.isEmpty(row.getCell(0))) {
		            item.setBarcode(new BigDecimal(row.getCell(0).toString()).toPlainString());
		            item.setName(getCellValue(row.getCell(2)));
		            item.setQuantity((int)Double.parseDouble(getCellValue(row.getCell(8))));
		            items.add(item);
	        	}
	        	
	        });
		} catch (IllegalStateException | InvalidFormatException | IOException e) {
			e.printStackTrace();
			throw new ExcelInvalidFormatException("file", "The excel contains invalid data.");
		}
		
		return items;
	}
	
	private String getCellValue(Cell cell) {
	    if(cell.getCellTypeEnum() == CellType.STRING) {
            return cell.getStringCellValue();
	    } else {
            return cell.getNumericCellValue() + "";
	    }
	}
	
	private static void printCellValue(Cell cell) {
	    switch (cell.getCellTypeEnum()) {
	        case STRING:
	            System.out.print(cell.getColumnIndex() + " " + cell.getStringCellValue() + "    ");
	            break;
	        case NUMERIC:
                System.out.print(cell.getColumnIndex() + " " + cell.getNumericCellValue() + "    ");
	            break;
	        case BLANK:
	            System.out.print(cell.getColumnIndex() + " " + "^^    ");
	            break;
	        default:
	            System.out.print(cell.getColumnIndex() + " " + "<>    ");
	    }
	}

}

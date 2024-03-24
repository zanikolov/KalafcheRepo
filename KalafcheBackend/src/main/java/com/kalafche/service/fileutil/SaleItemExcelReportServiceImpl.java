package com.kalafche.service.fileutil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kalafche.model.sale.SaleItem;
import com.kalafche.model.sale.SaleItemExcelReportRequest;
import com.kalafche.service.DateService;

@Service
public class SaleItemExcelReportServiceImpl implements SaleItemExcelReportService {

	@Autowired
	private DateService dateService;

	@Override
	public byte[] generateExcel(SaleItemExcelReportRequest saleItemExcelReportRequest) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(String.format("Продажби",
				dateService.convertMillisToDateTimeString(saleItemExcelReportRequest.getStartDate(), "dd-MM-yyyy", false),
				dateService.convertMillisToDateTimeString(saleItemExcelReportRequest.getEndDate(), "dd-MM-yyyy", false)));

		int rowNum = 0;		
		for (SaleItem saleItem : saleItemExcelReportRequest.getSaleItems()) {
			Row row = sheet.createRow(rowNum++);
			int colNum = 0;

			Cell deviceCell = row.createCell(colNum++);
			deviceCell.setCellValue((String) saleItem.getDeviceModelName());

			Cell productCodeCell = row.createCell(colNum++);
			productCodeCell.setCellValue((String) saleItem.getProductCode());

			Cell productNameCell = row.createCell(colNum++);
			productNameCell.setCellValue((String) saleItem.getProductName());
			
			Cell productTypeNameCell = row.createCell(colNum++);
			productTypeNameCell.setCellValue((String) saleItem.getProductTypeName());
			
			Cell productMasterTypeNameCell = row.createCell(colNum++);
			productMasterTypeNameCell.setCellValue((String) saleItem.getProductMasterTypeName());

			Cell storeNameCell = row.createCell(colNum++);
			storeNameCell.setCellValue((String) saleItem.getStoreName());

			Cell employeeNameCell = row.createCell(colNum++);
			employeeNameCell.setCellValue((String) saleItem.getEmployeeName());

			Cell saleTimestampCell = row.createCell(colNum++);
			saleTimestampCell
					.setCellValue((String) dateService.convertMillisToDateTimeString(saleItem.getSaleTimestamp(), "dd-MM-yyyy", true));

			Cell itemPriceCell = row.createCell(colNum++);
			itemPriceCell.setCellValue((String) saleItem.getItemPrice().toPlainString());
			
			Cell salePriceCell = row.createCell(colNum++);
			salePriceCell.setCellValue((String) saleItem.getSalePrice().toPlainString());
		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			workbook.write(baos);
			workbook.close();
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return baos.toByteArray();
		
	}

}

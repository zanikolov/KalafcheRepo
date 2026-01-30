package com.kalafche.service.fileutil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kalafche.model.sale.SaleItem;
import com.kalafche.model.sale.SaleItemExcelReportRequest;
import com.kalafche.service.DateService;

@Service
public class SaleItemExcelReportServiceImpl implements SaleItemExcelReportService {

	@Autowired
	private DateService dateService;
	
	private static SXSSFWorkbook workbook;
	private static Font calibriFont;

	@Override
	public byte[] generateExcel(SaleItemExcelReportRequest saleItemExcelReportRequest) {
		workbook = new SXSSFWorkbook();
		SXSSFSheet sheet = workbook.createSheet(String.format("Продажби",
				dateService.convertMillisToDateTimeString(saleItemExcelReportRequest.getStartDate(), "dd-MM-yyyy", false),
				dateService.convertMillisToDateTimeString(saleItemExcelReportRequest.getEndDate(), "dd-MM-yyyy", false)));
		
		createHeader(sheet); 
		
		int rowNum = 1;
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
			
			Cell discountCampaignCell = row.createCell(colNum++);
			discountCampaignCell.setCellValue((String) saleItem.getDiscountCampaignCode());
			
			Cell bonusPtsCell = row.createCell(colNum++);
			bonusPtsCell.setCellValue((String) saleItem.getBonusPts().toString());
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

	private void createHeader(SXSSFSheet sheet) {
		CellStyle headerCellStyle = initializeHeaderStyle();
		
		int rowNum = 0;
		Row headerRow = sheet.createRow(rowNum++);
		Cell headerDeviceCell = headerRow.createCell(0);
		headerDeviceCell.setCellValue("Device");
		headerDeviceCell.setCellStyle(headerCellStyle);
		Cell headerProductCodeCell = headerRow.createCell(1);
		headerProductCodeCell.setCellValue("Product Code");
		headerProductCodeCell.setCellStyle(headerCellStyle);
		Cell headerProductNameCell = headerRow.createCell(2);
		headerProductNameCell.setCellValue("Product Name");
		headerProductNameCell.setCellStyle(headerCellStyle);
		Cell headerProductTypeNameCell = headerRow.createCell(3);
		headerProductTypeNameCell.setCellValue("Product Type");
		headerProductTypeNameCell.setCellStyle(headerCellStyle);
		Cell headerProductMasterTypeNameCell = headerRow.createCell(4);
		headerProductMasterTypeNameCell.setCellValue("Product Master Type");
		headerProductMasterTypeNameCell.setCellStyle(headerCellStyle);
		Cell headerStoreNameCell = headerRow.createCell(5);
		headerStoreNameCell.setCellValue("Store");
		headerStoreNameCell.setCellStyle(headerCellStyle);
		Cell headerEmployeeNameCell = headerRow.createCell(6);
		headerEmployeeNameCell.setCellValue("Employee");
		headerEmployeeNameCell.setCellStyle(headerCellStyle);
		Cell headerSaleTimestampCell = headerRow.createCell(7);
		headerSaleTimestampCell.setCellValue("Sale Timestamp");
		headerSaleTimestampCell.setCellStyle(headerCellStyle);
		Cell headerItemPriceCell = headerRow.createCell(8);
		headerItemPriceCell.setCellValue("Item Price");
		headerItemPriceCell.setCellStyle(headerCellStyle);
		Cell headerSalePriceCell = headerRow.createCell(9);
		headerSalePriceCell.setCellValue("Sale Price");
		headerSalePriceCell.setCellStyle(headerCellStyle);
		Cell headerDiscountCampaignCell = headerRow.createCell(10);
		headerDiscountCampaignCell.setCellValue("Discount Campaign");
		headerDiscountCampaignCell.setCellStyle(headerCellStyle);
		Cell headerBonusPtsCell = headerRow.createCell(11);
		headerBonusPtsCell.setCellValue("Bonus Points");
		headerBonusPtsCell.setCellStyle(headerCellStyle);
	}
	
	private static CellStyle initializeHeaderStyle() {
		CellStyle headerCellStyle = initializeDefaultCellWithBorderStyle();
		headerCellStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
		headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		setBorder(headerCellStyle);
		
		return headerCellStyle;
	}

	private  static CellStyle initializeDefaultCellWithBorderStyle() {
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		setBorder(cellStyle);
		cellStyle.setFont(calibriFont);
		
		return cellStyle;
	}
	
	private static void setBorder(CellStyle cellStyle) {
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
	}
	
}

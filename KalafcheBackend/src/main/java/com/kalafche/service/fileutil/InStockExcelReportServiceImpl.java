package com.kalafche.service.fileutil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;

import com.kalafche.model.Stock;

@Service
public class InStockExcelReportServiceImpl implements InStockExcelReportService {

	private static final String[] HEADERS = {
			"#",
			"Модел",
			"Продукт",
			"Тип",
			"Магазин",
			"Цена",
			"Брой",
			"Поръчани",
			"Склад"
	};

	private static final int[] COLUMN_WIDTHS = { 8, 35, 45, 28, 28, 12, 10, 12, 22 };

	@Override
	public byte[] generateExcel(List<Stock> stocks) {
		SXSSFWorkbook workbook = new SXSSFWorkbook();

		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			SXSSFSheet sheet = workbook.createSheet("Наличности");
			CellStyle headerStyle = createHeaderStyle(workbook);
			createHeader(sheet, headerStyle);
			setColumnWidths(sheet);

			int rowIndex = 1;
			int stockIndex = 1;
			for (Stock stock : stocks) {
				Row row = sheet.createRow(rowIndex++);
				createStockRow(row, stockIndex++, stock);
			}

			workbook.write(outputStream);
			workbook.close();
			return outputStream.toByteArray();
		} catch (IOException e) {
			throw new IllegalStateException("Unable to generate in-stock Excel report.", e);
		} finally {
			workbook.dispose();
		}
	}

	private void createHeader(SXSSFSheet sheet, CellStyle headerStyle) {
		Row headerRow = sheet.createRow(0);
		for (int i = 0; i < HEADERS.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(HEADERS[i]);
			cell.setCellStyle(headerStyle);
		}
	}

	private void createStockRow(Row row, int stockIndex, Stock stock) {
		int column = 0;
		createCell(row, column++, stockIndex);
		createCell(row, column++, stock.getDeviceModelName());
		createCell(row, column++, buildProductName(stock));
		createCell(row, column++, stock.getProductTypeName());
		createCell(row, column++, stock.getStoreName());
		createCell(row, column++, stock.getProductPrice());
		createCell(row, column++, stock.getQuantity());
		createCell(row, column++, stock.getOrderedQuantity());
		createCell(row, column++, stock.getExtraQuantity());
	}

	private String buildProductName(Stock stock) {
		String productCode = stock.getProductCode() == null ? "" : stock.getProductCode();
		String productName = stock.getProductName() == null ? "" : stock.getProductName();

		return (productCode + " " + productName).trim();
	}

	private void createCell(Row row, int column, Object value) {
		Cell cell = row.createCell(column);

		if (value == null) {
			cell.setCellValue("");
		} else if (value instanceof BigDecimal) {
			cell.setCellValue(((BigDecimal) value).doubleValue());
		} else if (value instanceof Number) {
			cell.setCellValue(((Number) value).doubleValue());
		} else {
			cell.setCellValue(value.toString());
		}
	}

	private CellStyle createHeaderStyle(Workbook workbook) {
		CellStyle headerStyle = workbook.createCellStyle();
		headerStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
		headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headerStyle.setBorderBottom(BorderStyle.THIN);
		headerStyle.setBorderLeft(BorderStyle.THIN);
		headerStyle.setBorderRight(BorderStyle.THIN);
		headerStyle.setBorderTop(BorderStyle.THIN);

		Font font = workbook.createFont();
		font.setBold(true);
		headerStyle.setFont(font);

		return headerStyle;
	}

	private void setColumnWidths(SXSSFSheet sheet) {
		for (int i = 0; i < COLUMN_WIDTHS.length; i++) {
			sheet.setColumnWidth(i, COLUMN_WIDTHS[i] * 256);
		}
	}
}

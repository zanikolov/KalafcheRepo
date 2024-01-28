package com.kalafche.service.fileutil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.kalafche.model.DailyShift;
import com.kalafche.model.DayDto;
import com.kalafche.model.EmployeeHours;
import com.kalafche.model.MonthlySchedule;

@Service
public class MonthlyScheduleExcelReportService {

	public byte[] createPresentFormReportExcel(List<MonthlySchedule> presentForms, List<DayDto> days, Integer month,
			Integer year) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("Присъствена форма " + month + "-" + year);

		createTitleRow(sheet, days.size(), month, year);
		
		createCalendarRow(days, sheet);

		createContent(presentForms, sheet);	
		

//		int rowNum = 0;		
//		for (SaleItem saleItem : saleItemExcelReportRequest.getSaleItems()) {
//			Row row = sheet.createRow(rowNum++);
//			int colNum = 0;
//
//			Cell deviceCell = row.createCell(colNum++);
//			deviceCell.setCellValue((String) saleItem.getDeviceModelName());
//
//			Cell productCodeCell = row.createCell(colNum++);
//			productCodeCell.setCellValue((String) saleItem.getProductCode());
//
//			Cell productNameCell = row.createCell(colNum++);
//			productNameCell.setCellValue((String) saleItem.getProductName());
//			
//			Cell productTypeNameCell = row.createCell(colNum++);
//			productTypeNameCell.setCellValue((String) saleItem.getProductTypeName());
//			
//			Cell productMasterTypeNameCell = row.createCell(colNum++);
//			productMasterTypeNameCell.setCellValue((String) saleItem.getProductMasterTypeName());
//
//			Cell storeNameCell = row.createCell(colNum++);
//			storeNameCell.setCellValue((String) saleItem.getStoreName());
//
//			Cell employeeNameCell = row.createCell(colNum++);
//			employeeNameCell.setCellValue((String) saleItem.getEmployeeName());
//
//			Cell saleTimestampCell = row.createCell(colNum++);
//			saleTimestampCell
//					.setCellValue((String) dateService.convertMillisToDateTimeString(saleItem.getSaleTimestamp(), "dd-MM-yyyy", true));
//
//			Cell itemPriceCell = row.createCell(colNum++);
//			itemPriceCell.setCellValue((String) saleItem.getItemPrice().toPlainString());
//			
//			Cell salePriceCell = row.createCell(colNum++);
//			salePriceCell.setCellValue((String) saleItem.getSalePrice().toPlainString());
//		}

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			workbook.write(baos);
			workbook.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return baos.toByteArray();
	}

	private void createCalendarRow(List<DayDto> days, XSSFSheet sheet) {
		Row calendarRow = sheet.createRow(2);
		int colNum = 2;
		for (DayDto day : days) {
			Cell dayCell = calendarRow.createCell(colNum++);
			dayCell.setCellValue(String.format("%02d", day.getDay()));
			setDayCellStyle(dayCell, day.getDayOfTheWeek(), day.getIsHoliday());
		}
		Cell workingHoursCell = calendarRow.createCell(colNum++);
		workingHoursCell.setCellValue("Общо");
		
		Cell paidLeavesCell = calendarRow.createCell(colNum++);
		paidLeavesCell.setCellValue("ПО");
		
		Cell sickLeavesCell = calendarRow.createCell(colNum++);
		sickLeavesCell.setCellValue("ОБ");
		
		Cell unpaidLeavesCell = calendarRow.createCell(colNum++);
		unpaidLeavesCell.setCellValue("НО");
		
		Cell workDuringHolidaysCell = calendarRow.createCell(colNum++);
		workDuringHolidaysCell.setCellValue("ПТ");
	}

	private void setDayCellStyle(Cell dayCell, int dayOfTheWeek, boolean isHoliday) {
		CellStyle dayCellStyle = dayCell.getSheet().getWorkbook().createCellStyle();
		dayCellStyle.setAlignment(HorizontalAlignment.CENTER);
		dayCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		if (isHoliday || dayOfTheWeek == 6 || dayOfTheWeek == 7) {
			dayCellStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
			dayCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			dayCellStyle.setBorderBottom(BorderStyle.THIN);
			dayCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
	        dayCellStyle.setBorderLeft(BorderStyle.THIN);
	        dayCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
	        dayCellStyle.setBorderRight(BorderStyle.THIN);
	        dayCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
	        dayCellStyle.setBorderTop(BorderStyle.THIN);
	        dayCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
		}
		dayCell.setCellStyle(dayCellStyle);
	}

	private void createContent(List<MonthlySchedule> presentForms, XSSFSheet sheet) {
		int rowNum = 3;
		for (MonthlySchedule presentForm : presentForms) {
			createStoreCell(sheet, rowNum, presentForm);

			for (int i = 0; i < presentForm.getEmployeesHours().size(); i++) {
				EmployeeHours employeeHours = presentForm.getEmployeesHours().get(i);
				Row employeeRow = sheet.getRow(rowNum) != null ? sheet.getRow(rowNum) : sheet.createRow(rowNum);
				rowNum = rowNum + 1;

				Cell employeeCell = employeeRow.createCell(1);
				employeeCell.setCellValue(employeeHours.getEmployee().getName());

				int colNum = 2;
				for (Entry<String, List<DailyShift>> entry : presentForm.getDailyShiftsGroupedByDay().entrySet()) {
					DailyShift dailyShift = entry.getValue().get(i);
					Cell dailyShiftCell = employeeRow.createCell(colNum++);
					dailyShiftCell.setCellValue(StringUtils.isEmpty(dailyShift.getWorkingShiftName()) ? ""
							: dailyShift.getWorkingShiftName());
					setDayCellStyle(dailyShiftCell, dailyShift.getCalendarDayOfTheWeek(), dailyShift.getCalendarIsHoliday());
				}
				Cell workingHoursCell = employeeRow.createCell(colNum++);
				workingHoursCell.setCellValue(employeeHours.getHours());
				
				Cell paidLeavesCell = employeeRow.createCell(colNum++);
				paidLeavesCell.setCellValue(employeeHours.getPaidLeaves());
				
				Cell sickLeavesCell = employeeRow.createCell(colNum++);
				sickLeavesCell.setCellValue(employeeHours.getSickLeaves());
				
				Cell unpaidLeavesCell = employeeRow.createCell(colNum++);
				unpaidLeavesCell.setCellValue(employeeHours.getUnpaidLeaves());
				
				Cell workDuringHolidaysCell = employeeRow.createCell(colNum++);
				workDuringHolidaysCell.setCellValue(employeeHours.getWorkDuringHolidays());
			}

			sheet.createRow(rowNum++);
		}
	}

	private void createStoreCell(XSSFSheet sheet, int rowNum, MonthlySchedule presentForm) {
		sheet.addMergedRegion(
				new CellRangeAddress(rowNum, rowNum + presentForm.getEmployeesHours().size() - 1, 0, 0));
		Row row = sheet.createRow(rowNum);
		Cell storeCell = row.createCell(0);
		storeCell.setCellValue(presentForm.getStoreName());
		setStoreCellStyle(storeCell);
	}

	private void setStoreCellStyle(Cell storeCell) {
		CellStyle storeCellStyle = storeCell.getSheet().getWorkbook().createCellStyle();
		storeCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		storeCell.setCellStyle(storeCellStyle);
	}

	private void createTitleRow(XSSFSheet sheet, int numberOfDays, int month, int year) {
		int titleStartingCell = (numberOfDays - 10) / 2 + 2;
		sheet.addMergedRegion(new CellRangeAddress(0, 1, titleStartingCell, titleStartingCell + 10));

		Row row = sheet.createRow(0);

		Cell titleCell = row.createCell(titleStartingCell);
		titleCell.setCellValue("Keysoo " + month + "-" + year);

		setTitleCellStyle(titleCell);
	}

	private void setTitleCellStyle(Cell titleCell) {
		CellStyle titleCellStyle = titleCell.getSheet().getWorkbook().createCellStyle();
		titleCellStyle.setAlignment(HorizontalAlignment.CENTER);
		titleCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

		Font newFont = titleCell.getSheet().getWorkbook().createFont();
		newFont.setFontHeightInPoints((short) 18);
		titleCellStyle.setFont(newFont);
		titleCell.setCellStyle(titleCellStyle);
	}

}

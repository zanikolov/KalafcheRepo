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
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.kalafche.model.DayDto;
import com.kalafche.model.schedule.DailyShift;
import com.kalafche.model.schedule.EmployeeHours;
import com.kalafche.model.schedule.MonthlySchedule;
import com.kalafche.model.schedule.WorkingShift;

@Service
public class MonthlyScheduleExcelReportService {
	
	private String CONSENT = "Запознах се с графика.";
	private static XSSFWorkbook workbook;
	private static Font calibriFont;
	private static CellStyle titleCellStyle;
	private static CellStyle storeCellStyle;
	private static CellStyle defaultCellStyleWithBorder;
	private static CellStyle defaultCellStyle;
	private static CellStyle totalsCellStyle;
	private static CellStyle totalsHeaderCellStyle;
	private static CellStyle orangeDayCellStyle;
	private static CellStyle goldDayCellStyle;
	private static CellStyle blueDayCellStyle;
	private static CellStyle defaultDayCellStyle;
	private static CellStyle legendTableHeaderCellStyle;
	
	public byte[] createMonthlyScheduleReportExcel(List<MonthlySchedule> monthlySchedules, List<DayDto> days, List<WorkingShift> workingShifts, Integer month,
			Integer year, boolean isPresentFormReport) {
		workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet(isPresentFormReport ? "Присъствена форма " : "График " + month + "-" + year);

		initializeStylesAndFonts();
		
		createTitleRow(sheet, days.size(), month, year);
		
		createCalendarRow(days, isPresentFormReport, sheet);

		int rowNum = createContent(monthlySchedules, isPresentFormReport, sheet);	
		
		createLegendTable(workingShifts, rowNum, sheet);
		
		if (!isPresentFormReport) {
			createConsentArea(monthlySchedules, rowNum, sheet);
		}
		
		fitColumnsWidth(days.size() + 7, sheet);

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

	private void initializeStylesAndFonts() {
		calibriFont = initializeFont();
		titleCellStyle = initializeTitleCellStyle();
		storeCellStyle = initializeStoreCellStyle();
		defaultCellStyleWithBorder = initializeDefaultCellWithBorderStyle();
		defaultCellStyle = initializeDefaultCellStyle();
		totalsCellStyle = initializeTotalsCellStyle();
		totalsHeaderCellStyle = initializeTotalsHeaderCellStyle();
		orangeDayCellStyle = initializeOrangeDayCellStyle();
		goldDayCellStyle = initializeGoldDayCellStyle();
		blueDayCellStyle = initializeBlueDayCellStyle();
		defaultDayCellStyle = initializeDefaultDayCellStyle();
		legendTableHeaderCellStyle = initializeLegendTableHeaderStyle();
	}

	private void createConsentArea(List<MonthlySchedule> monthlySchedules, int rowNum, XSSFSheet sheet) {
		int colNum = 10;
		sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, colNum, colNum + 15));		
		Row row = sheet.getRow(rowNum++);

		Cell consentCell = row.createCell(colNum);
		consentCell.setCellValue(CONSENT);
		consentCell.setCellStyle(defaultCellStyle);
		
		if (!monthlySchedules.isEmpty()) {
			for (EmployeeHours employeeHours : monthlySchedules.get(0).getEmployeesHours()) {
				sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, colNum, colNum + 15));		
				Row employeeSignatureRow = sheet.getRow(rowNum++);
				
				Cell employeeSignatureRowCell = employeeSignatureRow.createCell(colNum);
				employeeSignatureRowCell.setCellValue(employeeHours.getEmployee().getName() + ':');
				employeeSignatureRowCell.setCellStyle(defaultCellStyle);
			}
		}
	}

	private void createLegendTable(List<WorkingShift> workingShifts, int rowNum, XSSFSheet sheet) {
		rowNum = createLegendTableHeader(rowNum, sheet);
		int colNum;
		for (WorkingShift shift : workingShifts) {
			colNum = 0;
			Row shiftRow = sheet.createRow(rowNum++);
			
			Cell shiftNameCell = shiftRow.createCell(colNum++);
			shiftNameCell.setCellValue(shift.getName());
			shiftNameCell.setCellStyle(defaultCellStyleWithBorder);
			
			Cell shiftRangeCell = shiftRow.createCell(colNum++);
			shiftRangeCell.setCellValue(shift.getStartTime() + '-' + shift.getEndTime());
			shiftRangeCell.setCellStyle(defaultCellStyleWithBorder);
			
			Cell shiftDurationCell = shiftRow.createCell(colNum++);
			shiftDurationCell.setCellValue(shift.getDuration());
			shiftDurationCell.setCellStyle(defaultCellStyleWithBorder);
		}
		
		rowNum = createLegendTableLeaveRow(rowNum, sheet, defaultCellStyleWithBorder, "О", "Платен отпуск", "08:00");
		rowNum = createLegendTableLeaveRow(rowNum, sheet, defaultCellStyleWithBorder, "Б", "Болничен", "08:00");
		rowNum = createLegendTableLeaveRow(rowNum, sheet, defaultCellStyleWithBorder, "НО", "Неплатен отпуск", "08:00");
	}

	private int createLegendTableLeaveRow(int rowNum, XSSFSheet sheet, CellStyle cellStyle, String name, String range, String duration) {
		Row shiftRow = sheet.createRow(rowNum++);
		int colNum = 0;
		
		Cell shiftNameCell = shiftRow.createCell(colNum++);
		shiftNameCell.setCellValue(name);
		shiftNameCell.setCellStyle(cellStyle);
		
		Cell shiftRangeCell = shiftRow.createCell(colNum++);
		shiftRangeCell.setCellValue(range);
		shiftRangeCell.setCellStyle(cellStyle);
		
		Cell shiftDurationCell = shiftRow.createCell(colNum++);
		shiftDurationCell.setCellValue(duration);
		shiftDurationCell.setCellStyle(cellStyle);
		
		return rowNum;
	}	

	private  static CellStyle initializeDefaultCellWithBorderStyle() {
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		setBorder(cellStyle);
		cellStyle.setFont(calibriFont);
		
		return cellStyle;
	}
	
	private static CellStyle initializeDefaultCellStyle() {
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellStyle.setFont(calibriFont);
		
		return cellStyle;
	}
	


	private int createLegendTableHeader(int rowNum, XSSFSheet sheet) {
		int colNum = 0;
		Row shiftHeaderRow = sheet.createRow(rowNum++);
		
		Cell shiftNameHeaderCell = shiftHeaderRow.createCell(colNum++);
		shiftNameHeaderCell.setCellValue("Име");
		shiftNameHeaderCell.setCellStyle(legendTableHeaderCellStyle);
		
		Cell shiftRangeHeaderCell = shiftHeaderRow.createCell(colNum++);
		shiftRangeHeaderCell.setCellValue("Диапазон");
		shiftRangeHeaderCell.setCellStyle(legendTableHeaderCellStyle);
		
		Cell shiftDurationHeaderCell = shiftHeaderRow.createCell(colNum++);
		shiftDurationHeaderCell.setCellValue("Прод.");
		shiftDurationHeaderCell.setCellStyle(legendTableHeaderCellStyle);
		
		return rowNum;
	}

	private static CellStyle initializeLegendTableHeaderStyle() {
		CellStyle headerCellStyle = initializeDefaultCellWithBorderStyle();
		headerCellStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
		headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		setBorder(headerCellStyle);
		
		return headerCellStyle;
	}

	private void fitColumnsWidth(int columnsCount, XSSFSheet sheet) {
		for (int i = 0; i < columnsCount; i++) {
			sheet.autoSizeColumn(i, true);
		}
	}

	private static Font initializeFont() {
		Font calibriFont = workbook.createFont();
		calibriFont.setFontHeightInPoints((byte)9);
		calibriFont.setFontName("Calibri");
		
		return calibriFont;
	}

	private void createCalendarRow(List<DayDto> days, boolean isPresentFormReport, XSSFSheet sheet) {
		Row calendarRow = sheet.createRow(2);
		int colNum = 2;
		for (DayDto day : days) {
			Cell dayCell = calendarRow.createCell(colNum++);
			dayCell.setCellValue(String.format("%02d", day.getDay()));
			setDayCellStyle(dayCell, day.getDayOfTheWeek(), day.getIsHoliday(), true);
		}
		Cell workingHoursCell = calendarRow.createCell(colNum++);
		workingHoursCell.setCellValue("Общо");
		workingHoursCell.setCellStyle(totalsHeaderCellStyle);
		
		if (isPresentFormReport) {
			Cell paidLeavesCell = calendarRow.createCell(colNum++);
			paidLeavesCell.setCellValue("О");
			paidLeavesCell.setCellStyle(totalsHeaderCellStyle);

			Cell sickLeavesCell = calendarRow.createCell(colNum++);
			sickLeavesCell.setCellValue("Б");
			sickLeavesCell.setCellStyle(totalsHeaderCellStyle);

			Cell unpaidLeavesCell = calendarRow.createCell(colNum++);
			unpaidLeavesCell.setCellValue("НО");
			unpaidLeavesCell.setCellStyle(totalsHeaderCellStyle);

			Cell workDuringHolidaysCell = calendarRow.createCell(colNum++);
			workDuringHolidaysCell.setCellValue("ПТ");
			workDuringHolidaysCell.setCellStyle(totalsHeaderCellStyle);
		}
	}

	private void setDayCellStyle(Cell dayCell, int dayOfTheWeek, boolean isHoliday, boolean isTitleCell) {
		if (isHoliday || dayOfTheWeek == 6 || dayOfTheWeek == 7 || isTitleCell) {
			if (isHoliday || dayOfTheWeek == 6 || dayOfTheWeek == 7) {
				if (isTitleCell) {
					dayCell.setCellStyle(orangeDayCellStyle);
				} else {
					dayCell.setCellStyle(goldDayCellStyle);
				}
			} else {
				dayCell.setCellStyle(blueDayCellStyle);
			}
		} else {
			dayCell.setCellStyle(defaultDayCellStyle);
		}
	}
	
	private static CellStyle initializeOrangeDayCellStyle() {
		CellStyle totalsCellStyle = createCenterCellStyle();
		totalsCellStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
		totalsCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		return totalsCellStyle;
	}
	
	private static CellStyle initializeGoldDayCellStyle() {
		CellStyle totalsCellStyle = createCenterCellStyle();
		totalsCellStyle.setFillForegroundColor(IndexedColors.GOLD.getIndex());
		totalsCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
				
		return totalsCellStyle;
	}
	
	private static CellStyle initializeBlueDayCellStyle() {
		CellStyle totalsCellStyle = createCenterCellStyle();
		totalsCellStyle.setFillForegroundColor(IndexedColors.SKY_BLUE.getIndex());
		totalsCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		return totalsCellStyle;
	}
	
	private static CellStyle initializeDefaultDayCellStyle() {
		CellStyle totalsCellStyle = createCenterCellStyle();
		
		return totalsCellStyle;
	}	
	
	private static CellStyle createCenterCellStyle() {
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellStyle.setFont(calibriFont);
		setBorder(cellStyle);
		
		return cellStyle;
	}

	private int createContent(List<MonthlySchedule> monthlySchedules, boolean isPresentFormReport, XSSFSheet sheet) {
		int rowNum = 3;
		for (MonthlySchedule monthlySchedule : monthlySchedules) {
			createStoreCell(sheet, rowNum, monthlySchedule);

			for (int i = 0; i < monthlySchedule.getEmployeesHours().size(); i++) {
				EmployeeHours employeeHours = monthlySchedule.getEmployeesHours().get(i);
				Row employeeRow = sheet.getRow(rowNum) != null ? sheet.getRow(rowNum) : sheet.createRow(rowNum);
				rowNum = rowNum + 1;

				Cell employeeCell = employeeRow.createCell(1);
				employeeCell.setCellValue(employeeHours.getEmployee().getName());
				employeeCell.setCellStyle(storeCellStyle);;

				int colNum = 2;
				for (Entry<String, List<DailyShift>> entry : monthlySchedule.getDailyShiftsGroupedByDay().entrySet()) {
					DailyShift dailyShift = entry.getValue().get(i);
					Cell dailyShiftCell = employeeRow.createCell(colNum++);
					dailyShiftCell.setCellValue(StringUtils.isEmpty(dailyShift.getWorkingShiftName()) ? ""
							: dailyShift.getWorkingShiftName());
					setDayCellStyle(dailyShiftCell, dailyShift.getCalendarDayOfTheWeek(), dailyShift.getCalendarIsHoliday(), false);
				}
				Cell workingHoursCell = employeeRow.createCell(colNum++);
				workingHoursCell.setCellValue(employeeHours.getHours());
				workingHoursCell.setCellStyle(totalsCellStyle);
				
				if (isPresentFormReport) {
					Cell paidLeavesCell = employeeRow.createCell(colNum++);
					paidLeavesCell.setCellValue(employeeHours.getPaidLeaves());
					paidLeavesCell.setCellStyle(totalsCellStyle);

					Cell sickLeavesCell = employeeRow.createCell(colNum++);
					sickLeavesCell.setCellValue(employeeHours.getSickLeaves());
					sickLeavesCell.setCellStyle(totalsCellStyle);

					Cell unpaidLeavesCell = employeeRow.createCell(colNum++);
					unpaidLeavesCell.setCellValue(employeeHours.getUnpaidLeaves());
					unpaidLeavesCell.setCellStyle(totalsCellStyle);

					Cell workDuringHolidaysCell = employeeRow.createCell(colNum++);
					workDuringHolidaysCell.setCellValue(employeeHours.getWorkDuringHolidays());
					workDuringHolidaysCell.setCellStyle(totalsCellStyle);
				}
			}

			sheet.createRow(rowNum++);
		}
		
		return rowNum;
	}

	private static CellStyle initializeTotalsCellStyle() {
		CellStyle totalsCellStyle = createCenterCellStyle();

		totalsCellStyle.setFont(calibriFont);
		
		return totalsCellStyle;
	}
	
	private static CellStyle initializeTotalsHeaderCellStyle() {
		CellStyle totalsCellStyle = createCenterCellStyle();
		totalsCellStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
		totalsCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		
		return totalsCellStyle;
	}

	private void createStoreCell(XSSFSheet sheet, int rowNum, MonthlySchedule monthlySchedule) {
		CellRangeAddress region = new CellRangeAddress(rowNum, rowNum + monthlySchedule.getEmployeesHours().size() - 1, 0, 0);
		sheet.addMergedRegion(region);
		Row row = sheet.createRow(rowNum);
		Cell storeCell = row.createCell(0);
		storeCell.setCellValue(monthlySchedule.getStoreName() + " \n " + monthlySchedule.getCompanyName());
		storeCell.setCellStyle(storeCellStyle);
		
		RegionUtil.setBorderTop(BorderStyle.THIN, region, sheet);
		RegionUtil.setBorderBottom(BorderStyle.THIN, region, sheet);
		RegionUtil.setBorderLeft(BorderStyle.THIN, region, sheet);
		RegionUtil.setBorderRight(BorderStyle.THIN, region, sheet);
	}

	private static CellStyle initializeStoreCellStyle() {
		CellStyle storeCellStyle = workbook.createCellStyle();
		storeCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		
		storeCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
		storeCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		setBorder(storeCellStyle);
		storeCellStyle.setFont(calibriFont);
	
		return storeCellStyle;
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

	private void createTitleRow(XSSFSheet sheet, int numberOfDays, int month, int year) {
		int titleStartingCell = (numberOfDays - 10) / 2 + 2;
		CellRangeAddress region = new CellRangeAddress(0, 1, titleStartingCell, titleStartingCell + 10);
		sheet.addMergedRegion(region);

		Row row = sheet.createRow(0);

		Cell titleCell = row.createCell(titleStartingCell);
		titleCell.setCellValue("Keysoo " + String.format("%02d", month) + "-" + year);

		titleCell.setCellStyle(titleCellStyle);
	}

	private static CellStyle initializeTitleCellStyle() {
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);

		Font newFont = workbook.createFont();
		newFont.setFontHeightInPoints((short) 18);
		cellStyle.setFont(newFont);
		
		return cellStyle;
	}

}

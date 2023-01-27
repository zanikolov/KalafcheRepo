package com.kalafche.service.fileutil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.util.StringUtils;

import com.kalafche.exceptions.ExcelInvalidFormatException;

public class JohnTest {

	public static void main(String[] args) throws FileNotFoundException {
		parseExcelData(15);

	}

	public static List<ExcelItem> parseExcelData(int rowNum) throws FileNotFoundException {
		List<ExcelItem> items = new ArrayList<>();
		
		  String excelFilePath = "C:\\Zahari\\consumers2.xlsx";
	        FileInputStream inputStream = new FileInputStream(new File(excelFilePath));

        Workbook workbook;
		try {
			workbook = WorkbookFactory.create(inputStream);

	        Sheet worksheet = workbook.getSheetAt(0);
	        Row row = worksheet.getRow(rowNum);
//	        worksheet.forEach(row -> {
//	        	ExcelItem item = new ExcelItem();
	
//	        	row.forEach(cell -> {
//	        		printCellValue(cell);
//	        	});
	        	System.out.println();
	        	
	        	if (!StringUtils.isEmpty(row.getCell(0))) {
	        		String req = REQUEST;
		            String refId = getCellValue(row.getCell(0));
		            req = req.replace("{clientRef}", refId);
		            
		            String firstName = getCellValue(row.getCell(1));
		            req = req.replace("{firstName}", firstName);
		            String lastName = getCellValue(row.getCell(2));
		            req = req.replace("{lastName}", lastName);
		            
		            String address1 = getCellValue(row.getCell(4));
		            req = req.replace("{streetNumber}", address1.split(" ")[0]);
		            String streetName = "";
		            for (int i = 1; i < address1.split(" ").length; i++) {
		            	streetName = streetName + address1.split(" ")[i] + " ";
		            }
		            streetName = streetName.substring(0, streetName.length() - 1);
		            req = req.replace("{streetName}", streetName);
		            
//		            String address2 = getCellValue(row.getCell(3));
//		            String address3 = getCellValue(row.getCell(4));
//		            String address4 = getCellValue(row.getCell(5));
		            
		            String postCode = getCellValue(row.getCell(5));
		            req = req.replace("{postCode}", postCode);
		            
		            String dob = getCellValue(row.getCell(3));
		            req = req.replace("{dob}", dob);
		           
//		            System.out.print(refId + " ");
//		            System.out.print(name + " ");
//		            System.out.print(address1 + " ");
//		            System.out.print(address2 + " ");
//		            System.out.print(address3 + " ");
//		            System.out.print(address4 + " ");
//		            System.out.print(postCode + " ");
//		            System.out.print(dob + " ");
		            
		            System.out.println(req);
	        	}
	        	
//	        });
		} catch (IllegalStateException | InvalidFormatException | IOException e) {
			e.printStackTrace();
			throw new ExcelInvalidFormatException("file", "The excel contains invalid data.");
		}
		
		return items;
	}
	
	private static String getCellValue(Cell cell) {
		if (cell == null) {
			return "";
		}
		if(cell.getCellTypeEnum() == CellType.STRING) {
            return cell.getStringCellValue();
	    } else if (cell.getCellTypeEnum() == CellType.NUMERIC) {
	    	if (HSSFDateUtil.isCellDateFormatted(cell)) {
	    		DataFormatter formatter = new DataFormatter();
	    		String formattedValue = formatter.formatCellValue(cell);
	    		return formattedValue;
	    	} else
            return (int)cell.getNumericCellValue() + "";
	    }
		
		return "";
	}
	
	private static void printCellValue(Cell cell) {
	    switch (cell.getCellTypeEnum()) {
	        case STRING:
	            System.out.print(cell.getStringCellValue() + "    ");
	            break;
	        case NUMERIC:
	        	if (HSSFDateUtil.isCellDateFormatted(cell)) {
	        		DataFormatter formatter = new DataFormatter();
	        		String formattedValue = formatter.formatCellValue(cell);
	        		System.out.print(formattedValue);
	        	} else {
	        		System.out.print((int) cell.getNumericCellValue() + "    ");
	        	}
	            break;
	        case BLANK:
	            System.out.print("^^    ");
	            break;
	        default:
	            System.out.print("<>    ");
	    }
	}
	
	public static final String REQUEST = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:dir=\"http://ewsconsumer.services.uk.equifax.com/schema/v4/creditsearch/directorcreditsearchrequest\">\r\n"
			+ "   <soapenv:Header/>\r\n"
			+ "   <soapenv:Body>\r\n"
			+ "      <dir:directorCreditSearchRequest>\r\n"
			+ "         <clientRef>{clientRef}</clientRef>\r\n"
			+ "         <soleSearch>\r\n"
			+ "            <creditSearchConfig>\r\n"
			+ "               <optIn>true</optIn>\r\n"
			+ "            </creditSearchConfig>\r\n"
			+ "            <matchCriteria>\r\n"
			+ "               <associate>notRequired</associate>\r\n"
			+ "               <attributable>notRequired</attributable>\r\n"
			+ "               <family>notRequired</family>\r\n"
			+ "               <potentialAssociate>notRequired</potentialAssociate>\r\n"
			+ "               <subject>required</subject>\r\n"
			+ "            </matchCriteria>\r\n"
			+ "            <requestedData>\r\n"
			+ "               <scoreAndCharacteristicRequests>\r\n"
			+ "                  <employSameCompanyInsight>true</employSameCompanyInsight>\r\n"
			+ "                  <scoreRequest>\r\n"
			+ "                     <scoreLabel>RNOSF01</scoreLabel>\r\n"
			+ "                     <scoreLabel>RNOSF02</scoreLabel>\r\n"
			+ "                     <scoreLabel>RNOLF04</scoreLabel>\r\n"
			+ "                     <scoreLabel>RNILF04</scoreLabel>\r\n"
			+ "                  </scoreRequest>\r\n"
			+ "                  <attributeRequest>\r\n"
			+ "                     <index>1</index>\r\n"
			+ "                  </attributeRequest>\r\n"
			+ "               </scoreAndCharacteristicRequests>\r\n"
			+ "            </requestedData>\r\n"
			+ "            <primary>\r\n"
			+ "               <dob>{dob}</dob>\r\n"
			+ "               <name>\r\n"
			+ "                  <surname>{lastName}</surname>\r\n"
			+ "                  <forename>{firstName}</forename>\r\n"
			+ "               </name>\r\n"
			+ "               <currentAddress>\r\n"
			+ "                  <address>\r\n"
			+ "                     <name>{streetNumber}</name>\r\n"
			+ "                     <postcode>{postCode}</postcode>\r\n"
			+ "                     <street1>{streetName}</street1>\r\n"
			+ "                  </address>\r\n"
			+ "               </currentAddress>\r\n"
			+ "            </primary>\r\n"
			+ "         </soleSearch>\r\n"
			+ "      </dir:directorCreditSearchRequest>\r\n"
			+ "   </soapenv:Body>\r\n"
			+ "</soapenv:Envelope>";
}

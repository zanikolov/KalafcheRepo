package com.kalafche.service.fileutil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.Barcode;
import com.itextpdf.text.pdf.BarcodeEAN;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.kalafche.model.BaseStock;

@Service
public class StickerPDFGeneratorServiceImpl implements StickerPDFGeneratorService {
	
	public static final String FONT = "fonts/FreeSans.ttf";
	public static final Font NORMAL_7 = FontFactory.getFont(FONT, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED, 7);
	
	@Override
	public byte[] generateFullStickers(List<? extends BaseStock> newStocks) {
		Rectangle rect = new Rectangle(PageSize.A4);
		System.out.println(rect.getHeight());
		System.out.println(rect.getWidth());
		
		
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] pdfBytes = null;
		
		Document document = new Document(rect, 0, 0, 0, 0);
	    try
	    {
	    	PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
	        document.open();
	 
	        PdfPTable table = configureTable(3, new float[]{2f, 2f, 2f});

	        newStocks.forEach(stock -> {
	        	for (int i = 0; i < stock.getQuantity(); i++) {
		        	PdfPTable stockTable = new PdfPTable(2);
		        	//Empty cells
		        	stockTable.addCell(configureCell("      ", NORMAL_7, 2, 12f, Element.ALIGN_LEFT));
		        	stockTable.addCell(configureCell("www.keysoo.bg", NORMAL_7, 2, 12f, Element.ALIGN_LEFT));
		        	stockTable.addCell(configureCell("      ", NORMAL_7, 2, 12f, Element.ALIGN_LEFT));
		        	
		        	//Product Type
		        	stockTable.addCell(configureCell(stock.getProductTypeName(), NORMAL_7, 2, 12f, Element.ALIGN_LEFT));
		        	
		        	//Product code
		        	stockTable.addCell(configureCell(stock.getProductCode(), NORMAL_7, 2, 12f, Element.ALIGN_LEFT));
		        	
		        	//Product name
		        	String productName = stock.getProductName();
		        	if (!StringUtils.isEmpty(productName)) {
		        		stockTable.addCell(configureCell(productName.substring(0, Math.min(productName.length(), 30)), NORMAL_7, 2, 12f, Element.ALIGN_LEFT));
		        	}
		        	
		        	//Device
		        	stockTable.addCell(configureCell(stock.getDeviceModelName(), NORMAL_7, 2, 12f, Element.ALIGN_LEFT));
		        	stockTable.addCell(configureCell("      ", NORMAL_7, 2, 12f, Element.ALIGN_LEFT));
		        	
		        	//Fabric
		        	stockTable.addCell(configureCell("Състав", NORMAL_7, null, 12f, Element.ALIGN_LEFT));
		        	stockTable.addCell(configureCell(stock.getProductFabric(), NORMAL_7, 2, 12f, Element.ALIGN_LEFT));
		        	
		        	//Manufacturer
		        	stockTable.addCell(configureCell("Произв.", NORMAL_7, null, 12f, Element.ALIGN_LEFT));
		        	stockTable.addCell(configureCell("Идеа Шоу", NORMAL_7, null, 12f, Element.ALIGN_LEFT));
		        	
		        	//Distributer
		        	stockTable.addCell(configureCell("Вносител", NORMAL_7, null, 12f, Element.ALIGN_LEFT));
		        	stockTable.addCell(configureCell("Азаника ЕООД", NORMAL_7, null, 12f, Element.ALIGN_LEFT));
		        	
		        	//Origin
		        	stockTable.addCell(configureCell("Произход", NORMAL_7, null, 12f, Element.ALIGN_LEFT));
		        	stockTable.addCell(configureCell("Китай", NORMAL_7, null, 12f, Element.ALIGN_LEFT));
		        	
		        	//Price
		        	stockTable.addCell(configureCell("Цена", NORMAL_7, null, 12f, Element.ALIGN_LEFT));
		        	stockTable.addCell(configureCell(stock.getProductPrice() + "лв", NORMAL_7, null, 12f, Element.ALIGN_LEFT));
		        	
		        	if (!StringUtils.isEmpty(stock.getBarcode())) {
			        	try {
			        		stockTable.addCell(createBarcode(writer, stock.getBarcode()));
						} catch (DocumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		        	}
		        	PdfPCell cell = new PdfPCell();
		        	cell.setPadding(0);
		        	cell.addElement(stockTable);
		        	cell.setFixedHeight(PageSize.A4.getHeight() / 7);
		        	cell.setRotation(90);
		        	cell.setBorder(Rectangle.NO_BORDER);
		        	
		        	table.addCell(cell);
	        	}
	        });
	        
	 
	        table.completeRow();
	        document.add(table);
	 
	        document.close();
	        writer.close();
	        pdfBytes = byteArrayOutputStream.toByteArray();
	    } 
	    catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    return pdfBytes;		
	}

	private PdfPCell configureCell(String text, Font font, Integer colspan, Float fixedHeight, Integer horizontalAlignment) {
		PdfPCell cell = new PdfPCell(new Paragraph(text, font));
		
		cell.setBorder(Rectangle.NO_BORDER);
		cell.setNoWrap(true);
		
		if (fixedHeight != null) {
			cell.setFixedHeight(fixedHeight);
		}
		
		if (colspan != null) {
			cell.setColspan(colspan);
		}
		
		if (horizontalAlignment != null) {
			cell.setHorizontalAlignment(horizontalAlignment);
		}
		
		return cell;
	}

	private PdfPTable configureTable(int columnsCount, float[] columnWidths) throws DocumentException {
		PdfPTable table = new PdfPTable(columnsCount);
		table.setWidthPercentage(100);
		table.setWidths(columnWidths);
		return table;
	}
	
    public static PdfPCell createBarcode(PdfWriter writer, String code) throws DocumentException, IOException {
        BarcodeEAN barcode = new BarcodeEAN();
        barcode.setCodeType(Barcode.EAN13);
        barcode.setCode(code);
        if (code.length() != 13) {
        	System.out.println(code);
        }
        PdfPCell cell = new PdfPCell(barcode.createImageWithBarcode(writer.getDirectContent(), BaseColor.BLACK, BaseColor.BLACK), true);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setColspan(2);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        return cell;
    }

	@Override
	public byte[] generatePartialStickers(List<? extends BaseStock> newStocks) {
		Rectangle rect = new Rectangle(PageSize.A4);		
		
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] pdfBytes = null;
		
		Document document = new Document(rect, 0, 0, 0, 0);
	    try
	    {
	    	PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
	        document.open();
	 
	        PdfPTable table = configureTable(11, new float[]{2f, 2f, 2f, 2f, 2f, 2f, 2f, 2f, 2f, 2f, 2f});

	        newStocks.forEach(stock -> {
	        	for (int i = 0; i < stock.getQuantity(); i++) {
		        	PdfPTable stockTable = new PdfPTable(2);
		        
		        	stockTable.addCell(configureItemCell(stock.getProductCode(), stock.getDeviceModelName()));
		        	
		        	//Price
		        	stockTable.addCell(configureCell("Цена: " + stock.getProductPrice() + "лв", NORMAL_7, 2, 12f, Element.ALIGN_CENTER));
		        	
		        	if (!StringUtils.isEmpty(stock.getBarcode())) {
			        	try {
			        		stockTable.addCell(createBarcode(writer, stock.getBarcode()));
						} catch (DocumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
		        	}
		        	PdfPCell cell = new PdfPCell();
		        	cell.setPadding(0);
		        	cell.addElement(stockTable);
		        	cell.setFixedHeight(PageSize.A4.getHeight() / 7);
		        	cell.setRotation(90);
		        	//cell.setBorder(Rectangle.NO_BORDER);
		        	
		        	table.addCell(cell);
	        	}
	        });
	        
	 
	        table.completeRow();
	        document.add(table);
	 
	        document.close();
	        writer.close();
	        pdfBytes = byteArrayOutputStream.toByteArray();
	    } 
	    catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    return pdfBytes;		
	}

	private PdfPCell configureItemCell(String productCode, String deviceName) {
		Phrase phrase = new Phrase();
		phrase.add(new Chunk(productCode, NORMAL_7));
		phrase.add(new Chunk(" ", NORMAL_7));
		phrase.add(new Chunk(deviceName, NORMAL_7));
		Paragraph paragraph = new Paragraph(phrase);
		PdfPCell itemCell = new PdfPCell(paragraph);
		itemCell.setFixedHeight(12f);
		itemCell.setNoWrap(false);
		itemCell.setColspan(2);
		itemCell.setBorder(Rectangle.NO_BORDER);
		itemCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		
		return itemCell;
	}
	
}

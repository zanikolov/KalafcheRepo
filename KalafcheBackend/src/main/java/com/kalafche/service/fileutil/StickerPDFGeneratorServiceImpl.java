package com.kalafche.service.fileutil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import com.itextpdf.text.Utilities;
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
	
	private static final BigDecimal EUR_TO_BGN_RATE = new BigDecimal("1.95583");
	
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
		        	stockTable.addCell(configureCell("      ", NORMAL_7, 2, 12f, null, Element.ALIGN_LEFT, null));
		        	stockTable.addCell(configureCell("www.keysoo.bg", NORMAL_7, 2, 12f, null, Element.ALIGN_LEFT, null));
		        	stockTable.addCell(configureCell("      ", NORMAL_7, 2, 12f, null, Element.ALIGN_LEFT, null));
		        	
		        	//Product Type
		        	stockTable.addCell(configureCell(stock.getProductTypeName(), NORMAL_7, 2, 12f, null, Element.ALIGN_LEFT, null));
		        	
		        	//Product code
		        	stockTable.addCell(configureCell(stock.getProductCode(), NORMAL_7, 2, 12f, null, Element.ALIGN_LEFT, null));
		        	
		        	//Product name
		        	String productName = stock.getProductName();
		        	if (!StringUtils.isEmpty(productName)) {
		        		stockTable.addCell(configureCell(productName.substring(0, Math.min(productName.length(), 30)), NORMAL_7, 2, 12f, null, Element.ALIGN_LEFT, null));
		        	}
		        	
		        	//Device
		        	stockTable.addCell(configureCell(stock.getDeviceModelName(), NORMAL_7, 2, 12f, null, Element.ALIGN_LEFT, null));
		        	stockTable.addCell(configureCell("      ", NORMAL_7, 2, 12f, null, Element.ALIGN_LEFT, null));
		        	
		        	//Fabric
		        	stockTable.addCell(configureCell("Състав", NORMAL_7, null, 12f, null, Element.ALIGN_LEFT, null));
		        	stockTable.addCell(configureCell(stock.getProductFabric(), NORMAL_7, 2, 12f, null, Element.ALIGN_LEFT, null));
		        	
		        	//Manufacturer
		        	stockTable.addCell(configureCell("Произв.", NORMAL_7, null, 12f, null, Element.ALIGN_LEFT, null));
		        	stockTable.addCell(configureCell("Идеа Шоу", NORMAL_7, null, 12f, null, Element.ALIGN_LEFT, null));
		        	
		        	//Distributer
		        	stockTable.addCell(configureCell("Вносител", NORMAL_7, null, 12f, null, Element.ALIGN_LEFT, null));
		        	stockTable.addCell(configureCell("Азаника ЕООД", NORMAL_7, null, 12f, null, Element.ALIGN_LEFT, null));
		        	
		        	//Origin
		        	stockTable.addCell(configureCell("Произход", NORMAL_7, null, 12f, null, Element.ALIGN_LEFT, null));
		        	stockTable.addCell(configureCell("Китай", NORMAL_7, null, 12f, null, Element.ALIGN_LEFT, null));
		        	
		        	//Price BGN
		        	stockTable.addCell(configureCell("Цена в лв", NORMAL_7, null, 12f, null, Element.ALIGN_LEFT, null));
		        	stockTable.addCell(configureCell(convertEurToBgn(stock.getProductPrice()) + "лв", NORMAL_7, null, 12f, null, Element.ALIGN_LEFT, null));
		        	
		        	//Price EUR
		        	stockTable.addCell(configureCell("Цена в EUR", NORMAL_7, null, 12f, null, Element.ALIGN_LEFT, null));
		        	stockTable.addCell(configureCell(stock.getProductPrice() + "€", NORMAL_7, null, 12f, null, Element.ALIGN_LEFT, null));
		        	
		        	if (!StringUtils.isEmpty(stock.getBarcode())) {
			        	try {
			        		stockTable.addCell(createBarcode(writer, stock.getBarcode(), 2, null, null, null));
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
	
	public static BigDecimal convertEurToBgn(BigDecimal eur) {
		if (eur == null) {
			return null;
		}

		return eur.multiply(EUR_TO_BGN_RATE).setScale(2, RoundingMode.HALF_UP);
	}

	private PdfPCell configureCell(String text, Font font, Integer colspan, Float fixedHeight, Integer padding, Integer horizontalAlignment, Integer verticalAlignment) {
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
		
        if (verticalAlignment != null) {
        	cell.setVerticalAlignment(verticalAlignment);
        }
        
        if (padding != null) {
        	cell.setPadding(padding);
        }
        
		return cell;
	}

	private PdfPTable configureTable(int columnsCount, float[] columnWidths) throws DocumentException {
		PdfPTable table = new PdfPTable(columnsCount);
		table.setWidthPercentage(100);
		table.setWidths(columnWidths);
		return table;
	}
	
    public static PdfPCell createBarcode(PdfWriter writer, String code, Integer colspan, Integer rowspan, Float fixedHeight, Integer verticalAlignment) throws DocumentException, IOException {
        BarcodeEAN barcode = new BarcodeEAN();
        barcode.setCodeType(Barcode.EAN13);
        barcode.setCode(code);
        if (code.length() != 13) {
        	System.out.println(code);
        }
        PdfPCell cell = new PdfPCell(barcode.createImageWithBarcode(writer.getDirectContent(), BaseColor.BLACK, BaseColor.BLACK), true);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(0);
        if (colspan != null) {
        	cell.setColspan(colspan);
        }
        if (rowspan != null) {
        	cell.setRowspan(rowspan);
        }
        if (verticalAlignment != null) {
        	cell.setVerticalAlignment(verticalAlignment);
        }
        if (fixedHeight != null) {
        	cell.setFixedHeight(fixedHeight);
        }
        
        return cell;
    }

	@Override
	public byte[] generatePartialStickers(List<? extends BaseStock> newStocks) {
		Rectangle rect = new Rectangle(PageSize.A4);

		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] pdfBytes = null;

		Document document = new Document(rect, Utilities.millimetersToPoints(9.25f),
				Utilities.millimetersToPoints(9.25f), Utilities.millimetersToPoints(13),
				Utilities.millimetersToPoints(13));
		try {
			PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
			document.open();

			PdfPTable table = configureTable(5, new float[] { 2f, 2f, 2f, 2f, 2f });

			newStocks.forEach(stock -> {
				for (int i = 0; i < stock.getQuantity(); i++) {
					PdfPTable stockTable = new PdfPTable(1);

					stockTable.addCell(configureItemCell(stock.getProductCode(), stock.getDeviceModelName()));
					stockTable
							.addCell(configureCell(
									"Цена: " + convertEurToBgn(stock.getProductPrice()) + "лв / "
											+ stock.getProductPrice() + "€",
									NORMAL_7, null, null, 1, Element.ALIGN_CENTER, Element.ALIGN_MIDDLE));

					if (!StringUtils.isEmpty(stock.getBarcode())) {
						try {
							stockTable.addCell(createBarcode(writer, stock.getBarcode(), null, null,
									PageSize.A4.getHeight() / 24, null));
						} catch (DocumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					PdfPCell cell = new PdfPCell();
					// cell.setPadding(0);
					cell.setPaddingLeft(Utilities.millimetersToPoints(1.25f));
					cell.setPaddingRight(Utilities.millimetersToPoints(1.25f));
					cell.addElement(stockTable);
					cell.setFixedHeight(Utilities.millimetersToPoints(16.9375f));
					cell.setBorder(Rectangle.NO_BORDER);

					table.addCell(cell);
				}
			});

			table.completeRow();
			document.add(table);

			document.close();
			writer.close();
			pdfBytes = byteArrayOutputStream.toByteArray();
		} catch (Exception e) {
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
		itemCell.setNoWrap(false);
		itemCell.setBorder(Rectangle.NO_BORDER);
		itemCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		itemCell.setPadding(0);
		
		return itemCell;
	}
	
}

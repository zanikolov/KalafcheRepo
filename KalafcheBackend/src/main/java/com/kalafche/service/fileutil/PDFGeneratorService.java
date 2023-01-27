package com.kalafche.service.fileutil;

import java.util.List;

import com.kalafche.model.BaseStock;

public interface PDFGeneratorService {

	byte[] generatePdf(List<? extends BaseStock> newStocks);

}

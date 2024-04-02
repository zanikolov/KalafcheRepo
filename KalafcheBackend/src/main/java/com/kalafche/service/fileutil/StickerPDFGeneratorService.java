package com.kalafche.service.fileutil;

import java.util.List;

import com.kalafche.model.BaseStock;

public interface StickerPDFGeneratorService {

	byte[] generateFullStickers(List<? extends BaseStock> newStocks);

	byte[] generatePartialStickers(List<? extends BaseStock> newStocks);

}

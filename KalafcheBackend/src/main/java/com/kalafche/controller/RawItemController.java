package com.kalafche.controller;

import java.io.IOException;
import java.util.List;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kalafche.model.RawItem;
import com.kalafche.service.RawItemService;

@CrossOrigin
@RestController
@RequestMapping({ "/rawItem" })
public class RawItemController {
	
	@Autowired
	private RawItemService rawItemService;
	
	@PutMapping
	public void updateItemBarcode(@RequestBody RawItem rawItem) {
		rawItemService.updateItemBarcode(rawItem.getProductId(), rawItem.getDeviceModelId(), rawItem.getBarcode());
	}
	
	@PostMapping("/import")
	public List<RawItem> submitNewStockFromFile( @RequestParam("rawItemFile") MultipartFile rawItemFile) throws IOException, EncryptedDocumentException, InvalidFormatException {
		return rawItemService.submitRawItemsFromFile(rawItemFile);
	}
	
}

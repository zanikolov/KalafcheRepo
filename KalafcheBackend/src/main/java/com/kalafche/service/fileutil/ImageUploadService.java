package com.kalafche.service.fileutil;

import org.springframework.web.multipart.MultipartFile;

public interface ImageUploadService {

	public String uploadWasteImage(MultipartFile image);
	
	public String uploadExpenseImage(MultipartFile image);
	
}

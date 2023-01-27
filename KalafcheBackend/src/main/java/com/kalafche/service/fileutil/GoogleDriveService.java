package com.kalafche.service.fileutil;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.common.collect.Lists;
import com.kalafche.exceptions.ImageUploadException;

@Service
public class GoogleDriveService implements ImageUploadService {

	@Autowired
	ImageResizeService imageResizeService;
	
    private static final String APPLICATION_NAME = "keysoo-oauth-client";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    private static final String CLIENT_SECRET_FILE_PATH = "/keysoo-221608-b26a7a8aeedd.json";
    
    private static final String WASTE_FOLDER_ID = "1ij29QyXfzxMlmhqgq1kM3YYOEJNYuSTV";

	private static final String EXPENSE_FOLDER_ID = "1Sm-R_fiYKzLxUM4pbF0_zIuYlV69BVLB";
    
    private static Credential getAPICredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {
    	InputStream in = GoogleDriveService.class.getResourceAsStream(CLIENT_SECRET_FILE_PATH);    	
    	GoogleCredential credentials = GoogleCredential.fromStream(in).createScoped(SCOPES);
   
    	return credentials;
    }

    private Drive createService() throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Drive service = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getAPICredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
        
        return service;
    }

	private static void getAllFiles(Drive service) throws IOException {
		FileList result = service.files().list()
                .setPageSize(10)
                .setFields("nextPageToken, files(id, name)")
                .execute();
        List<File> files = result.getFiles();
        if (files == null || files.isEmpty()) {
            System.out.println("No files found.");
        } else {
            System.out.println("Files:");
            for (File file : files) {
                System.out.printf("%s (%s)\n", file.getName(), file.getId());
            }
        }
	}

	public void createFolder(String name) throws IOException, GeneralSecurityException {
		File fileMetadata = new File();
        fileMetadata.setName(name);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");
        
        createService().files().create(fileMetadata).setFields("id").execute();
	}
	
	public void uploadFile(String name, MultipartFile file) throws IOException, GeneralSecurityException {
		File fileMetadata = new File();
		List<String> parents = Lists.newArrayList(WASTE_FOLDER_ID);
		fileMetadata.setParents(parents);
		FileContent mediaContent = new FileContent(file.getContentType(), multipartToFile(file));
		
		fileMetadata.setName(name);
		
		Drive service = createService();
		service.files().create(fileMetadata, mediaContent).setFields("id").execute();
		
		getWasteImages("test_");
	}
	
	public List<File> getWasteImages(String wasteId) throws IOException, GeneralSecurityException {
		FileList result = createService().files().list()
				.setQ("'" + WASTE_FOLDER_ID + "' in parents and mimeType != 'application/vnd.google-apps.folder' and trashed = false and name contains '" + wasteId + "'")
				.setSpaces("drive")
				.setFields("nextPageToken, files(id, name)")
				.execute();
	
        List<File> files = result.getFiles();
        if (files == null || files.isEmpty()) {
            System.out.println("No files found.");
        } else {
            System.out.println("Files:");
            for (File file : files) {
            	//createService().files().export(fileId, mimeType)
                System.out.printf("%s (%s)\n", file.getName(), file.getId());
            }
        }
		
		return null;
	}
	
	
	public String getWasteImageId(Integer wasteId) throws IOException, GeneralSecurityException {
		FileList result = createService().files().list()
				.setQ("'" + WASTE_FOLDER_ID + "' in parents and mimeType != 'application/vnd.google-apps.folder' and trashed = false and name = '" + wasteId + "_1'")
				.setSpaces("drive")
				.setFields("nextPageToken, files(id, name)")
				.execute();
	
        List<File> files = result.getFiles();
        if (files == null || files.isEmpty()) {
            System.out.println("No files found.");
        } else {
            for (File file : files) {
            	return file.getId();
            }
        }
		
		return null;
	}
	
	private java.io.File multipartToFile(MultipartFile multipart) throws IllegalStateException, IOException {
	    java.io.File convFile = new java.io.File(multipart.getOriginalFilename());
	    multipart.transferTo(convFile);
	    return convFile;
	}

	private String uploadImage(MultipartFile wasteImage, String parentFolder) throws IllegalStateException, IOException, GeneralSecurityException {
		File fileMetadata = new File();
		List<String> parents = Lists.newArrayList(parentFolder);
		fileMetadata.setParents(parents);

		FileContent mediaContent = new FileContent(wasteImage.getContentType(), imageResizeService.resizeImage(multipartToFile(wasteImage)));
		
		Drive service = createService();
		File uploadedFile = service.files().create(fileMetadata, mediaContent).setFields("id").execute();
		
		return uploadedFile.getId();
	}
	
	private String uploadImageHandleExceptions(MultipartFile image, String parentFolder) {
		try {
			if (image.getContentType().startsWith("image/")) {
				return uploadImage(image, parentFolder);
			} else {
				throw new ImageUploadException("image", "Incorrect file type.");
			}
		} catch (IllegalStateException | IOException | GeneralSecurityException e) {
			e.printStackTrace();
			throw new ImageUploadException("image", "Error during image upload.");
		}
	}
	
	@Override
	public String uploadWasteImage(MultipartFile image) {
		return uploadImageHandleExceptions(image, WASTE_FOLDER_ID);
	}
	
	@Override
	public String uploadExpenseImage(MultipartFile image) {
		return uploadImageHandleExceptions(image, EXPENSE_FOLDER_ID);
	}
	
}

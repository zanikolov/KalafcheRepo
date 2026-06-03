package com.kalafche.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kalafche.model.protectplus.ProtectPlusCallRecord;
import com.kalafche.model.protectplus.ProtectPlusCallRecordDownload;
import com.kalafche.model.protectplus.ProtectPlusCertificate;
import com.kalafche.model.protectplus.ProtectPlusCustomerEmailUpdateRequest;
import com.kalafche.model.protectplus.ProtectPlusDeviceModelChangeRecord;
import com.kalafche.model.protectplus.ProtectPlusDeviceModelChangeRequest;
import com.kalafche.model.protectplus.ProtectPlusCertificateRequest;
import com.kalafche.model.protectplus.ProtectPlusCertificateSearchResult;
import com.kalafche.model.protectplus.ProtectPlusRenewalRecord;
import com.kalafche.model.protectplus.ProtectPlusUsageRecord;
import com.kalafche.service.ProtectPlusCertificateService;

@CrossOrigin
@RestController
@RequestMapping({ "/protectPlus/certificate" })
public class ProtectPlusCertificateController {

	@Autowired
	private ProtectPlusCertificateService protectPlusCertificateService;

	@PostMapping("/{certificateId}/activate")
	public ProtectPlusCertificate activateProtectPlusCertificate(@PathVariable(value = "certificateId") Integer certificateId,
			@RequestPart(value = "certificate") ProtectPlusCertificateRequest request,
			@RequestPart(value = "gdprConsentImage") MultipartFile gdprConsentImage) {
		return protectPlusCertificateService.activateProtectPlusCertificate(certificateId, request, gdprConsentImage);
	}

	@PostMapping("/{certificateId}/customerEmail")
	public ProtectPlusCertificate updateCustomerEmail(@PathVariable(value = "certificateId") Integer certificateId,
			@RequestBody ProtectPlusCustomerEmailUpdateRequest request) {
		return protectPlusCertificateService.updateCustomerEmail(certificateId, request);
	}

	@PostMapping("/{certificateId}/callRecording")
	public ProtectPlusCallRecord uploadCallRecording(@PathVariable(value = "certificateId") Integer certificateId,
			@RequestParam(value = "callRecording") MultipartFile callRecording,
			@RequestParam(value = "note", required = false) String note) {
		return protectPlusCertificateService.uploadCallRecording(certificateId, callRecording, note);
	}

	@GetMapping("/{certificateId}/callRecords")
	public List<ProtectPlusCallRecord> getCallRecords(@PathVariable(value = "certificateId") Integer certificateId) {
		return protectPlusCertificateService.getCallRecords(certificateId);
	}

	@GetMapping("/{certificateId}/callRecords/{callRecordId}/download")
	public ResponseEntity<byte[]> downloadCallRecording(@PathVariable(value = "certificateId") Integer certificateId,
			@PathVariable(value = "callRecordId") Integer callRecordId) {
		ProtectPlusCallRecordDownload download = protectPlusCertificateService.downloadCallRecording(certificateId,
				callRecordId);

		return buildFileDownloadResponse(download);
	}

	@GetMapping("/{certificateId}/gdprConsent/download")
	public ResponseEntity<byte[]> downloadGdprConsent(@PathVariable(value = "certificateId") Integer certificateId) {
		return buildFileDownloadResponse(protectPlusCertificateService.downloadGdprConsent(certificateId));
	}

	@GetMapping("/{certificateId}/usageRecords")
	public List<ProtectPlusUsageRecord> getUsageRecords(@PathVariable(value = "certificateId") Integer certificateId) {
		return protectPlusCertificateService.getUsageRecords(certificateId);
	}

	@GetMapping("/{certificateId}/renewalRecords")
	public List<ProtectPlusRenewalRecord> getRenewalRecords(@PathVariable(value = "certificateId") Integer certificateId) {
		return protectPlusCertificateService.getRenewalRecords(certificateId);
	}

	@GetMapping("/{certificateId}/deviceModelChangeRecords")
	public List<ProtectPlusDeviceModelChangeRecord> getDeviceModelChangeRecords(
			@PathVariable(value = "certificateId") Integer certificateId) {
		return protectPlusCertificateService.getDeviceModelChangeRecords(certificateId);
	}

	private ResponseEntity<byte[]> buildFileDownloadResponse(ProtectPlusCallRecordDownload download) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType("application/octet-stream"));
		headers.setContentDispositionFormData(download.getFileName(), download.getFileName());
		headers.set("Content-Transfer-Encoding", "binary");
		headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

		return new ResponseEntity<byte[]>(download.getContents(), headers, HttpStatus.OK);
	}

	@PostMapping("/{certificateId}/deviceModel")
	public ProtectPlusCertificate changeDeviceModel(@PathVariable(value = "certificateId") Integer certificateId,
			@RequestBody ProtectPlusDeviceModelChangeRequest request) {
		return protectPlusCertificateService.changeDeviceModel(certificateId, request);
	}

	@GetMapping("/{certificateId}")
	public ProtectPlusCertificate getProtectPlusCertificate(@PathVariable(value = "certificateId") Integer certificateId) {
		return protectPlusCertificateService.getProtectPlusCertificate(certificateId);
	}

	@GetMapping("/number/{certificateNumber}")
	public ProtectPlusCertificate getProtectPlusCertificateByNumber(
			@PathVariable(value = "certificateNumber") Integer certificateNumber) {
		return protectPlusCertificateService.getProtectPlusCertificateByNumber(certificateNumber);
	}

	@GetMapping("/search")
	public List<ProtectPlusCertificateSearchResult> searchProtectPlusCertificates(
			@RequestParam(value = "query", required = false) String query,
			@RequestParam(value = "certificateNumber", required = false) Integer certificateNumber,
			@RequestParam(value = "phoneNumber", required = false) String phoneNumber,
			@RequestParam(value = "storeId", required = false) Integer storeId,
			@RequestParam(value = "deviceBrandId", required = false) Integer deviceBrandId,
			@RequestParam(value = "deviceModelId", required = false) Integer deviceModelId) {
		if (query != null && !query.isEmpty()) {
			return protectPlusCertificateService.searchActiveProtectPlusCertificatesByQuery(query);
		}

		return protectPlusCertificateService.searchActiveProtectPlusCertificates(certificateNumber, phoneNumber, storeId,
				deviceBrandId, deviceModelId);
	}

	@GetMapping("/inactive")
	public List<ProtectPlusCertificateSearchResult> getInactiveProtectPlusCertificatesForCurrentStore() {
		return protectPlusCertificateService.getInactiveProtectPlusCertificatesForCurrentStore();
	}
}

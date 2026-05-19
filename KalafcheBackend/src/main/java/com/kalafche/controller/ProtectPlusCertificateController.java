package com.kalafche.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kalafche.model.protectplus.ProtectPlusCertificate;
import com.kalafche.model.protectplus.ProtectPlusCertificateRequest;
import com.kalafche.model.protectplus.ProtectPlusCertificateSearchResult;
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

	@PostMapping("/{certificateId}/callRecording")
	public ProtectPlusCertificate uploadCallRecording(@PathVariable(value = "certificateId") Integer certificateId,
			@RequestParam(value = "callRecording") MultipartFile callRecording) {
		return protectPlusCertificateService.uploadCallRecording(certificateId, callRecording);
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
			return protectPlusCertificateService.searchActiveProtectPlusCertificates(query);
		}

		return protectPlusCertificateService.searchProtectPlusCertificates(certificateNumber, phoneNumber, storeId,
				deviceBrandId, deviceModelId);
	}

	@GetMapping("/inactive")
	public List<ProtectPlusCertificateSearchResult> getInactiveProtectPlusCertificatesForCurrentStore() {
		return protectPlusCertificateService.getInactiveProtectPlusCertificatesForCurrentStore();
	}
}

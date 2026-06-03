package com.kalafche.service;

import com.kalafche.model.protectplus.ProtectPlusCertificate;

public interface EmailService {

	void sendProtectPlusActivationEmail(ProtectPlusCertificate certificate);
}

package com.kalafche.service;

import com.kalafche.model.protectplus.ProtectPlusCertificate;
import com.kalafche.model.email.EmailSendResult;

public interface EmailService {

	EmailSendResult sendProtectPlusActivationEmail(ProtectPlusCertificate certificate);

	EmailSendResult sendProtectPlusDeviceModelChangeEmail(ProtectPlusCertificate certificate, String oldDeviceModelName);
}

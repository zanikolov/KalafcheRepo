package com.kalafche.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;
import java.util.TimeZone;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.kalafche.model.protectplus.ProtectPlusCertificate;
import com.kalafche.service.EmailService;

@Service
public class EmailServiceImpl implements EmailService {

	private static final Logger LOGGER = LogManager.getLogger(EmailServiceImpl.class);

	private static final String CONFIG_FILE_PATH = "/properties/keysoo.properties";
	private static final TimeZone BG_TIME_ZONE = TimeZone.getTimeZone("Europe/Sofia");

	private Properties config;

	public EmailServiceImpl() {
		config = new Properties();
		try (InputStream inputStream = EmailServiceImpl.class.getResourceAsStream(CONFIG_FILE_PATH)) {
			if (inputStream != null) {
				config.load(inputStream);
			}
		} catch (IOException exception) {
			LOGGER.error("Unable to load email configuration.", exception);
		}
	}

	@Override
	public void sendProtectPlusActivationEmail(ProtectPlusCertificate certificate) {
		if (!isMailEnabled()) {
			LOGGER.info("Protect+ activation email is not sent because email sending is disabled.");
			return;
		}
		if (certificate == null || StringUtils.isEmpty(certificate.getLoyalCustomerEmail())) {
			LOGGER.warn("Protect+ activation email is not sent because customer email is missing.");
			return;
		}

		try {
			String recipientEmail = certificate.getLoyalCustomerEmail().trim();

			MimeMessage message = new MimeMessage(createMailSession());
			message.setFrom(new InternetAddress(getRequiredProperty("mail.from.email"), getProperty("mail.from.name")));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
			message.setSubject(buildProtectPlusActivationSubject(certificate), "UTF-8");
			message.setContent(buildProtectPlusActivationBody(certificate), "text/html; charset=UTF-8");

			Transport.send(message);
		} catch (Exception exception) {
			LOGGER.error("Protect+ activation email sending failed for certificate ID " + certificate.getId(), exception);
		}
	}

	private Session createMailSession() {
		Properties mailProperties = new Properties();
		mailProperties.put("mail.smtp.host", getRequiredProperty("mail.smtp.host"));
		mailProperties.put("mail.smtp.port", getRequiredProperty("mail.smtp.port"));
		mailProperties.put("mail.smtp.auth", getProperty("mail.smtp.auth", "true"));
		mailProperties.put("mail.smtp.starttls.enable", getProperty("mail.smtp.starttls.enable", "true"));

		return Session.getInstance(mailProperties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(getRequiredProperty("mail.smtp.username"),
						getRequiredProperty("mail.smtp.password"));
			}
		});
	}

	private String buildProtectPlusActivationSubject(ProtectPlusCertificate certificate) {
		return "ПАКЕТ ЗА ЗАЩИТА НА ДИСПЛЕЙ OT KEYSOO";
	}

	private String buildProtectPlusActivationBody(ProtectPlusCertificate certificate) {
		String deviceModelName = StringUtils.isEmpty(certificate.getDeviceModelName())
				? "Вашето устройство"
				: certificate.getDeviceModelName();

		StringBuilder body = new StringBuilder();
		body.append("<html><body>");
		body.append("<h2>ПАКЕТ ЗА ЗАЩИТА НА ДИСПЛЕЙ OT KEYSOO</h2>");
		body.append("<p>Здравейте");
		if (!StringUtils.isEmpty(certificate.getLoyalCustomerName())) {
			body.append(", ").append(escapeHtml(certificate.getLoyalCustomerName()));
		}
		body.append("!</p>");
		body.append("<p>");
		body.append("Добре дошли в клуба на Keysoo Protect PLUS! &#127881;<br>");
		body.append("Вашият пакет за защита Protect PLUS е номер ").append(certificate.getCertificateNumber());
		body.append(" и е активиран за ").append(escapeHtml(deviceModelName));
		body.append("</p>");
		body.append("<p>Не изтривайте този имейл, той Ви предоставя следните привилегии за период от 1 година");
		if (certificate.getValidUntilTimestamp() != null) {
			body.append(" до ").append(formatDate(certificate.getValidUntilTimestamp()));
		}
		body.append("</p>");
		body.append("<ul>");
		body.append("<li>Безплатна еднократна смяна на протектор за ").append(escapeHtml(deviceModelName)).append("</li>");
		body.append("<li>50% отстъпка на всички протектори в периода на валидност на пакета за екрана на ")
				.append(escapeHtml(deviceModelName)).append("</li>");
		body.append("<li>15% отстъпка на всички останали продукти в нашите магазини в периода на валидност на пакета.</li>");
		body.append("<li>Безплатен сервизен труд при една смяна на дисплей по време на валидност на пакета за ")
				.append(escapeHtml(deviceModelName)).append("</li>");
		body.append("<li>Безплатен сервизен труд при смяна на батерия по време на валидност на пакета за ")
				.append(escapeHtml(deviceModelName)).append("</li>");
		body.append("</ul>");
		body.append("<p>Пакетът за защита на дисплей Protect PLUS не се комбинира с други текущи промоции или отстъпки.</p>");
		body.append("<p>Keysoo Protect PLUS е приложим във всеки физически магазин на Keysoo.</p>");
		body.append("<p>При смяна на устройството, можете да заявите една смяна на марката и модела в рамките на валидността, ")
				.append("като отговорите със съобщение, съдържащо новите марка и модел, посетите наш магазин или с обаждане на телефон 0700 10751</p>");
		body.append("<p>Покажете това съобщение в магазина за прилагане на отстъпките.</p>");
		body.append("<p>Благодарим Ви, че избрахте Keysoo и Ви желаем приятно пазаруване!</p>");
		body.append("</body></html>");
		return body.toString();
	}

	private boolean isMailEnabled() {
		return Boolean.parseBoolean(getProperty("mail.enabled", "false"));
	}

	private String getRequiredProperty(String name) {
		String value = getProperty(name);
		if (StringUtils.isEmpty(value)) {
			throw new IllegalStateException("Missing email configuration property: " + name);
		}

		return value;
	}

	private String getProperty(String name) {
		return getProperty(name, null);
	}

	private String getProperty(String name, String defaultValue) {
		String systemPropertyValue = System.getProperty(name);
		if (!StringUtils.isEmpty(systemPropertyValue)) {
			return systemPropertyValue;
		}

		String environmentVariableValue = System.getenv(toEnvironmentVariableName(name));
		if (!StringUtils.isEmpty(environmentVariableValue)) {
			return environmentVariableValue;
		}

		if (config == null) {
			return defaultValue;
		}

		return config.getProperty(name, defaultValue);
	}

	private String toEnvironmentVariableName(String propertyName) {
		return propertyName.toUpperCase(Locale.ROOT).replace(".", "_").replace("-", "_");
	}

	private String formatDate(Long timestamp) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("bg", "BG"));
		dateFormat.setTimeZone(BG_TIME_ZONE);
		return dateFormat.format(new Date(timestamp));
	}

	private String escapeHtml(String value) {
		return value.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\"", "&quot;")
				.replace("'", "&#39;");
	}
}

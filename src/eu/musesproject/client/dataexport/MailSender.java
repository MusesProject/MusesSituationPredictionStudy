package eu.musesproject.client.dataexport;

import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class MailSender extends javax.mail.Authenticator {
	private String mUser;
	private String mPw;
	private String mTo;
	private String mPort;
	private String mSport;
	private String mHost;
	private String mSubject;
	private Multipart mMultipart;

	public MailSender(String subject) {

		mHost = "smtp.gmail.com";
		mPort = "465";
		mSport = "465";
		mUser = "mobis.study@gmail.com";
		mPw = "http://mobis.informatik.uni-hamburg.de31072014";
		mSubject = subject;
		mTo = "mobis.study@gmail.com";

		MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
		mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
		mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
		mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
		mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
		mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
		CommandMap.setDefaultCommandMap(mc);

		mMultipart = new MimeMultipart();

	}

	public boolean sendMail() {

		try {
			Properties props = setProperties();
			Session session = Session.getInstance(props, this);
			MimeMessage msg = new MimeMessage(session);

			msg.setFrom(new InternetAddress());
			msg.setRecipients(MimeMessage.RecipientType.TO, mTo);
			msg.setSubject(mSubject);

			// Put parts in message
			msg.setContent(mMultipart);

			// send email
			Transport.send(msg);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public boolean setText(String text) {
		// setup message body
		BodyPart messageBodyPart = new MimeBodyPart();
		try {
			messageBodyPart.setText(text);
			mMultipart.addBodyPart(messageBodyPart);
			return true;
		} catch (MessagingException e) {
			return false;
		}

	}

	public boolean setAttachment(String filePath) {
		// add attachment
		DataSource source = new FileDataSource(filePath);
		BodyPart messageBodyPart = new MimeBodyPart();
		try {
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName("user_datapoints.xml");
			mMultipart.addBodyPart(messageBodyPart);
			return true;
		} catch (MessagingException e) {
			return false;
		}

	}

	private Properties setProperties() {
		Properties props = new Properties();

		props.put("mail.smtp.host", mHost);

		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", mPort);
		props.put("mail.smtp.socketFactory.port", mSport);
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.socketFactory.fallback", "false");

		return props;
	}

	@Override
	public PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(mUser, mPw);
	}

}
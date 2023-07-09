package ngd.mail;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class MailSenderService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MailSenderService.class);

	@Autowired
	private JavaMailSender htmlMailSender;
	
	public void sendMail(String msg, String email, String subject) {
		try {
			MimeMessage hmm = htmlMailSender.createMimeMessage(); 
			MimeMessageHelper helper = new MimeMessageHelper(hmm, "utf-8");

			helper.setTo(email);
			helper.setSubject(subject);
			hmm.setContent(msg, "text/html");
	
			htmlMailSender.send(hmm);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			LOGGER.info(e.getMessage());
		}
	}
}

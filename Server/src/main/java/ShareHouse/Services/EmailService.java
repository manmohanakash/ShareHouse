package ShareHouse.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import ShareHouse.Models.User;

@Service
public class EmailService {

	private JavaMailSender javaMailSender;
	
	@Autowired
	public EmailService(JavaMailSender javaMailSender) {
		this.javaMailSender = javaMailSender;
	}
	
	public void welcomemail(User user) throws MailException {
		SimpleMailMessage mail= new SimpleMailMessage();
		mail.setTo(user.getEmail());
		mail.setFrom("sharehouseapp@gmail.com");
		mail.setSubject("ShareHouse Account Registered");
		mail.setText("Dear "+user.getFirstName()+" "+user.getLastName()+",\n\n"+""
				+ "Your account has been sucesfully registered.\nThanks for downloading ShareHouse App.\n\nAccount Details:\n\tFirst Name : "+user.getFirstName()
				+"\n\tLast Name : "+user.getLastName()+"\n\tEmail : "+user.getEmail()+"\n\tUsername : "+user.getUserName()
				+"\n\nShould you ever encounter problems with your account or forget your password please contact us on sharehouseapp@gmail.com"
				+"\n\nEnjoy!\nShareHouse Team");
		
		javaMailSender.send(mail);
	}
	
	public void passwordchangemail(User user) throws MailException {
		SimpleMailMessage mail= new SimpleMailMessage();
		mail.setTo(user.getEmail());
		mail.setFrom("sharehouseapp@gmail.com");
		mail.setSubject("Account Password Changed");
		mail.setText("Dear "+user.getFirstName()+" "+user.getLastName()+",\n\n"+""
				+ "Your account has been changed.\n\nAccount Details:\n\tFirst Name : "+user.getFirstName()
				+"\n\tLast Name : "+user.getLastName()+"\n\tEmail : "+user.getEmail()+"\n\tUsername : "+user.getUserName()+"\n\tPassword : "+user.getPassword()
				+"\n\nIf you did not request for new password please contact us on sharehouseapp@gmail.com"
				+"\n\nEnjoy!\nShareHouse Team");
		
		javaMailSender.send(mail);
	}
	
	
	
	
	
}

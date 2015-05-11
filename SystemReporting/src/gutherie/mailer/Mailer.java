package gutherie.mailer;

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.sun.mail.smtp.SMTPTransport;

public class Mailer {
	public Mailer(Properties props, String FROM, String TO, String SUBJECT){
		super();
		properties = props;
		from = FROM;
		to = TO;
		subject = SUBJECT;
		init();
	}
	
	public void appendToMessage(String text){
		message.append(text);
	}
	
	public void clearMessage(){
		message = new StringBuffer();
	}
	
	public String getMessage(){
		return message.toString();
	}
	
	public boolean sendMessage(){
		Session session = Session.getInstance(properties);
		Message message = new MimeMessage(session);

		try{
		    message.setFrom(new InternetAddress(from));
		    InternetAddress[] toAddresses = {new InternetAddress(to)};
		    message.setRecipients(Message.RecipientType.TO, toAddresses);
		    message.setSubject(subject);
		    message.setSentDate(new Date());
		    message.setText(message.toString());
		    message.setHeader("X-mailer", "DFMMAIL");
		    
		    SMTPTransport transport = (SMTPTransport)session.getTransport("smtp");
		    transport.connect((String)properties.get("mail.smtp.host"));
		    transport.sendMessage(message, message.getAllRecipients());      
		    transport.close();

			
			return true;
		}catch(AddressException e){
			System.out.println("Error sending message : " + e.getMessage());
		}catch(MessagingException e){
			System.out.println("Error sending message : " + e.getMessage());
		}
		return false;
	}
	
	private void init(){
		message = new StringBuffer();
	}
	
	private String from;
	private String to;
	private String subject;
	private Properties properties;
	private StringBuffer message;
	
}

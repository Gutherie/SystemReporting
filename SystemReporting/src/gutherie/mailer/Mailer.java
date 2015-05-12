package gutherie.mailer;

/*
 *   This file is part of SystemReporting.
 *
 *   SystemReporting is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   SystemReporting is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with SystemReporting.  If not, see <http://www.gnu.org/licenses/>.
 */

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
		textMessage.append(text);
	}
	
	public void clearMessage(){
		textMessage = new StringBuffer();
	}
	
	public String getMessage(){
		return textMessage.toString();
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
		    message.setText(textMessage.toString());
		    //message.setText("Test Message");
		    message.setHeader("X-mailer", "DFMMAIL");
		    
		    Transport.send(message);


			
			return true;
		}catch(AddressException e){
			System.out.println("Error sending message : " + e.getMessage());
		}catch(MessagingException e){
			System.out.println("Error sending message : " + e.getMessage());
		}
		return false;
	}
	
	private void init(){
		textMessage = new StringBuffer();
	}
	
	private String from;
	private String to;
	private String subject;
	private Properties properties;
	private StringBuffer textMessage;
	
}

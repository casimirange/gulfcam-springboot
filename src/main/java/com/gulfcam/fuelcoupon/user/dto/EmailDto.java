package com.gulfcam.fuelcoupon.user.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class EmailDto {
	
	private String to;
	
	private String subject;
	
	private String body;

	private String replyTo;

	private String from;

	private String senderName;

	private String replyToName;

	private String namefile;

	private String contentType;

	private MultipartFile[] files;

	private byte[] attachement;

	private Map<String, Object> props = new HashMap<>();

	public EmailDto(String from,String senderName, String to,String replyTo, Map<String, Object> props, String subject, String template) {
		super();
		this.from = from;
		this.senderName = senderName;
		this.to = to;
		this.replyTo= replyTo;
		this.subject = subject;
		this.body = template;
		this.props = props;
	}

	public EmailDto(String from,String senderName, String to,String replyTo, Map<String, Object> props, String subject, String template, byte[] attachement) {
		super();
		this.from = from;
		this.senderName = senderName;
		this.to = to;
		this.replyTo= replyTo;
		this.subject = subject;
		this.body = template;
		this.attachement = attachement;
	}

	public EmailDto(String from,String senderName, String to,String replyTo, Map<String, Object> props, String subject, String template, byte[] attachement, String namefile, String contentType) {
		super();
		this.from = from;
		this.senderName = senderName;
		this.to = to;
		this.replyTo= replyTo;
		this.subject = subject;
		this.body = template;
		this.attachement = attachement;
		this.namefile = namefile;
		this.contentType = contentType;
	}
}

package com.gulfcam.fuelcoupon.utilities.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Getter
@Setter
@ToString
public class SupportDtoJms implements Serializable {

    private String subject;

    private String body;

    private int ticketId;

    private String completName;

    private MultipartFile[] files;

    private String template;
}

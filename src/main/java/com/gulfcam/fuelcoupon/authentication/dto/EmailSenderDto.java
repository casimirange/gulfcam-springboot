package com.gulfcam.fuelcoupon.authentication.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ToString
public class EmailSenderDto implements Serializable {
    private static final long serialVersionUID = 7526472295622776147L;
    private String to;
    private String object;
    private String message;
    private String template;
    private String code;
    private String idGulfcam;
    private String completName;
    private String linkConnection;
    private String password;
    private String phone;
    private String action;
    private String question;

}

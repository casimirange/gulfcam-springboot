package com.gulfcam.fuelcoupon.authentication.dto;

import lombok.Value;

@Value
public class SignUpResponse {
    private boolean using2FA;
    private String qrCodeImage;
    private String access_token;
    private boolean authenticated;
}

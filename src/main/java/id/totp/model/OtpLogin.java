package id.totp.model;

import lombok.Data;

@Data
public class OtpLogin {

    private String account;
    private String key;
}

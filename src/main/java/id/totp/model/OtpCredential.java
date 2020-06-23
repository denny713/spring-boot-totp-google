package id.totp.model;

import lombok.Data;

@Data
public class OtpCredential {

    private String username;
    private String key;
}

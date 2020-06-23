package id.totp.service;

import id.totp.entity.OtpAuth;

import javax.transaction.Transactional;
import java.net.URISyntaxException;

@Transactional
public interface AuthService {

    public OtpAuth getByUsername(String username);

    public void saveAuth(OtpAuth otp);

    public String generateAuthKey();

    public String generateKeyUri(String account, String issuer, String secret) throws URISyntaxException;

    public Boolean isUsedKey(int pass, int size);
}

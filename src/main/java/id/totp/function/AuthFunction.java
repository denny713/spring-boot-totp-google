package id.totp.function;

import org.springframework.transaction.annotation.Transactional;

import java.net.URISyntaxException;

@Transactional
public interface AuthFunction {

    public String generateKey();

    public String generateKeyUri(String account, String issuer, String secret) throws URISyntaxException;

    public Boolean isUsedKey(int pass, int size);
}

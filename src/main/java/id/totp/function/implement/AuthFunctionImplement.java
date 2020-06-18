package id.totp.function.implement;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import id.totp.function.AuthFunction;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class AuthFunctionImplement implements AuthFunction {

    private final long keyInterval = TimeUnit.SECONDS.toMillis(30);
    private int lastUsedPassword = -1;
    private long lastVerifiedTime = 0;

    @Override
    public String generateKey() {
        final GoogleAuthenticator auth = new GoogleAuthenticator();
        final GoogleAuthenticatorKey key = auth.createCredentials();
        return key.getKey();
    }

    @Override
    public String generateKeyUri(String account, String issuer, String secret) throws URISyntaxException {
        URI link = new URI("otpauth", "totp", "/" + issuer + ":" + account, "secret=" + secret + "&issuer=" + issuer, null);
        return link.toASCIIString();
    }

    @Override
    public Boolean isUsedKey(int pass, int size) {
        long now = new Date().getTime();
        int forwardTimesLots = ((size - 1) / 2);
        long timesLotNow = now / keyInterval;
        long timesLotThen = lastVerifiedTime / keyInterval;
        if (pass != lastUsedPassword || timesLotNow > timesLotThen + forwardTimesLots) {
            lastUsedPassword = pass;
            lastVerifiedTime = now;
            return true;
        }
        return false;
    }
}

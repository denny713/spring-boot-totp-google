package id.totp.controller;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorConfig;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import com.warrenstrange.googleauth.GoogleAuthenticatorQRGenerator;
import id.totp.entity.OtpAuth;
import id.totp.model.OtpCredential;
import id.totp.model.Result;
import id.totp.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;

@RestController
public class OtpController {

    @Autowired
    private AuthService authService;

    private static Boolean isNumericType(String secret) {
        return secret.matches("-?\\d+(\\.\\d+)?");
    }

    private static GoogleAuthenticatorKey setUp() {
        GoogleAuthenticatorConfig config = new GoogleAuthenticatorConfig.GoogleAuthenticatorConfigBuilder().build();
        return new GoogleAuthenticatorKey.Builder("secretKey").setConfig(config).setVerificationCode(123456).setScratchCodes(new ArrayList<Integer>()).build();
    }

    @GetMapping("/init")
    @ResponseBody
    public Result initialOtp(@Valid @RequestBody String account) {
        Result result = new Result();
        try {
            String key = authService.generateAuthKey();
            String uri = authService.generateKeyUri(account, "OTP", key);
            OtpAuth auth = new OtpAuth();
            auth.setUsername(account);
            auth.setSecret(key);
            auth.setUri(uri);
            authService.saveAuth(auth);
            result.setResult(true);
            result.setMessage("Save Success With Key " + key);
        } catch (Exception g) {
            result.setResult(false);
            result.setMessage(g.getMessage());
        }
        return result;
    }

    @GetMapping("/verify")
    @ResponseBody
    public Result verifyCode(@Valid @RequestBody OtpCredential otp) {
        Result response = new Result();
        GoogleAuthenticator auth = new GoogleAuthenticator();
        OtpAuth userAuth = authService.getByUsername(otp.getUsername());
        if (userAuth == null) {
            response.setResult(false);
            response.setMessage("User " + otp.getUsername() + " Not Found");
        } else {
            if (Boolean.FALSE.equals(isNumericType(otp.getKey()))) {
                response.setResult(false);
                response.setMessage("Secret Code Must Be Numeric Type");
            } else {
                String key = userAuth.getSecret();
                Integer totp = Integer.valueOf(otp.getKey().equals("") ? "-1" : otp.getKey());
                Boolean unused = authService.isUsedKey(totp, 3);
                Boolean matches = auth.authorize(key, totp);
                if (unused && matches) {
                    response.setResult(true);
                    response.setMessage("Valid Key");
                } else {
                    response.setResult(false);
                    response.setMessage("Invalid Key");
                }
            }
        }
        return response;
    }

    @GetMapping("/qr")
    @ResponseBody
    public String getQrCode(@Valid @RequestBody String account) {
        GoogleAuthenticatorKey credential = setUp();
        return GoogleAuthenticatorQRGenerator.getOtpAuthTotpURL("OTP", account, credential);
    }

    @GetMapping("/url")
    @ResponseBody
    public String getUrl(@Valid @RequestBody String account) {
        GoogleAuthenticatorKey credential = setUp();
        return GoogleAuthenticatorQRGenerator.getOtpAuthURL("OTP", account, credential);
    }
}

package id.totp.controller;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import id.totp.entity.OtpAuth;
import id.totp.function.AuthFunction;
import id.totp.model.OtpLogin;
import id.totp.model.Response;

import javax.validation.Valid;

import id.totp.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;

@RestController
@SuppressWarnings("serial")
@RequestMapping("/api/otp")
public class OtpController {

    @Autowired
    private AuthFunction authFunction;

    @Autowired
    private AuthService authService;

    private static Boolean isNumeric(String secret) {
        return secret.matches("-?\\d+(\\.\\d+)?");
    }

    @GetMapping("/init")
    @ResponseBody
    public Response initialOtp(@Valid @RequestParam String account) throws URISyntaxException {
        Response response = new Response();
        try {
            String key = authFunction.generateKey();
            String authLink = authFunction.generateKeyUri(account, "OTP", key);
            response.setResult(true);
            response.setMessage("Success\n URL : " + authLink + "\n Key : " + key);
        } catch (Exception f) {
            response.setResult(false);
            response.setMessage(f.getMessage());
        }
        return response;
    }

    @GetMapping("/verify")
    @ResponseBody
    public Response verifyCode(@Valid @RequestParam OtpLogin otp) {
        Response response = new Response();
        try {
            GoogleAuthenticator auth = new GoogleAuthenticator();
            OtpAuth totp = authService.getByUsername(otp.getAccount());
            if (totp == null) {
                response.setResult(false);
                response.setMessage("Account " + otp.getAccount() + " Not Found");
            } else {
                if (Boolean.FALSE.equals(isNumeric(otp.getKey()))) {
                    response.setResult(false);
                    response.setMessage("Secret Code Must Be Numeric Type");
                } else {
                    Integer keys = Integer.valueOf(otp.getKey().equals("") ? "-1" : otp.getKey());
                    Boolean unused = authFunction.isUsedKey(keys, 3);
                    Boolean matches = auth.authorize(totp.getKey(), keys);
                    if (unused && matches) {
                        response.setResult(true);
                        response.setMessage("Key Is Valid");
                    } else {
                        response.setResult(false);
                        response.setMessage("Key Is Invalid");
                    }
                }
            }
        } catch (Exception f) {
            response.setResult(false);
            response.setMessage(f.getMessage());
        }
        return response;
    }
}

package id.totp.service;

import id.totp.entity.OtpAuth;

import javax.transaction.Transactional;

@Transactional
public interface AuthService {

    public OtpAuth getByUsername(String username);

    public void saveAuth(OtpAuth auth);
}

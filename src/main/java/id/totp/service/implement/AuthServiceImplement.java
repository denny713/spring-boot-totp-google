package id.totp.service.implement;

import id.totp.entity.OtpAuth;
import id.totp.repository.AuthRepository;
import id.totp.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImplement implements AuthService {

    @Autowired
    private AuthRepository authRepository;

    @Override
    public OtpAuth getByUsername(String username) {
        return authRepository.findByUsername(username);
    }
}

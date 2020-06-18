package id.totp.repository;

import id.totp.entity.OtpAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepository extends JpaRepository<OtpAuth, String> {

    public OtpAuth findByUsername(String username);
}

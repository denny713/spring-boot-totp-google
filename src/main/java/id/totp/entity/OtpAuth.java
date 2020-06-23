package id.totp.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Table(name = "otp_authentication")
public class OtpAuth {

    @Id
    @Column(name = "username")
    private String username;

    @Column(name = "uri")
    private String uri;

    @Column(name = "secret")
    private String secret;
}

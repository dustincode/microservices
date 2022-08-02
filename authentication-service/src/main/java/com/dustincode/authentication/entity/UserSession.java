package com.dustincode.authentication.entity;

import com.dustincode.authentication.security.dto.JwtTokenDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "t_user_session")
@NoArgsConstructor @AllArgsConstructor
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id")
    private String sessionId;

    @Lob
    @Column(name = "token")
    private String token;

    @Column(name = "expired_time")
    private Date expiredTime;

    @ManyToOne
    @JoinColumn(name="fk_user_id", nullable = false)
    private User user;

    public UserSession(User user, JwtTokenDto tokenDto) {
        sessionId = tokenDto.getTokenId();
        token = tokenDto.getToken();
        expiredTime = tokenDto.getExpiredTime();
        this.user = user;
    }
}

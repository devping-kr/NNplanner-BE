package devping.nnplanner.domain.auth.entity;

import jakarta.persistence.Id;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "nnemail")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Email {

    @Id
    private String id;

    private String email;

    private String verificationCode;

    private long expirationTime;

    private boolean isVerified;

    @Indexed(name = "createdAtIndex", expireAfterSeconds = 1800) // 30분
    private Instant createdAt;

    public void create(String email,
                       String verificationCode,
                       long expirationTime,
                       boolean isVerified,
                       Instant createdAt) {

        this.email = email;
        this.verificationCode = verificationCode;
        this.expirationTime = expirationTime;
        this.isVerified = isVerified;
        this.createdAt = createdAt;
    }

    public void update(String verificationCode, long expirationTime) {
        this.verificationCode = verificationCode;
        this.expirationTime = expirationTime;
    }

    public void verify(boolean isVerified) {
        this.isVerified = isVerified;
    }
}

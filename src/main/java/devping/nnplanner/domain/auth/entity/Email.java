package devping.nnplanner.domain.auth.entity;

import jakarta.persistence.Id;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "nnemail")
@Getter
@Builder
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

    public Email create(String email,
                        String verificationCode,
                        long expirationTime,
                        boolean isVerified) {

        return Email.builder()
                    .email(email)
                    .verificationCode(verificationCode)
                    .expirationTime(expirationTime)
                    .isVerified(isVerified)
                    .build();
    }

    public void update(String verificationCode, long expirationTime) {
        this.verificationCode = verificationCode;
        this.expirationTime = expirationTime;
    }

    public void verify(boolean isVerified) {
        this.isVerified = isVerified;
    }
}

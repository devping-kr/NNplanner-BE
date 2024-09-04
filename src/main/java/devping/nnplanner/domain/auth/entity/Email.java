package devping.nnplanner.domain.auth.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "nnemail")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Email {

    @Id
    private String id;

    @Setter
    private String email;

    @Setter
    private String verificationCode;

    @Setter
    private long expirationTime;

    @Setter
    private boolean isVerified;
}

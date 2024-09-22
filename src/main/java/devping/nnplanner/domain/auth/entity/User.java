package devping.nnplanner.domain.auth.entity;

import devping.nnplanner.global.entity.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String username;

    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    public enum LoginType {
        LOCAL,
        GOOGLE
    }

    public void create(String username,
                       String email,
                       String password,
                       LoginType loginType) {
        
        this.username = username;
        this.email = email;
        this.password = password;
        this.loginType = loginType;
    }
}

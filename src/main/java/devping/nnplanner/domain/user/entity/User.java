package devping.nnplanner.domain.user.entity;

import devping.nnplanner.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
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

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String email;

    private String password;

    @Column(nullable = false)
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

    public void update(String password) {
        this.password = password;
    }
}

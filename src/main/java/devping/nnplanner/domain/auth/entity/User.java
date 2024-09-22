package devping.nnplanner.domain.auth.entity;

import devping.nnplanner.global.entity.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "users")
@Builder
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

    public User create(String username,
                       String email,
                       String password) {

        return User.builder()
                   .username(username)
                   .email(email)
                   .password(password)
                   .build();
    }
}

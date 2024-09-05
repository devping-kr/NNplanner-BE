package devping.nnplanner.global.jwt.token;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "refreshToken", timeToLive = 345600) //4일
@AllArgsConstructor
public class RefreshToken {

    @Id
    private Long userId;

    private String refreshToken;
}
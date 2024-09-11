package devping.nnplanner.global.jwt.token;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "blackList", timeToLive = 10800) //3시간
@AllArgsConstructor
public class BlackList {

    @Id
    private String tokenId;
}

package devping.nnplanner.domain.auth.repository;

import devping.nnplanner.domain.auth.entity.Email;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailRepository extends MongoRepository<Email, String> {

    Optional<Email> findByEmail(String email); // 이메일로 인증 정보 조회

    boolean existsByEmail(String email);

    boolean existsByEmailAndVerifiedIsFalse(String email);
}
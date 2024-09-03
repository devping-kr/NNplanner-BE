package devping.nnplanner.domain.auth.service;

import devping.nnplanner.domain.auth.dto.request.AuthRequestDTO;
import devping.nnplanner.domain.auth.entity.User;
import devping.nnplanner.domain.auth.repository.EmailRepository;
import devping.nnplanner.domain.auth.repository.UserRepository;
import devping.nnplanner.global.exception.CustomException;
import devping.nnplanner.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final EmailRepository emailRepository;
    
    @Transactional
    public void signUp(AuthRequestDTO authRequestDTO) {

        if (userRepository.existsByEmail(authRequestDTO.getEmail())) {
            throw new CustomException(ErrorCode.ALREADY_EMAIL);
        }
        ;
        if (!authRequestDTO.getPassword().equals(authRequestDTO.getPasswordConfirm())) {
            throw new CustomException(ErrorCode.NOT_MATCH_PASSWORD);
        }
        ;
        if (!emailRepository.existsByEmail(authRequestDTO.getEmail())
            && emailRepository.existsByEmailAndVerifiedIsFalse(authRequestDTO.getEmail())) {
            throw new CustomException(ErrorCode.NOT_VERIFIED_EMAIL);
        }
        ;

        User user = new User();

        user.setUsername(authRequestDTO.getUsername());
        user.setEmail(authRequestDTO.getEmail());
        user.setPassword(passwordEncoder.encode(authRequestDTO.getPassword()));
        user.setCreatedBy(authRequestDTO.getEmail());

        userRepository.save(user);
    }

}

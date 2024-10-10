package devping.nnplanner.domain.user.service;

import devping.nnplanner.domain.auth.entity.User;
import devping.nnplanner.domain.auth.repository.UserRepository;
import devping.nnplanner.domain.user.dto.request.UserRequestDTO;
import devping.nnplanner.global.exception.CustomException;
import devping.nnplanner.global.exception.ErrorCode;
import devping.nnplanner.global.jwt.user.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public void checkPassword(UserDetailsImpl userDetails, UserRequestDTO userRequestDTO) {

        User user = userRepository.findById(userDetails.getUser().getUserId())
                                  .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        if (!passwordEncoder.matches(userRequestDTO.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.NOT_EQUALS_PASSWORD);
        }
    }

    @Transactional
    public void editPassword(UserDetailsImpl userDetails, UserRequestDTO userRequestDTO) {

        User user = userRepository.findById(userDetails.getUser().getUserId())
                                  .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        user.update(passwordEncoder.encode(userRequestDTO.getPassword()));

        userRepository.save(user);
    }
}

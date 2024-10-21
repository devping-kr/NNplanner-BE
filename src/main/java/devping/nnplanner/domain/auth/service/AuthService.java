package devping.nnplanner.domain.auth.service;

import devping.nnplanner.domain.auth.dto.request.AuthSignRequestDTO;
import devping.nnplanner.domain.auth.dto.response.AuthTokenResponseDTO;
import devping.nnplanner.domain.auth.entity.User;
import devping.nnplanner.domain.auth.entity.User.LoginType;
import devping.nnplanner.domain.auth.repository.EmailRepository;
import devping.nnplanner.domain.auth.repository.UserRepository;
import devping.nnplanner.domain.monthmenu.entity.MonthMenu;
import devping.nnplanner.domain.monthmenu.entity.MonthMenuHospital;
import devping.nnplanner.domain.monthmenu.entity.MonthMenuSchool;
import devping.nnplanner.domain.monthmenu.repository.MonthMenuHospitalRepository;
import devping.nnplanner.domain.monthmenu.repository.MonthMenuRepository;
import devping.nnplanner.domain.monthmenu.repository.MonthMenuSchoolRepository;
import devping.nnplanner.domain.survey.entity.Survey;
import devping.nnplanner.domain.survey.repository.SurveyRepository;
import devping.nnplanner.global.exception.CustomException;
import devping.nnplanner.global.exception.ErrorCode;
import devping.nnplanner.global.jwt.token.JwtUtil;
import devping.nnplanner.global.jwt.user.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
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
    private final JwtUtil jwtUtil;
    private final MonthMenuRepository monthMenuRepository;
    private final MonthMenuHospitalRepository monthMenuHospitalRepository;
    private final MonthMenuSchoolRepository monthMenuSchoolRepository;
    private final SurveyRepository surveyRepository;

    @Transactional
    public void signUp(AuthSignRequestDTO authSignRequestDTO) {

        if (userRepository.existsByEmail(authSignRequestDTO.getEmail())) {
            throw new CustomException(ErrorCode.ALREADY_EMAIL);
        }
        ;
        if (!authSignRequestDTO.getPassword().equals(authSignRequestDTO.getPasswordConfirm())) {
            throw new CustomException(ErrorCode.NOT_MATCH_PASSWORD);
        }
        ;
        if (!emailRepository.existsByEmail(authSignRequestDTO.getEmail())
            && emailRepository.existsByEmailAndVerifiedIsFalse(authSignRequestDTO.getEmail())) {
            throw new CustomException(ErrorCode.NOT_VERIFIED_EMAIL);
        }
        ;

        if (authSignRequestDTO.getLoginType().equals("LOCAL")) {

            User user = new User();

            user.create(
                authSignRequestDTO.getUsername(),
                authSignRequestDTO.getEmail(),
                passwordEncoder.encode(authSignRequestDTO.getPassword()),
                LoginType.LOCAL);

            user.setCreatedBy(authSignRequestDTO.getEmail());

            userRepository.save(user);
        }
    }

    public AuthTokenResponseDTO reissueToken(String refreshToken) {

        String reissueRefreshToken = jwtUtil.reissueRefreshToken(refreshToken);
        String reissueAccessToken = jwtUtil.reissueAccessToken(refreshToken);

        return new AuthTokenResponseDTO(reissueAccessToken, reissueRefreshToken);

    }

    public void logout(HttpServletRequest httpRequest, UserDetailsImpl userDetails) {

        Long userId = userDetails.getUser().getUserId();
        String email = userDetails.getUser().getEmail();

        jwtUtil.deleteRefreshToken(userId, email);
        jwtUtil.logoutAccessToken(httpRequest);
    }

    public void signOut(UserDetailsImpl userDetails) {

        User user = userRepository.findById(userDetails.getUser().getUserId())
                                  .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        List<MonthMenu> monthMenus =
            monthMenuRepository.findAllByUser_UserId(userDetails.getUser().getUserId());

        for (MonthMenu monthMenu : monthMenus) {

            List<MonthMenuHospital> monthMenuHospitals =
                monthMenuHospitalRepository.findAllByMonthMenu_MonthMenuId(
                    monthMenu.getMonthMenuId());
            monthMenuHospitalRepository.deleteAll(monthMenuHospitals);

            List<MonthMenuSchool> monthMenuSchools =
                monthMenuSchoolRepository.findAllByMonthMenu_MonthMenuId(
                    monthMenu.getMonthMenuId());
            monthMenuSchoolRepository.deleteAll(monthMenuSchools);

            List<Survey> surveys =
                surveyRepository.findAllByMonthMenu_MonthMenuId(monthMenu.getMonthMenuId());
            surveyRepository.deleteAll(surveys);

            monthMenuRepository.delete(monthMenu);
        }
        
        userRepository.delete(user);

    }

}

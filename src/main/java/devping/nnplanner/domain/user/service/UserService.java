package devping.nnplanner.domain.user.service;

import devping.nnplanner.domain.auth.entity.User;
import devping.nnplanner.domain.auth.repository.UserRepository;
import devping.nnplanner.domain.monthmenu.entity.MonthMenu;
import devping.nnplanner.domain.monthmenu.entity.MonthMenuHospital;
import devping.nnplanner.domain.monthmenu.entity.MonthMenuSchool;
import devping.nnplanner.domain.monthmenu.repository.MonthMenuHospitalRepository;
import devping.nnplanner.domain.monthmenu.repository.MonthMenuRepository;
import devping.nnplanner.domain.monthmenu.repository.MonthMenuSchoolRepository;
import devping.nnplanner.domain.survey.entity.Survey;
import devping.nnplanner.domain.survey.repository.SurveyRepository;
import devping.nnplanner.domain.user.dto.request.UserRequestDTO;
import devping.nnplanner.global.exception.CustomException;
import devping.nnplanner.global.exception.ErrorCode;
import devping.nnplanner.global.jwt.user.UserDetailsImpl;
import java.util.List;
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
    private final MonthMenuRepository monthMenuRepository;
    private final MonthMenuHospitalRepository monthMenuHospitalRepository;
    private final MonthMenuSchoolRepository monthMenuSchoolRepository;
    private final SurveyRepository surveyRepository;

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

    @Transactional
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

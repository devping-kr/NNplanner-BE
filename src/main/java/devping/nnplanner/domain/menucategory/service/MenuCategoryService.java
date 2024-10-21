package devping.nnplanner.domain.menucategory.service;

import devping.nnplanner.domain.openapi.repository.HospitalMenuRepository;
import devping.nnplanner.domain.openapi.repository.SchoolInfoRepository;
import devping.nnplanner.global.exception.CustomException;
import devping.nnplanner.global.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MenuCategoryService {

    private final HospitalMenuRepository hospitalMenuRepository;
    private final SchoolInfoRepository schoolInfoRepository;

    @Transactional(readOnly = true)
    public List<String> getMenuCategory(String majorCategory) {

        if (majorCategory.equals("병원")) {

            return hospitalMenuRepository.findDistinctHospitalMenuKinds();

        } else if (majorCategory.equals("학교")) {

            return schoolInfoRepository.findDistinctSchoolKindNames();

        } else if (majorCategory.equals("학교명")) {

            return schoolInfoRepository.findDistinctSchoolNames();

        } else {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
    }
}

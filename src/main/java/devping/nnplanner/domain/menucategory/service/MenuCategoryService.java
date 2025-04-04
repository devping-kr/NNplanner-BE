package devping.nnplanner.domain.menucategory.service;

import devping.nnplanner.domain.menucategory.entity.MenuCategory;
import devping.nnplanner.domain.menucategory.repository.MenuCategoryRepository;
import devping.nnplanner.domain.openapi.repository.HospitalMenuRepository;
import devping.nnplanner.domain.openapi.repository.SchoolInfoRepository;
import devping.nnplanner.global.exception.CustomException;
import devping.nnplanner.global.exception.ErrorCode;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MenuCategoryService {

    private final HospitalMenuRepository hospitalMenuRepository;
    private final SchoolInfoRepository schoolInfoRepository;
    private final MenuCategoryRepository menuCategoryRepository;

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

    @Async
    @Transactional
    public void addMenuCategories() {

        List<String> hospitalMenuKinds = hospitalMenuRepository.findDistinctHospitalMenuKinds();
        List<String> schoolKindNames = schoolInfoRepository.findDistinctSchoolKindNames();
        List<String> schoolNames = schoolInfoRepository.findDistinctSchoolNames();

        saveMenuCategories("병원", hospitalMenuKinds);
        saveMenuCategories("학교", schoolKindNames);
        saveMenuCategories("학교명", schoolNames);
    }

    private void saveMenuCategories(String majorCategory, List<String> minorCategories) {

        for (String minorCategory : minorCategories) {
            if (!menuCategoryRepository
                .existsByMajorCategoryAndMinorCategory(majorCategory, minorCategory)) {

                MenuCategory menuCategory = new MenuCategory();
                menuCategory.create(majorCategory, minorCategory);

                menuCategoryRepository.save(menuCategory);
            }
        }
    }

    @Transactional(readOnly = true)
    public List<String> getSchoolNameSuggestions(String keyword) {

        if (keyword.length() < 2) { // 최소 2글자 이상 입력해야 검색
            return Collections.emptyList();
        }
        return schoolInfoRepository.searchSchoolNames(keyword, 10);
    }
}

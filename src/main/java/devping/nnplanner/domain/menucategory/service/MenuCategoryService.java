package devping.nnplanner.domain.menucategory.service;

import devping.nnplanner.domain.menucategory.repository.MenuCategoryRepository;
import devping.nnplanner.domain.monthmenu.repository.MonthMenuRepository;
import devping.nnplanner.domain.openapi.repository.HospitalMenuRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MenuCategoryService {

    private final MenuCategoryRepository menuCategoryRepository;
    private final HospitalMenuRepository hospitalMenuRepository;
    private final MonthMenuRepository monthMenuRepository;

    @Transactional(readOnly = true)
    public List<String> getMenuCategory(String majorCategory) {

        if (majorCategory.equals("병원")) {

            return hospitalMenuRepository.findDistinctHospitalMenuKinds();


        } else {
            return null; //TODO: 학교,학교명인경우 추가
        }
    }
}

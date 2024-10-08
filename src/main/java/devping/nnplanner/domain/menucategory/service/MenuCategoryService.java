package devping.nnplanner.domain.menucategory.service;

import devping.nnplanner.domain.menucategory.entity.MenuCategory;
import devping.nnplanner.domain.menucategory.repository.MenuCategoryRepository;
import devping.nnplanner.domain.monthmenu.dto.response.MonthMenuPageResponseDTO;
import devping.nnplanner.domain.monthmenu.dto.response.MonthMenuResponseDTO;
import devping.nnplanner.domain.monthmenu.entity.MonthMenu;
import devping.nnplanner.domain.monthmenu.repository.MonthMenuRepository;
import devping.nnplanner.domain.openapi.repository.HospitalMenuRepository;
import devping.nnplanner.global.exception.CustomException;
import devping.nnplanner.global.exception.ErrorCode;
import devping.nnplanner.global.jwt.user.UserDetailsImpl;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MenuCategoryService {

    private final MenuCategoryRepository menuCategoryRepository;
    private final HospitalMenuRepository hospitalMenuRepository;
    private final MonthMenuRepository monthMenuRepository;

    public List<String> getMenuCategory(String majorCategory) {

        if (majorCategory.equals("병원")) {

            return hospitalMenuRepository.findDistinctHospitalMenuKinds();


        } else {
            return null; //TODO: 학교,학교명인경우 추가
        }
    }

    public MonthMenuPageResponseDTO getAllMonthMenuByCategoryId(UserDetailsImpl userDetails,
                                                                String majorCategory,
                                                                String minorCategory,
                                                                Pageable pageable) {

        MenuCategory menuCategory =
            menuCategoryRepository.findByMajorCategoryAndMinorCategory(majorCategory, minorCategory)
                                  .orElseThrow(() -> new CustomException((ErrorCode.NOT_FOUND)));

        Page<MonthMenu> monthMenuPage =
            monthMenuRepository.findAllByUser_UserIdAndMenuCategory(
                userDetails.getUser().getUserId(), menuCategory, pageable);

        List<MonthMenuResponseDTO> menuResponseDTOS =
            monthMenuPage.stream()
                         .map(page -> new MonthMenuResponseDTO(page, null))
                         .toList();

        return new MonthMenuPageResponseDTO(
            monthMenuPage.getNumber(),
            monthMenuPage.getTotalPages(),
            monthMenuPage.getTotalElements(),
            monthMenuPage.getSize(),
            menuResponseDTOS
        );
    }
}

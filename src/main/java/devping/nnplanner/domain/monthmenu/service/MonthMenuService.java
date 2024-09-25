package devping.nnplanner.domain.monthmenu.service;

import devping.nnplanner.domain.menucategory.entity.MenuCategory;
import devping.nnplanner.domain.menucategory.repository.MenuCategoryRepository;
import devping.nnplanner.domain.monthmenu.dto.request.MonthMenuAutoRequestDTO;
import devping.nnplanner.domain.monthmenu.dto.request.MonthMenuSaveRequestDTO;
import devping.nnplanner.domain.monthmenu.dto.response.FoodResponseDTO;
import devping.nnplanner.domain.monthmenu.dto.response.MonthFoodListResponseDTO;
import devping.nnplanner.domain.monthmenu.dto.response.MonthMenuAutoResponseDTO;
import devping.nnplanner.domain.monthmenu.dto.response.MonthMenuPageResponseDTO;
import devping.nnplanner.domain.monthmenu.dto.response.MonthMenuResponseDTO;
import devping.nnplanner.domain.monthmenu.entity.MonthMenu;
import devping.nnplanner.domain.monthmenu.entity.MonthMenuHospital;
import devping.nnplanner.domain.monthmenu.repository.MonthMenuHospitalRepository;
import devping.nnplanner.domain.monthmenu.repository.MonthMenuRepository;
import devping.nnplanner.domain.openapi.entity.Food;
import devping.nnplanner.domain.openapi.entity.HospitalMenu;
import devping.nnplanner.domain.openapi.repository.FoodRepository;
import devping.nnplanner.domain.openapi.repository.HospitalMenuRepository;
import devping.nnplanner.global.exception.CustomException;
import devping.nnplanner.global.exception.ErrorCode;
import devping.nnplanner.global.jwt.user.UserDetailsImpl;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MonthMenuService {

    private final FoodRepository foodRepository;
    private final MonthMenuRepository monthMenuRepository;
    private final HospitalMenuRepository hospitalMenuRepository;
    private final MenuCategoryRepository menuCategoryRepository;
    private final MonthMenuHospitalRepository monthMenuHospitalRepository;

    public List<MonthMenuAutoResponseDTO> createMonthMenuAuto(
        MonthMenuAutoRequestDTO requestDTO) {

        if (requestDTO.getMajorCategory().equals("병원")) {

            List<HospitalMenu> randomHospitalMenus =
                hospitalMenuRepository.findRandomHospitalMenusByCategory(
                    requestDTO.getMinorCategory(), requestDTO.getDayCount());

            return randomHospitalMenus.stream()
                                      .map(MonthMenuAutoResponseDTO::new)
                                      .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    public void saveMonthMenu(UserDetailsImpl userDetails, MonthMenuSaveRequestDTO requestDTO) {

        MenuCategory menuCategory =
            menuCategoryRepository.findByMajorCategoryAndMinorCategory(
                                      requestDTO.getMajorCategory(),
                                      requestDTO.getMinorCategory())
                                  .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        MonthMenu monthMenu = new MonthMenu();
        monthMenu.create(userDetails.getUser(), menuCategory, requestDTO.getMonthMenuName());
        monthMenuRepository.save(monthMenu);

        if (requestDTO.getMajorCategory().equals("병원")) {
            requestDTO.getMonthMenusSaveList().forEach(menuSaveDTO -> {
                if (menuSaveDTO.getHospitalMenuId() != null) {

                    HospitalMenu hospitalMenu =
                        hospitalMenuRepository
                            .findById(UUID.fromString(menuSaveDTO.getHospitalMenuId()))
                            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

                    MonthMenuHospital monthMenuHospital = new MonthMenuHospital();
                    monthMenuHospital.create(menuSaveDTO.getMenuDate(), monthMenu, hospitalMenu);
                    monthMenuHospitalRepository.save(monthMenuHospital);

                } else {

                    Food food1 = foodRepository.findById(UUID.fromString(menuSaveDTO.getFood1()))
                                               .orElse(null);
                    Food food2 = foodRepository.findById(UUID.fromString(menuSaveDTO.getFood2()))
                                               .orElse(null);
                    Food food3 = foodRepository.findById(UUID.fromString(menuSaveDTO.getFood3()))
                                               .orElse(null);
                    Food food4 = foodRepository.findById(UUID.fromString(menuSaveDTO.getFood4()))
                                               .orElse(null);
                    Food food5 = foodRepository.findById(UUID.fromString(menuSaveDTO.getFood5()))
                                               .orElse(null);
                    Food food6 = foodRepository.findById(UUID.fromString(menuSaveDTO.getFood6()))
                                               .orElse(null);
                    Food food7 = foodRepository.findById(UUID.fromString(menuSaveDTO.getFood7()))
                                               .orElse(null);

                    HospitalMenu hospitalMenu = HospitalMenu.builder()
                                                            .hospitalMenuKind(
                                                                requestDTO.getMinorCategory())
                                                            .food1(food1)
                                                            .food2(food2)
                                                            .food3(food3)
                                                            .food4(food4)
                                                            .food5(food5)
                                                            .food6(food6)
                                                            .food7(food7)
                                                            .build();

                    hospitalMenuRepository.save(hospitalMenu);

                    MonthMenuHospital monthMenuHospital = new MonthMenuHospital();
                    monthMenuHospital.create(menuSaveDTO.getMenuDate(), monthMenu, hospitalMenu);
                    monthMenuHospitalRepository.save(monthMenuHospital);
                }
            });
        }
    }

    public MonthMenuPageResponseDTO getAllMonthMenu(UserDetailsImpl userDetails,
                                                    Pageable pageable) {

        Page<MonthMenu> monthMenus =
            monthMenuRepository.findAllByUser_UserId(userDetails.getUser().getUserId(), pageable);

        System.out.println("MonthMenus: " + monthMenus.getContent());

        List<MonthMenuResponseDTO> menuResponseDTOList =
            monthMenus.stream().map(monthMenu ->
                          new MonthMenuResponseDTO(monthMenu, Collections.emptyList()))
                      .toList();

        System.out.println("MenuResponseDTOList: " + menuResponseDTOList);

        return new MonthMenuPageResponseDTO(
            monthMenus.getNumber(),
            monthMenus.getTotalPages(),
            monthMenus.getTotalElements(),
            monthMenus.getSize(),
            menuResponseDTOList);

    }

    public MonthMenuResponseDTO getMonthMenu(UUID monthMenuId) {

        MonthMenu monthMenu =
            monthMenuRepository.findById(monthMenuId)
                               .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        List<MonthMenuHospital> monthMenuHospitalList =
            monthMenuHospitalRepository.findAllByMonthMenu_MonthMenuId(monthMenuId);

        List<MonthFoodListResponseDTO> monthFoodListResponseDTOS =
            monthMenuHospitalList.stream().map(menu -> {

                HospitalMenu hospitalMenu = menu.getHospitalMenu();

                List<FoodResponseDTO> foodList = Stream.of(
                                                           hospitalMenu.getFood1(),
                                                           hospitalMenu.getFood2(),
                                                           hospitalMenu.getFood3(),
                                                           hospitalMenu.getFood4(),
                                                           hospitalMenu.getFood5(),
                                                           hospitalMenu.getFood6(),
                                                           hospitalMenu.getFood7()
                                                       )
                                                       .map(FoodResponseDTO::new)
                                                       .toList();

                return new MonthFoodListResponseDTO(menu, foodList);
            }).toList();

        return new MonthMenuResponseDTO(monthMenu, monthFoodListResponseDTOS);
    }


}



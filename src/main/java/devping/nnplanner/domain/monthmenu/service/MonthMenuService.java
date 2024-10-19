package devping.nnplanner.domain.monthmenu.service;

import devping.nnplanner.domain.menucategory.entity.MenuCategory;
import devping.nnplanner.domain.menucategory.repository.MenuCategoryRepository;
import devping.nnplanner.domain.monthmenu.dto.request.MonthMenuAutoRequestDTO;
import devping.nnplanner.domain.monthmenu.dto.request.MonthMenuSaveRequestDTO;
import devping.nnplanner.domain.monthmenu.dto.request.MonthMenuSaveRequestDTO.MonthMenusSave;
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
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
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

                    HospitalMenu hospitalMenu = createHospitalMenu(requestDTO, menuSaveDTO);

                    MonthMenuHospital monthMenuHospital = new MonthMenuHospital();
                    monthMenuHospital.create(menuSaveDTO.getMenuDate(), monthMenu, hospitalMenu);
                    monthMenuHospitalRepository.save(monthMenuHospital);
                }
            });
        }//TODO: majorCategory 별로 나눠서 저장해야함
    }

    @Transactional(readOnly = true)
    public MonthMenuPageResponseDTO getAllMonthMenu(UserDetailsImpl userDetails,
                                                    Pageable pageable) {

        Page<MonthMenu> monthMenus =
            monthMenuRepository.findAllByUser_UserId(userDetails.getUser().getUserId(), pageable);

        List<MonthMenuResponseDTO> menuResponseDTOList =
            monthMenus.stream()
                      .map(
                          monthMenu -> new MonthMenuResponseDTO(monthMenu, Collections.emptyList()))
                      .toList();

        return new MonthMenuPageResponseDTO(
            monthMenus.getNumber(),
            monthMenus.getTotalPages(),
            monthMenus.getTotalElements(),
            monthMenus.getSize(),
            menuResponseDTOList);
    }

    @Transactional(readOnly = true)
    public MonthMenuResponseDTO getMonthMenu(UUID monthMenuId) {

        MonthMenu monthMenu =
            monthMenuRepository.findById(monthMenuId)
                               .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        if (monthMenu.getMenuCategory().getMajorCategory().equals("병원")) {

            List<MonthMenuHospital> monthMenuHospitalList =
                monthMenuHospitalRepository.findAllByMonthMenu_MonthMenuId(monthMenuId);

            List<MonthFoodListResponseDTO> monthFoodListResponseDTOS =
                createMonthFoodListResponseDTOS(monthMenuHospitalList);

            return new MonthMenuResponseDTO(monthMenu, monthFoodListResponseDTOS);
        } else {
            //TODO: majorCategory 별로 나눠서 조회해야함
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
    }

    @Transactional
    public MonthMenuResponseDTO updateMonthMenu(UUID monthMenuId,
                                                MonthMenuSaveRequestDTO requestDTO) {

        MonthMenu monthMenu =
            monthMenuRepository.findById(monthMenuId)
                               .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        MenuCategory menuCategory =
            menuCategoryRepository.findByMajorCategoryAndMinorCategory(
                                      requestDTO.getMajorCategory(), requestDTO.getMinorCategory())
                                  .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        monthMenu.update(menuCategory, requestDTO.getMonthMenuName());

        monthMenuRepository.save(monthMenu);

        if (monthMenu.getMenuCategory().getMajorCategory().equals("병원")) {

            requestDTO.getMonthMenusSaveList().forEach(menuSaveDTO -> {

                if (menuSaveDTO.getHospitalMenuId() == null) {

                    MonthMenuHospital monthMenuHospital =
                        monthMenuHospitalRepository
                            .findByMonthMenu_MonthMenuIdAndMenuDate(monthMenuId,
                                menuSaveDTO.getMenuDate())
                            .orElseThrow(() ->
                                new CustomException(ErrorCode.NOT_FOUND));

                    monthMenuHospitalRepository.delete(monthMenuHospital);

                    if (monthMenuHospital.getHospitalMenu().getCreatedBy() != null
                        && !monthMenuHospital.getHospitalMenu().getCreatedBy().isEmpty()) {
                        hospitalMenuRepository.delete(monthMenuHospital.getHospitalMenu());
                    }

                    HospitalMenu hospitalMenu = createHospitalMenu(requestDTO, menuSaveDTO);

                    MonthMenuHospital saveMonthMenuHospital = new MonthMenuHospital();
                    saveMonthMenuHospital.create(
                        menuSaveDTO.getMenuDate(), monthMenu, hospitalMenu);
                    monthMenuHospitalRepository.save(saveMonthMenuHospital);
                }
            });

            List<MonthMenuHospital> monthMenuHospitalList =
                monthMenuHospitalRepository.findAllByMonthMenu_MonthMenuId(monthMenuId);

            List<MonthFoodListResponseDTO> monthFoodListResponseDTOS =
                createMonthFoodListResponseDTOS(monthMenuHospitalList);

            return new MonthMenuResponseDTO(monthMenu, monthFoodListResponseDTOS);
        } else {
            //TODO: majorCategory 별로 나눠서 업데이트 해야함
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
    }

    @Transactional
    public void deleteMonthMenu(UUID monthMenuId) {

        MonthMenu monthMenu =
            monthMenuRepository.findById(monthMenuId)
                               .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        if (monthMenu.getMenuCategory().getMajorCategory().equals("병원")) {

            List<MonthMenuHospital> monthMenuHospitalList =
                monthMenuHospitalRepository
                    .findAllByMonthMenu_MonthMenuId(monthMenu.getMonthMenuId());

            monthMenuHospitalList.forEach(monthMenuHospital -> {

                HospitalMenu hospitalMenu =
                    hospitalMenuRepository
                        .findById(monthMenuHospital.getHospitalMenu().getHospitalMenuId())
                        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

                monthMenuHospitalRepository.delete(monthMenuHospital);

                if (hospitalMenu.getCreatedBy() != null) {
                    hospitalMenuRepository.delete(hospitalMenu);
                }
            });

            monthMenuRepository.delete(monthMenu);
        }
        //TODO: 학교식단 삭제 구현해야함
    }

    @Transactional(readOnly = true)
    public Integer countMonthMenu(UserDetailsImpl userDetails) {

        return monthMenuRepository.countByUser_UserId(userDetails.getUser().getUserId());
    }

    @Transactional(readOnly = true)
    public List<FoodResponseDTO> searchFood(String foodName, Pageable pageable) {

        Page<Food> foodPage = foodRepository.findBySearchFood(foodName, pageable);

        return foodPage.stream().map(FoodResponseDTO::new).toList();
    }

    private HospitalMenu createHospitalMenu(MonthMenuSaveRequestDTO requestDTO,
                                            MonthMenusSave menusSave) {

        Food food1 = foodRepository.findById(UUID.fromString(menusSave.getFood1()))
                                   .orElse(null);
        Food food2 = foodRepository.findById(UUID.fromString(menusSave.getFood2()))
                                   .orElse(null);
        Food food3 = foodRepository.findById(UUID.fromString(menusSave.getFood3()))
                                   .orElse(null);
        Food food4 = foodRepository.findById(UUID.fromString(menusSave.getFood4()))
                                   .orElse(null);
        Food food5 = foodRepository.findById(UUID.fromString(menusSave.getFood5()))
                                   .orElse(null);
        Food food6 = foodRepository.findById(UUID.fromString(menusSave.getFood6()))
                                   .orElse(null);
        Food food7 = foodRepository.findById(UUID.fromString(menusSave.getFood7()))
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

        return hospitalMenu;
    }

    private List<MonthFoodListResponseDTO> createMonthFoodListResponseDTOS(
        List<MonthMenuHospital> monthMenuHospitalList) {

        return monthMenuHospitalList.stream().map(MHMenu -> {

            HospitalMenu hospitalMenu = MHMenu.getHospitalMenu();

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

            return new MonthFoodListResponseDTO(MHMenu, foodList);
        }).toList();
    }
}

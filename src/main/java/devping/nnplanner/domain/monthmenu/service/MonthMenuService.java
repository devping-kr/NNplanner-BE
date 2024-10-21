package devping.nnplanner.domain.monthmenu.service;

import devping.nnplanner.domain.menucategory.entity.MenuCategory;
import devping.nnplanner.domain.menucategory.repository.MenuCategoryRepository;
import devping.nnplanner.domain.monthmenu.dto.request.MonthCountRequestDTO;
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
import devping.nnplanner.domain.monthmenu.entity.MonthMenuSchool;
import devping.nnplanner.domain.monthmenu.repository.MonthMenuHospitalRepository;
import devping.nnplanner.domain.monthmenu.repository.MonthMenuRepository;
import devping.nnplanner.domain.monthmenu.repository.MonthMenuSchoolRepository;
import devping.nnplanner.domain.openapi.entity.Food;
import devping.nnplanner.domain.openapi.entity.HospitalMenu;
import devping.nnplanner.domain.openapi.entity.SchoolInfo;
import devping.nnplanner.domain.openapi.entity.SchoolMenu;
import devping.nnplanner.domain.openapi.repository.FoodRepository;
import devping.nnplanner.domain.openapi.repository.HospitalMenuRepository;
import devping.nnplanner.domain.openapi.repository.SchoolInfoRepository;
import devping.nnplanner.domain.openapi.repository.SchoolMenuRepository;
import devping.nnplanner.domain.survey.entity.Survey;
import devping.nnplanner.domain.survey.repository.SurveyRepository;
import devping.nnplanner.global.exception.CustomException;
import devping.nnplanner.global.exception.ErrorCode;
import devping.nnplanner.global.jwt.user.UserDetailsImpl;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
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
    private final SchoolMenuRepository schoolMenuRepository;
    private final MonthMenuSchoolRepository monthMenuSchoolRepository;
    private final SchoolInfoRepository schoolInfoRepository;
    private final SurveyRepository surveyRepository;

    public List<MonthMenuAutoResponseDTO> createHospitalMonthMenuAuto(
        MonthMenuAutoRequestDTO requestDTO) {

        switch (requestDTO.getMajorCategory()) {
            case "병원" -> {

                List<HospitalMenu> randomHospitalMenus =
                    hospitalMenuRepository.findRandomHospitalMenusByCategory(
                        requestDTO.getMinorCategory(), requestDTO.getDayCount());

                return randomHospitalMenus.stream()
                                          .map(menu -> {
                                              MonthMenuAutoResponseDTO monthMenuAutoResponseDTO
                                                  = new MonthMenuAutoResponseDTO();

                                              monthMenuAutoResponseDTO.hospitalMenu(menu);

                                              return monthMenuAutoResponseDTO;
                                          })
                                          .toList();
            }
            case "학교" -> {

                List<SchoolMenu> randomSchoolMenus =
                    schoolMenuRepository.findRandomSchoolMenusBySchoolKindName(
                        requestDTO.getMinorCategory(), requestDTO.getDayCount());

                return randomSchoolMenus.stream()
                                        .map(menu -> {
                                            MonthMenuAutoResponseDTO monthMenuAutoResponseDTO
                                                = new MonthMenuAutoResponseDTO();

                                            monthMenuAutoResponseDTO.schoolKindName(menu);

                                            return monthMenuAutoResponseDTO;
                                        })
                                        .toList();

            }
            case "학교명" -> {

                List<SchoolMenu> randomSchoolMenus =
                    schoolMenuRepository.findRandomSchoolMenusBySchoolName(
                        requestDTO.getMinorCategory(), requestDTO.getDayCount());

                return randomSchoolMenus.stream()
                                        .map(menu -> {
                                            MonthMenuAutoResponseDTO monthMenuAutoResponseDTO
                                                = new MonthMenuAutoResponseDTO();

                                            monthMenuAutoResponseDTO.schoolName(menu);

                                            return monthMenuAutoResponseDTO;
                                        })
                                        .toList();
            }
            default -> throw new CustomException(ErrorCode.BAD_REQUEST);
        }
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

        switch (requestDTO.getMajorCategory()) {
            case "병원" -> requestDTO.getMonthMenusSaveList().forEach(menuSaveDTO -> {
                if (menuSaveDTO.getMenuId() != null) {

                    HospitalMenu hospitalMenu =
                        hospitalMenuRepository
                            .findById(UUID.fromString(menuSaveDTO.getMenuId()))
                            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

                    MonthMenuHospital monthMenuHospital = new MonthMenuHospital();
                    monthMenuHospital.create(menuSaveDTO.getMenuDate(), monthMenu,
                        hospitalMenu);
                    monthMenuHospitalRepository.save(monthMenuHospital);

                } else {

                    HospitalMenu hospitalMenu = createHospitalMenu(requestDTO, menuSaveDTO);

                    MonthMenuHospital monthMenuHospital = new MonthMenuHospital();
                    monthMenuHospital.create(menuSaveDTO.getMenuDate(), monthMenu,
                        hospitalMenu);
                    monthMenuHospitalRepository.save(monthMenuHospital);
                }
            });
            case "학교" -> requestDTO.getMonthMenusSaveList().forEach(menuSaveDTO -> {

                if (menuSaveDTO.getMenuId() != null) {

                    SchoolMenu schoolMenu =
                        schoolMenuRepository
                            .findById(UUID.fromString(menuSaveDTO.getMenuId()))
                            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

                    MonthMenuSchool monthMenuSchool = new MonthMenuSchool();
                    monthMenuSchool.create(menuSaveDTO.getMenuDate(), monthMenu, schoolMenu);
                    monthMenuSchoolRepository.save(monthMenuSchool);

                } else {

                    SchoolInfo schoolInfo =
                        schoolInfoRepository
                            .findTop1BySchoolKindName(requestDTO.getMinorCategory())
                            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

                    SchoolMenu schoolMenu = createSchoolMenu(schoolInfo, menuSaveDTO);

                    MonthMenuSchool monthMenuSchool = new MonthMenuSchool();
                    monthMenuSchool.create(menuSaveDTO.getMenuDate(), monthMenu, schoolMenu);
                    monthMenuSchoolRepository.save(monthMenuSchool);
                }
            });
            case "학교명" -> requestDTO.getMonthMenusSaveList().forEach(menuSaveDTO -> {

                if (menuSaveDTO.getMenuId() != null) {

                    SchoolMenu schoolMenu =
                        schoolMenuRepository
                            .findById(UUID.fromString(menuSaveDTO.getMenuId()))
                            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

                    MonthMenuSchool monthMenuSchool = new MonthMenuSchool();
                    monthMenuSchool.create(menuSaveDTO.getMenuDate(), monthMenu, schoolMenu);
                    monthMenuSchoolRepository.save(monthMenuSchool);

                } else {

                    SchoolInfo schoolInfo =
                        schoolInfoRepository
                            .findBySchoolName(requestDTO.getMinorCategory())
                            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

                    SchoolMenu schoolMenu = createSchoolMenu(schoolInfo, menuSaveDTO);

                    MonthMenuSchool monthMenuSchool = new MonthMenuSchool();
                    monthMenuSchool.create(menuSaveDTO.getMenuDate(), monthMenu, schoolMenu);
                    monthMenuSchoolRepository.save(monthMenuSchool);
                }
            });
            default -> throw new CustomException(ErrorCode.BAD_REQUEST);
        }
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
                createHospitalMonthFoodList(monthMenuHospitalList);

            return new MonthMenuResponseDTO(monthMenu, monthFoodListResponseDTOS);

        } else if (monthMenu.getMenuCategory().getMajorCategory().equals("학교") ||
            monthMenu.getMenuCategory().getMajorCategory().equals("학교명")) {

            List<MonthMenuSchool> monthMenuSchoolList =
                monthMenuSchoolRepository.findAllByMonthMenu_MonthMenuId(monthMenuId);

            List<MonthFoodListResponseDTO> monthFoodListResponseDTOS =
                createSchoolMonthFoodList(monthMenuSchoolList);

            return new MonthMenuResponseDTO(monthMenu, monthFoodListResponseDTOS);

        } else {
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

                if (menuSaveDTO.getMenuId() == null) {

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
                createHospitalMonthFoodList(monthMenuHospitalList);

            return new MonthMenuResponseDTO(monthMenu, monthFoodListResponseDTOS);

        } else if (monthMenu.getMenuCategory().getMajorCategory().equals("학교") ||
            monthMenu.getMenuCategory().getMajorCategory().equals("학교명")) {

            requestDTO.getMonthMenusSaveList().forEach(menuSaveDTO -> {

                if (menuSaveDTO.getMenuId() == null) {

                    MonthMenuSchool monthMenuSchool =
                        monthMenuSchoolRepository
                            .findByMonthMenu_MonthMenuIdAndMenuDate(monthMenuId,
                                menuSaveDTO.getMenuDate())
                            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

                    SchoolInfo schoolInfo = monthMenuSchool.getSchoolMenu().getSchoolInfo();

                    monthMenuSchoolRepository.delete(monthMenuSchool);

                    if (monthMenuSchool.getSchoolMenu().getCreatedBy() != null
                        && !monthMenuSchool.getSchoolMenu().getCreatedBy().isEmpty()) {
                        schoolMenuRepository.delete(monthMenuSchool.getSchoolMenu());
                    }

                    SchoolMenu schoolMenu = createSchoolMenu(schoolInfo, menuSaveDTO);

                    MonthMenuSchool saveMonthMenuSchool = new MonthMenuSchool();
                    saveMonthMenuSchool.create(menuSaveDTO.getMenuDate(), monthMenu, schoolMenu);
                    monthMenuSchoolRepository.save(saveMonthMenuSchool);
                }
            });

            List<MonthMenuSchool> monthMenuSchoolList =
                monthMenuSchoolRepository.findAllByMonthMenu_MonthMenuId(monthMenuId);

            List<MonthFoodListResponseDTO> monthFoodListResponseDTOS =
                createSchoolMonthFoodList(monthMenuSchoolList);

            return new MonthMenuResponseDTO(monthMenu, monthFoodListResponseDTOS);

        } else {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
    }

    @Transactional
    public void deleteMonthMenu(UUID monthMenuId) {

        MonthMenu monthMenu =
            monthMenuRepository.findById(monthMenuId)
                               .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        List<Survey> surveys =
            surveyRepository.findAllByMonthMenu_MonthMenuId(monthMenu.getMonthMenuId());

        surveyRepository.deleteAll(surveys);

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
        } else if (monthMenu.getMenuCategory().getMajorCategory().equals("학교") ||
            monthMenu.getMenuCategory().getMajorCategory().equals("학교명")) {

            List<MonthMenuSchool> monthMenuSchoolList =
                monthMenuSchoolRepository.findAllByMonthMenu_MonthMenuId(
                    monthMenu.getMonthMenuId());

            monthMenuSchoolList.forEach(monthMenuSchool -> {

                SchoolMenu schoolMenu =
                    schoolMenuRepository
                        .findById(monthMenuSchool.getSchoolMenu().getSchoolMenuId())
                        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

                monthMenuSchoolRepository.delete(monthMenuSchool);

                if (schoolMenu.getCreatedBy() != null) {
                    schoolMenuRepository.delete(schoolMenu);
                }
            });

            monthMenuRepository.delete(monthMenu);

        } else {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
    }

    @Transactional(readOnly = true)
    public MonthCountRequestDTO countMonthMenu(UserDetailsImpl userDetails) {

        Integer totalMonthCount =
            monthMenuRepository.countByUser_UserId(userDetails.getUser().getUserId());

        // 이번 달의 시작과 끝 날짜 구하기
        LocalDate now = LocalDate.now();
        LocalDate firstDayOfThisMonth = now.withDayOfMonth(1);
        LocalDateTime startOfCurrentMonth = firstDayOfThisMonth.atStartOfDay();
        LocalDateTime endOfCurrentMonth = now.plusMonths(1).withDayOfMonth(1).atStartOfDay();

        // 저번 달의 시작과 끝 날짜 구하기
        LocalDate firstDayOfLastMonth = now.minusMonths(1).withDayOfMonth(1);
        LocalDateTime startOfLastMonth = firstDayOfLastMonth.atStartOfDay();
        LocalDateTime endOfLastMonth = firstDayOfThisMonth.atStartOfDay();

        Integer currentMonthCount = monthMenuRepository.countByUser_UserIdAndCreatedAtBetween(
            userDetails.getUser().getUserId(), startOfCurrentMonth, endOfCurrentMonth);

        Integer lastMonthCount = monthMenuRepository.countByUser_UserIdAndCreatedAtBetween(
            userDetails.getUser().getUserId(), startOfLastMonth, endOfLastMonth);

        return new MonthCountRequestDTO(totalMonthCount, currentMonthCount, lastMonthCount);
    }

    @Transactional(readOnly = true)
    public List<FoodResponseDTO> searchFood(String foodName, Pageable pageable) {

        Page<Food> foodPage = foodRepository.findBySearchFood(foodName, pageable);

        return foodPage.stream().map(FoodResponseDTO::new).toList();
    }

    @Transactional(readOnly = true)
    public MonthMenuPageResponseDTO searchMonthMenus(UserDetailsImpl userDetails,
                                                     String majorCategory,
                                                     String minorCategory,
                                                     String menuName,
                                                     Integer year,
                                                     Integer month,
                                                     Pageable pageable) {

        Page<MonthMenu> monthMenuPage =
            monthMenuRepository.searchByMenuNameOrDateAndCategory(
                userDetails.getUser().getUserId(),
                majorCategory,
                minorCategory,
                menuName,
                year,
                month,
                pageable);

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

    private HospitalMenu createHospitalMenu(MonthMenuSaveRequestDTO requestDTO,
                                            MonthMenusSave menusSave) {

        Food food1 = foodRepository.findById(UUID.fromString(menusSave.getFood1()))
                                   .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        Food food2 = foodRepository.findById(UUID.fromString(menusSave.getFood2()))
                                   .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        Food food3 = foodRepository.findById(UUID.fromString(menusSave.getFood3()))
                                   .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        Food food4 = foodRepository.findById(UUID.fromString(menusSave.getFood4()))
                                   .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        Food food5 = foodRepository.findById(UUID.fromString(menusSave.getFood5()))
                                   .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        Food food6 = foodRepository.findById(UUID.fromString(menusSave.getFood6()))
                                   .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        Food food7 = foodRepository.findById(UUID.fromString(menusSave.getFood7()))
                                   .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

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

    private SchoolMenu createSchoolMenu(SchoolInfo schoolInfo,
                                        MonthMenusSave menusSave) {

        Food food1 = foodRepository.findById(UUID.fromString(menusSave.getFood1()))
                                   .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        Food food2 = foodRepository.findById(UUID.fromString(menusSave.getFood2()))
                                   .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        Food food3 = foodRepository.findById(UUID.fromString(menusSave.getFood3()))
                                   .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        Food food4 = foodRepository.findById(UUID.fromString(menusSave.getFood4()))
                                   .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        Food food5 = foodRepository.findById(UUID.fromString(menusSave.getFood5()))
                                   .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        Food food6 = foodRepository.findById(UUID.fromString(menusSave.getFood6()))
                                   .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
        Food food7 = foodRepository.findById(UUID.fromString(menusSave.getFood7()))
                                   .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        SchoolMenu schoolMenu = new SchoolMenu();
        schoolMenu.create(
            schoolInfo,
            food1,
            food2,
            food3,
            food4,
            food5,
            food6,
            food7
        );

        schoolMenuRepository.save(schoolMenu);

        return schoolMenu;
    }

    private List<MonthFoodListResponseDTO> createHospitalMonthFoodList(
        List<MonthMenuHospital> monthMenuHospitalList) {

        return monthMenuHospitalList.stream().map(MHMenu -> {

            HospitalMenu hospitalMenu = MHMenu.getHospitalMenu();

            List<FoodResponseDTO> foodList =
                Stream.of(
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

            MonthFoodListResponseDTO monthFoodListResponseDTO = new MonthFoodListResponseDTO();
            monthFoodListResponseDTO.monthMenuHospital(MHMenu, foodList);

            return monthFoodListResponseDTO;
        }).toList();
    }

    private List<MonthFoodListResponseDTO> createSchoolMonthFoodList(
        List<MonthMenuSchool> monthMenuSchoolList) {

        return monthMenuSchoolList
            .stream().map(monthMenuSchool -> {

                List<FoodResponseDTO> foodList =
                    Stream.of(
                              monthMenuSchool.getSchoolMenu().getFood1(),
                              monthMenuSchool.getSchoolMenu().getFood2(),
                              monthMenuSchool.getSchoolMenu().getFood3(),
                              monthMenuSchool.getSchoolMenu().getFood4(),
                              monthMenuSchool.getSchoolMenu().getFood5(),
                              monthMenuSchool.getSchoolMenu().getFood6(),
                              monthMenuSchool.getSchoolMenu().getFood7()
                          )
                          .map(FoodResponseDTO::new)
                          .toList();

                MonthFoodListResponseDTO monthFoodListResponseDTO = new MonthFoodListResponseDTO();
                monthFoodListResponseDTO.monthMenuSchool(monthMenuSchool,
                    foodList);

                return monthFoodListResponseDTO;
            })
            .toList();
    }
}

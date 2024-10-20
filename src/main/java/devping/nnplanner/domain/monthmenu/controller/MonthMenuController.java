package devping.nnplanner.domain.monthmenu.controller;

import devping.nnplanner.domain.monthmenu.dto.request.MonthMenuAutoRequestDTO;
import devping.nnplanner.domain.monthmenu.dto.request.MonthMenuSaveRequestDTO;
import devping.nnplanner.domain.monthmenu.dto.response.FoodResponseDTO;
import devping.nnplanner.domain.monthmenu.dto.response.HospitalMonthMenuAutoResponseDTO;
import devping.nnplanner.domain.monthmenu.dto.response.MonthMenuPageResponseDTO;
import devping.nnplanner.domain.monthmenu.dto.response.MonthMenuResponseDTO;
import devping.nnplanner.domain.monthmenu.dto.response.SchoolMonthMenuAutoResponseDTO;
import devping.nnplanner.domain.monthmenu.service.MonthMenuService;
import devping.nnplanner.domain.survey.service.SurveyService;
import devping.nnplanner.global.jwt.user.UserDetailsImpl;
import devping.nnplanner.global.response.ApiResponse;
import devping.nnplanner.global.response.GlobalResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/api/month-menus")
@RestController
@RequiredArgsConstructor
public class MonthMenuController {

    private final MonthMenuService monthMenuService;
    private final SurveyService surveyService;

    @PostMapping("/hospital-auto")
    public ResponseEntity<ApiResponse<List<HospitalMonthMenuAutoResponseDTO>>> createHospitalMonthMenuAuto(
        @RequestBody @Valid MonthMenuAutoRequestDTO monthMenuAutoRequestDTO) {

        List<HospitalMonthMenuAutoResponseDTO> hospitalMonthMenuAutoResponseDTO =
            monthMenuService.createHospitalMonthMenuAuto(monthMenuAutoRequestDTO);

        return GlobalResponse.CREATED("병원 자동 식단 생성 성공", hospitalMonthMenuAutoResponseDTO);
    }

    @PostMapping("/school-auto")
    public ResponseEntity<ApiResponse<List<SchoolMonthMenuAutoResponseDTO>>> createSchoolMonthMenuAuto(
        @RequestBody @Valid MonthMenuAutoRequestDTO monthMenuAutoRequestDTO) {

        List<SchoolMonthMenuAutoResponseDTO> hospitalMonthMenuAutoResponseDTO =
            monthMenuService.createSchoolMonthMenuAuto(monthMenuAutoRequestDTO);

        return GlobalResponse.CREATED("학교 자동 식단 생성 성공", hospitalMonthMenuAutoResponseDTO);
    }

    @PostMapping("/save")
    public ResponseEntity<ApiResponse<Void>> saveMonthMenuAuto(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestBody @Valid MonthMenuSaveRequestDTO monthMenuSaveRequestDTO) {

        monthMenuService.saveMonthMenu(userDetails, monthMenuSaveRequestDTO);

        return GlobalResponse.OK("월별 식단 저장 성공", null);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<MonthMenuPageResponseDTO>> getAllMonthMenu(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PageableDefault(page = 0, size = 8, sort = "createdAt",
            direction = Sort.Direction.DESC) Pageable pageable) {

        MonthMenuPageResponseDTO monthMenuPageResponseDTO =
            monthMenuService.getAllMonthMenu(userDetails, pageable);

        return GlobalResponse.OK("작성한 식단 전체 조회 성공", monthMenuPageResponseDTO);
    }

    @GetMapping("/{monthMenuId}")
    public ResponseEntity<ApiResponse<MonthMenuResponseDTO>> getMonthMenu(
        @PathVariable("monthMenuId") UUID monthMenuId) {

        MonthMenuResponseDTO monthMenuResponseDTO =
            monthMenuService.getMonthMenu(monthMenuId);

        return GlobalResponse.OK("작성한 식단 상세 조회 성공", monthMenuResponseDTO);
    }

    @PutMapping("/{monthMenuId}")
    public ResponseEntity<ApiResponse<MonthMenuResponseDTO>> updateMonthMenu(
        @PathVariable("monthMenuId") UUID monthMenuId,
        @RequestBody MonthMenuSaveRequestDTO monthMenuSaveRequestDTO) {

        MonthMenuResponseDTO monthMenuResponseDTO =
            monthMenuService.updateMonthMenu(monthMenuId, monthMenuSaveRequestDTO);

        return GlobalResponse.OK("월별 식단 수정 성공", monthMenuResponseDTO);
    }

    @DeleteMapping("/{monthMenuId}")
    public ResponseEntity<ApiResponse<Void>> deleteMonthMenu(
        @PathVariable("monthMenuId") UUID monthMenuId) {

        monthMenuService.deleteMonthMenu(monthMenuId);

        return GlobalResponse.OK("월별 식단 삭제 성공", null);
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Integer>> countMonthMenu(
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        Integer count = monthMenuService.countMonthMenu(userDetails);

        return GlobalResponse.OK("월별 식단 카운트 성공", count);
    }

    @GetMapping("/foods")
    public ResponseEntity<ApiResponse<List<FoodResponseDTO>>> searchFood(
        @RequestParam(name = "foodName") String foodName,
        @PageableDefault(page = 0, size = 3, sort = "createdAt",
            direction = Sort.Direction.DESC) Pageable pageable) {

        List<FoodResponseDTO> foodResponseDTOList = monthMenuService.searchFood(foodName, pageable);

        return GlobalResponse.OK("음식 정보 검색 성공", foodResponseDTOList);
    }
}

package devping.nnplanner.domain.monthmenu.controller;

import devping.nnplanner.domain.monthmenu.dto.request.MonthMenuAutoRequestDTO;
import devping.nnplanner.domain.monthmenu.dto.request.MonthMenuSaveRequestDTO;
import devping.nnplanner.domain.monthmenu.dto.response.FoodResponseDTO;
import devping.nnplanner.domain.monthmenu.dto.response.MonthCountResponseDTO;
import devping.nnplanner.domain.monthmenu.dto.response.MonthMenuAutoResponseDTO;
import devping.nnplanner.domain.monthmenu.dto.response.MonthMenuPageResponseDTO;
import devping.nnplanner.domain.monthmenu.dto.response.MonthMenuResponseDTO;
import devping.nnplanner.domain.monthmenu.service.MonthMenuService;
import devping.nnplanner.global.jwt.user.UserDetailsImpl;
import devping.nnplanner.global.response.ApiResponse;
import devping.nnplanner.global.response.GlobalResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
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
@Tag(name = "MonthMenu", description = "월별 메뉴 API")
public class MonthMenuController {

    private final MonthMenuService monthMenuService;

    @PostMapping("/auto")
    public ResponseEntity<ApiResponse<List<MonthMenuAutoResponseDTO>>> createHospitalMonthMenuAuto(
        @RequestBody @Valid MonthMenuAutoRequestDTO monthMenuAutoRequestDTO) {

        List<MonthMenuAutoResponseDTO> monthMenuAutoResponseDTO =
            monthMenuService.createHospitalMonthMenuAuto(monthMenuAutoRequestDTO);

        return GlobalResponse.CREATED("자동 식단 생성 성공", monthMenuAutoResponseDTO);
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

        int correctedPage = (pageable.getPageNumber() > 0) ? pageable.getPageNumber() - 1 : 0;

        Pageable updatedPageable =
            PageRequest.of(correctedPage, pageable.getPageSize(), pageable.getSort());

        MonthMenuPageResponseDTO monthMenuPageResponseDTO =
            monthMenuService.getAllMonthMenu(userDetails, updatedPageable);

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
    public ResponseEntity<ApiResponse<MonthCountResponseDTO>> countMonthMenu(
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        MonthCountResponseDTO monthCountResponseDTO = monthMenuService.countMonthMenu(userDetails);

        return GlobalResponse.OK("월별 식단 카운트 성공", monthCountResponseDTO);
    }

    @GetMapping("/foods")
    public ResponseEntity<ApiResponse<List<FoodResponseDTO>>> searchFood(
        @RequestParam(name = "foodName") String foodName,
        @PageableDefault(page = 0, size = 3, sort = "createdAt",
            direction = Sort.Direction.DESC) Pageable pageable) {

        int correctedPage = (pageable.getPageNumber() > 0) ? pageable.getPageNumber() - 1 : 0;

        Pageable updatedPageable =
            PageRequest.of(correctedPage, pageable.getPageSize(), pageable.getSort());

        List<FoodResponseDTO> foodResponseDTOList = monthMenuService.searchFood(foodName,
            updatedPageable);

        return GlobalResponse.OK("음식 정보 검색 성공", foodResponseDTOList);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<MonthMenuPageResponseDTO>> searchMonthMenus(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam(required = false) String majorCategory,
        @RequestParam(required = false) String minorCategory,
        @RequestParam(required = false) String menuName,
        @RequestParam(required = false) Integer year,
        @RequestParam(required = false) Integer month,
        @PageableDefault(page = 0, size = 8,
            sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {

        int correctedPage = (pageable.getPageNumber() > 0) ? pageable.getPageNumber() - 1 : 0;

        Pageable updatedPageable = PageRequest.of(correctedPage, pageable.getPageSize(),
            pageable.getSort());

        MonthMenuPageResponseDTO monthMenuPageResponseDTO =
            monthMenuService
                .searchMonthMenus(userDetails, majorCategory, minorCategory, menuName, year, month,
                    updatedPageable);

        return GlobalResponse.OK("식단 검색 결과 조회 성공", monthMenuPageResponseDTO);
    }
}

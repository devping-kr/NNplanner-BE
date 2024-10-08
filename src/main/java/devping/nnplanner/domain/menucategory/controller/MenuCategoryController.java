package devping.nnplanner.domain.menucategory.controller;

import devping.nnplanner.domain.menucategory.service.MenuCategoryService;
import devping.nnplanner.domain.monthmenu.dto.response.MonthMenuPageResponseDTO;
import devping.nnplanner.global.jwt.user.UserDetailsImpl;
import devping.nnplanner.global.response.ApiResponse;
import devping.nnplanner.global.response.GlobalResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/menu-categories")
public class MenuCategoryController {

    private final MenuCategoryService menuCategoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<String>>> getMenuCategory(
        @RequestParam("major-category") String majorCategory) {

        List<String> menuCategory = menuCategoryService.getMenuCategory(majorCategory);

        return GlobalResponse.OK("소분류 목록 조회 성공", menuCategory);
    }

    @GetMapping("/month-menus")
    public ResponseEntity<ApiResponse<MonthMenuPageResponseDTO>> getAllMonthMenuByCategoryId(
        @RequestParam("major-category") String majorCategory,
        @RequestParam("minor-category") String minorCategory,
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PageableDefault(page = 0, size = 8, sort = "createdAt",
            direction = Sort.Direction.DESC) Pageable pageable) {

        MonthMenuPageResponseDTO monthMenuPageResponseDTO =
            menuCategoryService.getAllMonthMenuByCategoryId(
                userDetails,
                majorCategory,
                minorCategory,
                pageable);

        return GlobalResponse.OK("작성한 식단 카테고리별 전체 조회 성공", monthMenuPageResponseDTO);
    }
}

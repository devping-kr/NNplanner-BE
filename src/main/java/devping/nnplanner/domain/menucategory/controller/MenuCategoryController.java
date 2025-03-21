package devping.nnplanner.domain.menucategory.controller;

import devping.nnplanner.domain.menucategory.service.MenuCategoryService;
import devping.nnplanner.global.response.ApiResponse;
import devping.nnplanner.global.response.GlobalResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/menu-categories")
@RequiredArgsConstructor
@RestController
@Tag(name = "MenuCategory", description = "메뉴 카테고리 API")
public class MenuCategoryController {

    private final MenuCategoryService menuCategoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<String>>> getMenuCategory(
        @RequestParam("major-category") String majorCategory) {

        List<String> menuCategory = menuCategoryService.getMenuCategory(majorCategory);

        return GlobalResponse.OK("소분류 목록 조회 성공", menuCategory);
    }

    @GetMapping("/add")
    public ResponseEntity<ApiResponse<String>> addMenuCategories() {

        menuCategoryService.addMenuCategories();

        return GlobalResponse.OK("메뉴 카테고리 추가 성공, 작업이 백그라운드에서 실행됩니다.", null);
    }

    @GetMapping("/search-school")
    public ResponseEntity<ApiResponse<List<String>>> searchSchoolCategories(
        @RequestParam("keyword") String keyword) {

        List<String> suggestions = menuCategoryService.getSchoolNameSuggestions(keyword);

        return GlobalResponse.OK("학교 검색 성공", suggestions);
    }
}

package devping.nnplanner.domain.menucategory.controller;

import devping.nnplanner.domain.menucategory.service.MenuCategoryService;
import devping.nnplanner.global.response.ApiResponse;
import devping.nnplanner.global.response.GlobalResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
}

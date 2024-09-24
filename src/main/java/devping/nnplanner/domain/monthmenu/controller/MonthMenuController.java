package devping.nnplanner.domain.monthmenu.controller;

import devping.nnplanner.domain.monthmenu.dto.request.MonthMenuAutoRequestDTO;
import devping.nnplanner.domain.monthmenu.dto.response.MonthMenuAutoResponseDTO;
import devping.nnplanner.domain.monthmenu.service.MonthMenuService;
import devping.nnplanner.global.response.ApiResponse;
import devping.nnplanner.global.response.GlobalResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/monthmenus")
@RestController
@RequiredArgsConstructor
public class MonthMenuController {

    private final MonthMenuService monthMenuService;

    @PostMapping("/auto")
    public ResponseEntity<ApiResponse<List<MonthMenuAutoResponseDTO>>> createMonthMenuAuto(
        @RequestBody @Valid MonthMenuAutoRequestDTO monthMenuAutoRequestDTO) {

        List<MonthMenuAutoResponseDTO> monthMenuAutoResponseDTO =
            monthMenuService.createMonthMenuAuto(monthMenuAutoRequestDTO);

        return GlobalResponse.CREATED("자동 식단 생성 성공", monthMenuAutoResponseDTO);
    }

}

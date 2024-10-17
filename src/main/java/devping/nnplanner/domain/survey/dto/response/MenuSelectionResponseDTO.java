package devping.nnplanner.domain.survey.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
public class MenuSelectionResponseDTO {

    private LocalDateTime responseDate; // 응답 날짜
    private List<String> menus;  // 메뉴 리스트

    public MenuSelectionResponseDTO() {
    }

    public MenuSelectionResponseDTO(LocalDateTime responseDate, String menus) {
        this.responseDate = responseDate;
        this.menus = Collections.singletonList(menus);
    }
}


package devping.nnplanner.domain.survey.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class MenuSelectionResponseDTO {

    private LocalDateTime responseDate;
    private String menu;

    public MenuSelectionResponseDTO(LocalDateTime responseDate, String menu) {
        this.responseDate = responseDate;
        this.menu = menu;
    }
}

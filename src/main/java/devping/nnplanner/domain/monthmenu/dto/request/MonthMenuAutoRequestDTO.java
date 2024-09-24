package devping.nnplanner.domain.monthmenu.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class MonthMenuAutoRequestDTO {

    @NotBlank
    private String majorCategory;

    @NotBlank
    private String minorCategory;

    @NotNull
    private Integer dayCount;
}

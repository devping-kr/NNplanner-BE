package devping.nnplanner.domain.monthmenu.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;

@Getter
public class MonthMenuSaveRequestDTO {

    @NotBlank
    private String monthMenuName;

    @NotBlank
    private String majorCategory;

    @NotBlank
    private String minorCategory;

    private List<MonthMenusSave> monthMenusSaveList;

    @Getter
    public static class MonthMenusSave {

        private String hospitalMenuId;

        @NotNull
        private LocalDate menuDate;

        @NotBlank
        private String food1;

        @NotBlank
        private String food2;

        @NotBlank
        private String food3;

        @NotBlank
        private String food4;

        @NotBlank
        private String food5;

        @NotBlank
        private String food6;

        @NotBlank
        private String food7;
    }
}

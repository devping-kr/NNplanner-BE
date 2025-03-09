package devping.nnplanner.domain.monthmenu.dto.response;

import lombok.Getter;

@Getter
public class MonthCountResponseDTO {

    private Integer totalMenuCount;

    private Integer currentMenuCount;

    private Integer lastMenuCount;

    public MonthCountResponseDTO(Integer totalMenuCount,
                                 Integer currentMenuCount,
                                 Integer lastMenuCount) {

        this.totalMenuCount = totalMenuCount;
        this.currentMenuCount = currentMenuCount;
        this.lastMenuCount = lastMenuCount;
    }
}
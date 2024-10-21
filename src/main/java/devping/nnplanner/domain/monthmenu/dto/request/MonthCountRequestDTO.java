package devping.nnplanner.domain.monthmenu.dto.request;

import lombok.Getter;

@Getter
public class MonthCountRequestDTO {

    private Integer totalMenuCount;

    private Integer currentMenuCount;

    private Integer lastMenuCount;

    public MonthCountRequestDTO(Integer totalMenuCount,
                                Integer currentMenuCount,
                                Integer lastMenuCount) {

        this.totalMenuCount = totalMenuCount;
        this.currentMenuCount = currentMenuCount;
        this.lastMenuCount = lastMenuCount;
    }
}
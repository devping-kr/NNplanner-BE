package devping.nnplanner.domain.monthmenu.dto.response;

import devping.nnplanner.domain.monthmenu.entity.MonthMenu;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Getter;

@Getter
public class MonthMenuResponseDTO {

    private final Long userId;

    private final UUID monthMenuId;

    private final String majorCategory;

    private final String minorCategory;

    private final String monthMenuName;

    private final LocalDateTime createAt;

    private final List<MonthFoodListResponseDTO> monthMenuList;

    public MonthMenuResponseDTO(MonthMenu monthMenu, List<MonthFoodListResponseDTO> monthMenuList) {
        this.userId = monthMenu.getUser().getUserId();
        this.monthMenuId = monthMenu.getMonthMenuId();
        this.majorCategory = monthMenu.getMenuCategory().getMajorCategory();
        this.minorCategory = monthMenu.getMenuCategory().getMinorCategory();
        this.monthMenuName = monthMenu.getMonthMenuName();
        this.createAt = monthMenu.getCreatedAt();
        this.monthMenuList = monthMenuList;
    }
}

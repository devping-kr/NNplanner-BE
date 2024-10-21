package devping.nnplanner.domain.monthmenu.dto.response;

import devping.nnplanner.domain.monthmenu.entity.MonthMenuHospital;
import devping.nnplanner.domain.monthmenu.entity.MonthMenuSchool;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.Getter;

@Getter
public class MonthFoodListResponseDTO {

    private LocalDate menuDate;

    private UUID menuId;

    private List<FoodResponseDTO> foodList;

    public void monthMenuHospital(MonthMenuHospital monthMenuHospital,
                                  List<FoodResponseDTO> foodList) {
        this.menuDate = monthMenuHospital.getMenuDate();
        this.menuId = monthMenuHospital.getHospitalMenu().getHospitalMenuId();
        this.foodList = foodList;
    }

    public void monthMenuSchool(MonthMenuSchool monthMenuSchool,
                                List<FoodResponseDTO> foodList) {
        this.menuDate = monthMenuSchool.getMenuDate();
        this.menuId = monthMenuSchool.getSchoolMenu().getSchoolMenuId();
        this.foodList = foodList;
    }
}

package devping.nnplanner.domain.monthmenu.dto.response;

import devping.nnplanner.domain.monthmenu.entity.MonthMenuHospital;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.Getter;

@Getter
public class MonthFoodListResponseDTO {

    private final LocalDate menuDate;

    private final UUID hospitalMenuId;

    private final List<FoodResponseDTO> foodList;

    public MonthFoodListResponseDTO(MonthMenuHospital monthMenuHospital,
                                    List<FoodResponseDTO> foodList) {
        this.menuDate = monthMenuHospital.getMenuDate();
        this.hospitalMenuId = monthMenuHospital.getHospitalMenu().getHospitalMenuId();
        this.foodList = foodList;
    }
}

package devping.nnplanner.domain.monthmenu.dto.response;

import devping.nnplanner.domain.openapi.entity.HospitalMenu;
import devping.nnplanner.domain.openapi.entity.SchoolMenu;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import lombok.Getter;

@Getter
public class MonthMenuAutoResponseDTO {

    private UUID menuId;

    private String menuKind;

    private List<FoodResponseDTO> foods;

    public void hospitalMenu(HospitalMenu hospitalMenu) {

        this.menuId = hospitalMenu.getHospitalMenuId();
        this.menuKind = hospitalMenu.getHospitalMenuKind();
        this.foods = new ArrayList<>(Arrays.asList(
            new FoodResponseDTO(hospitalMenu.getFood1()),
            new FoodResponseDTO(hospitalMenu.getFood2()),
            new FoodResponseDTO(hospitalMenu.getFood3()),
            new FoodResponseDTO(hospitalMenu.getFood4()),
            new FoodResponseDTO(hospitalMenu.getFood5()),
            new FoodResponseDTO(hospitalMenu.getFood6()),
            new FoodResponseDTO(hospitalMenu.getFood7())
        ));
    }

    public void schoolKindName(SchoolMenu schoolMenu) {

        this.menuId = schoolMenu.getSchoolMenuId();
        this.menuKind = schoolMenu.getSchoolInfo().getSchoolKindName();
        this.foods = new ArrayList<>(Arrays.asList(
            new FoodResponseDTO(schoolMenu.getFood1()),
            new FoodResponseDTO(schoolMenu.getFood2()),
            new FoodResponseDTO(schoolMenu.getFood3()),
            new FoodResponseDTO(schoolMenu.getFood4()),
            new FoodResponseDTO(schoolMenu.getFood5()),
            new FoodResponseDTO(schoolMenu.getFood6()),
            new FoodResponseDTO(schoolMenu.getFood7())
        ));
    }

    public void schoolName(SchoolMenu schoolMenu) {

        this.menuId = schoolMenu.getSchoolMenuId();
        this.menuKind = schoolMenu.getSchoolInfo().getSchoolName();
        this.foods = new ArrayList<>(Arrays.asList(
            new FoodResponseDTO(schoolMenu.getFood1()),
            new FoodResponseDTO(schoolMenu.getFood2()),
            new FoodResponseDTO(schoolMenu.getFood3()),
            new FoodResponseDTO(schoolMenu.getFood4()),
            new FoodResponseDTO(schoolMenu.getFood5()),
            new FoodResponseDTO(schoolMenu.getFood6()),
            new FoodResponseDTO(schoolMenu.getFood7())
        ));
    }
}

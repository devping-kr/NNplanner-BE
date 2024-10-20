package devping.nnplanner.domain.monthmenu.dto.response;

import devping.nnplanner.domain.openapi.entity.SchoolMenu;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import lombok.Getter;

@Getter
public class SchoolMonthMenuAutoResponseDTO {

    private final UUID schoolMenuId;

    private final Long schoolInfoId;

    private final List<FoodResponseDTO> foods;

    public SchoolMonthMenuAutoResponseDTO(SchoolMenu schoolMenu) {

        this.schoolMenuId = schoolMenu.getSchoolMenuId();
        this.schoolInfoId = schoolMenu.getSchoolInfo().getSchoolInfoId();
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

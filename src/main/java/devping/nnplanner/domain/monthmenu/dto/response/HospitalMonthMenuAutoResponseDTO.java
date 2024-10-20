package devping.nnplanner.domain.monthmenu.dto.response;

import devping.nnplanner.domain.openapi.entity.HospitalMenu;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import lombok.Getter;

@Getter
public class HospitalMonthMenuAutoResponseDTO {

    private final UUID hospitalMenuId;

    private final String hospitalMenuKind;

    private final List<FoodResponseDTO> foods;

    public HospitalMonthMenuAutoResponseDTO(HospitalMenu hospitalMenu) {

        this.hospitalMenuId = hospitalMenu.getHospitalMenuId();
        this.hospitalMenuKind = hospitalMenu.getHospitalMenuKind();
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
}

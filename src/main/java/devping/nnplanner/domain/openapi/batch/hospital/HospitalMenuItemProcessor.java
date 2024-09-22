package devping.nnplanner.domain.openapi.batch.hospital;

import devping.nnplanner.domain.openapi.entity.Food;
import devping.nnplanner.domain.openapi.entity.HospitalMenu;
import devping.nnplanner.domain.openapi.entity.ImportHospitalMenu;
import devping.nnplanner.domain.openapi.repository.FoodRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HospitalMenuItemProcessor implements
    ItemProcessor<ImportHospitalMenu, HospitalMenu> {

    private final FoodRepository foodRepository;

    @Override
    public HospitalMenu process(ImportHospitalMenu importMenu) {

        Food food1 = findBestMatchingFood(importMenu.getHospitalFood1());
        Food food2 = findBestMatchingFood(importMenu.getHospitalFood2());
        Food food3 = findBestMatchingFood(importMenu.getHospitalFood3());
        Food food4 = findBestMatchingFood(importMenu.getHospitalFood4());
        Food food5 = findBestMatchingFood(importMenu.getHospitalFood5());
        Food food6 = findBestMatchingFood(importMenu.getHospitalFood6());
        Food food7 = findBestMatchingFood(importMenu.getHospitalFood7());

        if (food1 == null || food2 == null || food3 == null || food4 == null ||
            food5 == null || food6 == null || food7 == null) {
            return null;
        }

        return new HospitalMenu().create(
            importMenu.getMenuKind(), food1, food2, food3, food4, food5, food6, food7);
    }

    private Food findBestMatchingFood(String hospitalFoodName) {

        List<Food> matchingFoods = foodRepository.findBySimilarName(hospitalFoodName);

        if (matchingFoods.isEmpty()) {
            return null;
        }

        return matchingFoods.get(0);
    }
}
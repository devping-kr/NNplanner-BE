package devping.nnplanner.domain.openapi.batch.schoolMenu;

import com.fasterxml.jackson.databind.ObjectMapper;
import devping.nnplanner.domain.openapi.dto.response.SchoolMenuDataVO;
import devping.nnplanner.domain.openapi.entity.Food;
import devping.nnplanner.domain.openapi.entity.SchoolInfo;
import devping.nnplanner.domain.openapi.entity.SchoolMenu;
import devping.nnplanner.domain.openapi.repository.FoodRepository;
import devping.nnplanner.domain.openapi.repository.SchoolInfoRepository;
import devping.nnplanner.global.exception.CustomException;
import devping.nnplanner.global.exception.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class SchoolMenuProcessor implements
    ItemProcessor<SchoolMenuDataVO, List<SchoolMenu>> {

    private final SchoolInfoRepository schoolInfoRepository;
    private final FoodRepository foodRepository;

    @Override
    @Transactional
    public List<SchoolMenu> process(SchoolMenuDataVO menuData) throws Exception {

        List<SchoolMenu> schoolMenus = new ArrayList<>();

        ObjectMapper objectMapper = new ObjectMapper();

        log.info("Process 단계에서 복사된 값: {}", objectMapper.writeValueAsString(menuData));

        SchoolInfo schoolInfo =
            schoolInfoRepository.findBySchoolCode(menuData.getSchoolCode())
                                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        for (String[] foodNames : menuData.getFoodNamesList()) {

            List<Food> foods = new ArrayList<>();
            boolean isValidMenu = true;

            // Food 매칭
            for (String foodName : foodNames) {

                String parsedFoodName = extractKoreanName(foodName);
                Food matchingFood = findBestMatchingFood(parsedFoodName);

                if (matchingFood == null || foods.size() > 7) {
                    isValidMenu = false;
                    break;
                }

                if (!foods.contains(matchingFood)) {
                    foods.add(matchingFood);
                }
            }

            if (!isValidMenu) {
                continue;
            }

            while (foods.size() < 7) {
                foods.add(findBestMatchingFood("없음"));
            }

            SchoolMenu schoolMenu = new SchoolMenu();
            schoolMenu.create(
                schoolInfo,
                foods.get(0),
                foods.get(1),
                foods.get(2),
                foods.get(3),
                foods.get(4),
                foods.get(5),
                foods.get(6)
            );

            schoolMenus.add(schoolMenu);
        }

        return schoolMenus;
    }

    private String extractKoreanName(String foodName) {

        return foodName.replaceAll("[^가-힣 ]", "").trim();
    }

    private Food findBestMatchingFood(String hospitalFoodName) {

        List<Food> matchingFoods = foodRepository.findBySimilarName(hospitalFoodName);

        if (matchingFoods.isEmpty()) {
            return null;
        }

        return matchingFoods.get(0);
    }
}

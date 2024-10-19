package devping.nnplanner.domain.openapi.batch.schoolMenu;

import devping.nnplanner.domain.openapi.entity.SchoolMenu;
import devping.nnplanner.domain.openapi.repository.SchoolMenuRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SchoolMenuWriter implements ItemWriter<List<SchoolMenu>> {

    private final SchoolMenuRepository schoolMenuRepository;

    @Override
    @Transactional
    public void write(Chunk<? extends List<SchoolMenu>> items) throws Exception {

        List<SchoolMenu> menusToSave = new ArrayList<>();

        items.getItems().forEach(schoolMenuList ->
            schoolMenuList.forEach(schoolMenu -> {

                boolean exists = schoolMenuRepository
                    .existsBySchoolInfo_SchoolInfoIdAndFood1AndFood2AndFood3AndFood4AndFood5AndFood6AndFood7(
                        schoolMenu.getSchoolInfo().getSchoolInfoId(),
                        schoolMenu.getFood1(),
                        schoolMenu.getFood2(),
                        schoolMenu.getFood3(),
                        schoolMenu.getFood4(),
                        schoolMenu.getFood5(),
                        schoolMenu.getFood6(),
                        schoolMenu.getFood7()
                    );

                if (!exists) {
                    menusToSave.add(schoolMenu);
                }
            }));

        if (!menusToSave.isEmpty()) {
            schoolMenuRepository.saveAll(menusToSave);
        }
    }

}

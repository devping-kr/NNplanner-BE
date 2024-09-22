package devping.nnplanner.domain.openapi.batch.hospital;

import devping.nnplanner.domain.openapi.entity.HospitalMenu;
import devping.nnplanner.domain.openapi.repository.HospitalMenuRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class HospitalMenuItemWriter implements ItemWriter<HospitalMenu> {

    private final HospitalMenuRepository hospitalMenuRepository;

    @Override
    @Transactional
    public void write(Chunk<? extends HospitalMenu> items) {

        List<HospitalMenu> menusToSave = new ArrayList<>();

        items.forEach(hospitalMenu -> {
            boolean exists = hospitalMenuRepository
                .existsByHospitalMenuKindAndFood1AndFood2AndFood3AndFood4AndFood5AndFood6AndFood7(
                    hospitalMenu.getHospitalMenuKind(),
                    hospitalMenu.getFood1(),
                    hospitalMenu.getFood2(),
                    hospitalMenu.getFood3(),
                    hospitalMenu.getFood4(),
                    hospitalMenu.getFood5(),
                    hospitalMenu.getFood6(),
                    hospitalMenu.getFood7()
                );

            if (!exists) {
                menusToSave.add(hospitalMenu);
            }
        });

        if (!menusToSave.isEmpty()) {
            hospitalMenuRepository.saveAll(menusToSave);
        }
    }
}
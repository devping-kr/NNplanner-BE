package devping.nnplanner.domain.openapi.batch.hospital;

import devping.nnplanner.domain.openapi.entity.ImportHospitalMenu;
import devping.nnplanner.domain.openapi.repository.ImportHospitalMenuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class HospitalMenuItemReader implements ItemReader<ImportHospitalMenu> {

    private final ImportHospitalMenuRepository importHospitalMenuRepository;
    private Long currentId = 1L;

    @Override
    @Transactional(readOnly = true)
    public ImportHospitalMenu read() {

        ImportHospitalMenu currentBatch = importHospitalMenuRepository.findById(currentId)
                                                                      .orElse(null);
        currentId++;

        log.info("현재 ID: {}, 데이터를 읽었습니다.", currentId);

        return currentBatch;
    }
}
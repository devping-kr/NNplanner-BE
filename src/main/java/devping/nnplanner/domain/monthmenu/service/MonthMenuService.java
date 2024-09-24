package devping.nnplanner.domain.monthmenu.service;

import devping.nnplanner.domain.monthmenu.dto.request.MonthMenuAutoRequestDTO;
import devping.nnplanner.domain.monthmenu.dto.response.MonthMenuAutoResponseDTO;
import devping.nnplanner.domain.monthmenu.repository.MonthMenuRepository;
import devping.nnplanner.domain.openapi.entity.HospitalMenu;
import devping.nnplanner.domain.openapi.repository.HospitalMenuRepository;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MonthMenuService {

    private final MonthMenuRepository monthMenuRepository;
    private final HospitalMenuRepository hospitalMenuRepository;

    public List<MonthMenuAutoResponseDTO> createMonthMenuAuto(
        MonthMenuAutoRequestDTO requestDTO) {

        if (requestDTO.getMajorCategory().equals("병원")) {

            List<HospitalMenu> randomHospitalMenus =
                hospitalMenuRepository.findRandomHospitalMenusByCategory(
                    requestDTO.getMinorCategory(), requestDTO.getDayCount());

            return randomHospitalMenus.stream()
                                      .map(MonthMenuAutoResponseDTO::new)
                                      .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}

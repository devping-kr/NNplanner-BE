package devping.nnplanner.domain.monthmenu.dto.response;

import java.util.List;
import lombok.Getter;

@Getter
public class MonthMenuPageResponseDTO {

    private final int currentPage;     // 현재 페이지 번호

    private final int totalPages;      // 전체 페이지 수

    private final long totalElements;     // 전체 항목 수

    private final int pageSize;        // 한 페이지당 항목 수

    private final List<MonthMenuResponseDTO> menuResponseDTOList;

    public MonthMenuPageResponseDTO(int currentPage,
                                    int totalPages,
                                    long totalElements,
                                    int pageSize,
                                    List<MonthMenuResponseDTO> menuResponseDTOList) {
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.pageSize = pageSize;
        this.menuResponseDTOList = menuResponseDTOList;
    }
}

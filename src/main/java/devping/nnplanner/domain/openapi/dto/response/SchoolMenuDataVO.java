package devping.nnplanner.domain.openapi.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SchoolMenuDataVO {

    private String schoolCode;
    private List<String[]> foodNamesList;
}

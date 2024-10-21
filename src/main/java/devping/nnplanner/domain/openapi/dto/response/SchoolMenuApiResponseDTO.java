package devping.nnplanner.domain.openapi.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;

@Getter
public class SchoolMenuApiResponseDTO {

    private List<SchoolMenuInfo> mealServiceDietInfo;

    @Getter
    public static class SchoolMenuInfo {

        @JsonProperty("row")
        private List<SchoolMenuRow> row;
    }

    @Getter
    public static class SchoolMenuRow {

        @JsonProperty("DDISH_NM")
        private String schoolMenuName;

        @JsonProperty("SD_SCHUL_CODE")
        private String schoolCode;
    }
}

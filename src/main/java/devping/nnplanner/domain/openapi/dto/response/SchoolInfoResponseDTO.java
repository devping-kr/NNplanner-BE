package devping.nnplanner.domain.openapi.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;

@Getter
public class SchoolInfoResponseDTO {

    private List<SchoolInfo> schoolInfo;

    @Getter
    public static class SchoolInfo {

        private List<Head> head;

        private List<SchoolRow> row;
    }

    @Getter
    public static class Head {

        @JsonProperty("list_total_count")
        private Integer listTotalCount;
    }


    @Getter
    public static class SchoolRow {

        @JsonProperty("ATPT_OFCDC_SC_CODE")
        private String schoolAreaCode;

        @JsonProperty("ATPT_OFCDC_SC_NM")
        private String schoolAreaName;

        @JsonProperty("SD_SCHUL_CODE")
        private String schoolCode;

        @JsonProperty("SCHUL_NM")
        private String schoolName;

        @JsonProperty("SCHUL_KND_SC_NM")
        private String schoolKindName;
    }
}
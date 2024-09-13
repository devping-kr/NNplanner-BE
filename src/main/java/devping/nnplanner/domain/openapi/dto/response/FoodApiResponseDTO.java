package devping.nnplanner.domain.openapi.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;

@Getter
public class FoodApiResponseDTO {

    private Header header;
    private Body body;

    @Getter
    public static class Header {

        private String resultCode;
        private String resultMsg;
    }

    @Getter
    public static class Body {

        private String pageNo;
        private String totalCount;
        private String numOfRows;
        private List<FoodItem> items;
    }

    @Getter
    public static class FoodItem {

        @JsonProperty("FOOD_NM_KR")
        private String foodName;

        @JsonProperty("AMT_NUM4")
        private String fat;

        @JsonProperty("AMT_NUM1")
        private String kcal;

        @JsonProperty("AMT_NUM3")
        private String protein;

        @JsonProperty("AMT_NUM7")
        private String carbohydrate;
    }
}
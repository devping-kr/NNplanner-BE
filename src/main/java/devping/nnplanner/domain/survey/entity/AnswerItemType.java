package devping.nnplanner.domain.survey.entity;

public enum AnswerItemType {
    DATE("날짜"), RADIO("점수"), TEXT("의견"), ETC("기타");

    private final String name;

    AnswerItemType(String name) {
        this.name = name;
    }
}

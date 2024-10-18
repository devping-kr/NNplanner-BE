package devping.nnplanner.domain.survey.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class SurveyResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "survey_id")
    private Survey survey;

    @ManyToOne
    @JoinColumn(name = "question_id")
    private Question question;

    private LocalDateTime responseDate;

    private Integer satisfactionScore; // 만족도 점수 (1~10)

    @ElementCollection
    @CollectionTable(name = "liked_menus", joinColumns = @JoinColumn(name = "survey_response_id"))
    @Column(name = "menu")
    private List<String> likedMenus;

    @ElementCollection
    @CollectionTable(name = "disliked_menus", joinColumns = @JoinColumn(name = "survey_response_id"))
    @Column(name = "menu")
    private List<String> dislikedMenus;

    @ElementCollection
    @CollectionTable(name = "survey_response_desired_menus", joinColumns = @JoinColumn(name = "survey_response_id"))
    @Column(name = "desired_menus")
    private List<String> desiredMenus; // 먹고 싶은 메뉴 리스트

    @Column(length = 1000)
    private String messagesToDietitian; // 영양사에게 남기는 메시지

    private int monthlySatisfaction = 0;
    private int portionSatisfaction= 0;
    private int hygieneSatisfaction = 0;
    private int tasteSatisfaction = 0;


    // 기본 생성자
    public SurveyResponse() {
    }

    public SurveyResponse(Survey survey, Question question, List<String> likedMenus, List<String> dislikedMenus,
                          List<String> desiredMenus, String messagesToDietitian, Integer satisfactionScore,
                          LocalDateTime responseDate) {
        this.survey = survey;
        this.question = question;
        this.likedMenus = likedMenus; // 리스트 타입이므로 리스트를 직접 할당
        this.dislikedMenus = dislikedMenus; // 리스트 타입이므로 리스트를 직접 할당
        this.desiredMenus = desiredMenus;
        this.messagesToDietitian = messagesToDietitian;
        this.satisfactionScore = satisfactionScore;
        this.responseDate = responseDate;
    }

}

package devping.nnplanner.domain.openapi.entity;

import devping.nnplanner.global.entity.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Table(name = "school_menus")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SchoolMenu extends BaseTimeEntity {

    @Id
    @UuidGenerator
    private UUID schoolMenuId;

    @ManyToOne
    @JoinColumn(name = "school_info_id")
    private SchoolInfo schoolInfo;

    @ManyToOne
    @JoinColumn(name = "food_id_1")
    private Food food1;

    @ManyToOne
    @JoinColumn(name = "food_id_2")
    private Food food2;

    @ManyToOne
    @JoinColumn(name = "food_id_3")
    private Food food3;

    @ManyToOne
    @JoinColumn(name = "food_id_4")
    private Food food4;

    @ManyToOne
    @JoinColumn(name = "food_id_5")
    private Food food5;

    @ManyToOne
    @JoinColumn(name = "food_id_6")
    private Food food6;

    @ManyToOne
    @JoinColumn(name = "food_id_7")
    private Food food7;

    public void create(SchoolInfo schoolInfo,
                       Food food1,
                       Food food2,
                       Food food3,
                       Food food4,
                       Food food5,
                       Food food6,
                       Food food7) {

        this.schoolInfo = schoolInfo;
        this.food1 = food1;
        this.food2 = food2;
        this.food3 = food3;
        this.food4 = food4;
        this.food5 = food5;
        this.food6 = food6;
        this.food7 = food7;
    }
}

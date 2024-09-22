package devping.nnplanner.domain.openapi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Table(name = "hospital_menus")
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class HospitalMenu {

    @Id
    @UuidGenerator
    private UUID hospitalDMId;

    private String hospitalMenuKind;

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

    public HospitalMenu create(String hospitalMenuKind,
                               Food food1,
                               Food food2,
                               Food food3,
                               Food food4,
                               Food food5,
                               Food food6,
                               Food food7) {

        return HospitalMenu
            .builder()
            .hospitalMenuKind(hospitalMenuKind)
            .food1(food1)
            .food2(food2)
            .food3(food3)
            .food4(food4)
            .food5(food5)
            .food6(food6)
            .food7(food7)
            .build();
    }
}

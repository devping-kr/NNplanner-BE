package devping.nnplanner.domain.openapi.entity;

import devping.nnplanner.global.entity.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Table(name = "foods")
@Getter
@Entity
public class Food extends BaseTimeEntity {

    @Id
    @UuidGenerator
    private UUID foodId;

    @Setter
    private String foodName;

    @Setter
    private String carbohydrate;

    @Setter
    private String protein;

    @Setter
    private String fat;

    @Setter
    private String kcal;
}

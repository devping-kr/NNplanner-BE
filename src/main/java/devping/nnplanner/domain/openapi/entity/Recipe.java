package devping.nnplanner.domain.openapi.entity;

import devping.nnplanner.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import org.hibernate.annotations.UuidGenerator;

@Table(name = "recipes")
@Getter
@Entity
public class Recipe extends BaseTimeEntity {

    @Id
    @UuidGenerator
    private UUID recipeId;

    private Integer month;

    private String recipeName;

    @Column(columnDefinition = "TEXT")
    private String mainIngredient;

    @Column(columnDefinition = "TEXT")
    private String subIngredient;

    @Column(columnDefinition = "TEXT")
    private String instructions;

    @Column(columnDefinition = "TEXT")
    private String forGroup;

    @Column(length = 500)
    private String imageUrl;
}
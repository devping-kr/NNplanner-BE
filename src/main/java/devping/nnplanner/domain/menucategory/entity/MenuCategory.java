package devping.nnplanner.domain.menucategory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.UUID;
import lombok.Getter;
import org.hibernate.annotations.UuidGenerator;

@Getter
@Entity
public class MenuCategory {

    @Id
    @UuidGenerator
    private UUID menuCategoryId;

    @Column(nullable = false)
    private String majorCategory;

    @Column(nullable = false)
    private String minorCategory;
}

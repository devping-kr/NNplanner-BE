package devping.nnplanner.domain.monthmenu.entity;

import devping.nnplanner.domain.auth.entity.User;
import devping.nnplanner.domain.menucategory.entity.MenuCategory;
import devping.nnplanner.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Table(name = "month_menus")
@Getter
@NoArgsConstructor
@Entity
public class MonthMenu extends BaseTimeEntity {

    @Id
    @UuidGenerator
    private UUID monthMenuId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "menu_category_id", nullable = false)
    private MenuCategory menuCategory;

    @Column(nullable = false)
    private String monthMenuName;

    @Column(nullable = false)
    private String month;

    public void create(User user,
                       MenuCategory menuCategory,
                       String monthMenuName,
                       String month) {
        this.user = user;
        this.menuCategory = menuCategory;
        this.monthMenuName = monthMenuName;
        this.month = month;
    }
}

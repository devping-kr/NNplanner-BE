package devping.nnplanner.domain.monthmenu.entity;

import devping.nnplanner.domain.openapi.entity.SchoolMenu;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Getter;
import org.hibernate.annotations.UuidGenerator;

@Table(name = "month_menu_schools")
@Getter
@Entity
public class MonthMenuSchool {

    @Id
    @UuidGenerator
    private UUID monthMenuSchoolId;

    @Column(nullable = false)
    private LocalDate menuDate;

    @ManyToOne
    @JoinColumn(name = "month_menu_id", nullable = false)
    private MonthMenu monthMenu;

    @ManyToOne
    @JoinColumn(name = "school_menu_id", nullable = false)
    private SchoolMenu schoolMenu;

    public void create(LocalDate menuDate,
                       MonthMenu monthMenu,
                       SchoolMenu schoolMenu) {
        this.menuDate = menuDate;
        this.monthMenu = monthMenu;
        this.schoolMenu = schoolMenu;
    }
}
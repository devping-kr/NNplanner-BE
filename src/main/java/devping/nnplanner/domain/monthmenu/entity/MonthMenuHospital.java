package devping.nnplanner.domain.monthmenu.entity;

import devping.nnplanner.domain.openapi.entity.HospitalMenu;
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

@Table(name = "month_menu_hospitals")
@Getter
@Entity
public class MonthMenuHospital {

    @Id
    @UuidGenerator
    private UUID monthMenuHospitalId;

    @Column(nullable = false)
    private LocalDate menuDate;

    @ManyToOne
    @JoinColumn(name = "month_menu_id", nullable = false)
    private MonthMenu monthMenu;

    @ManyToOne
    @JoinColumn(name = "hospital_menu_id", nullable = false)
    private HospitalMenu hospitalMenu;

    public void create(LocalDate menuDate,
                       MonthMenu monthMenu,
                       HospitalMenu hospitalMenu) {
        this.menuDate = menuDate;
        this.monthMenu = monthMenu;
        this.hospitalMenu = hospitalMenu;
    }
}

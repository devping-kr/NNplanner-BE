package devping.nnplanner.domain.monthmenu.entity;

import devping.nnplanner.domain.openapi.entity.HospitalMenu;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

@Table(name = "month_menu_hospitals")
@Getter
@Entity
public class MonthMenuHospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long monthMenuHospitalId;

    @ManyToOne
    @JoinColumn(name = "month_menu_id", nullable = false)
    private MonthMenu monthMenu;

    @ManyToOne
    @JoinColumn(name = "hospital_menu_id", nullable = false)
    private HospitalMenu hospitalMenu;
}

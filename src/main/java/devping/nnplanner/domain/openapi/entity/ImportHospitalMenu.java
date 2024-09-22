package devping.nnplanner.domain.openapi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

/*
@Table(name = "import_hospital_menus")
*/
@Getter
@Entity
public class ImportHospitalMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hospitalMenuId;

    private String menuKind;

    private String hospitalFood1;

    private String hospitalFood2;

    private String hospitalFood3;

    private String hospitalFood4;

    private String hospitalFood5;

    private String hospitalFood6;

    private String hospitalFood7;
}

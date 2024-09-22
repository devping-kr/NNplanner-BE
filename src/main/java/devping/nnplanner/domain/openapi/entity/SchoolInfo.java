package devping.nnplanner.domain.openapi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

@Table(name = "school_infos")
@Getter
@Entity
public class SchoolInfo {

    @Id
    @UuidGenerator
    private UUID schoolInfoId;

    @Setter
    private String schoolAreaCode;

    @Setter
    private String schoolAreaName;

    @Setter
    private String schoolCode;

    @Setter
    private String schoolName;

    @Setter
    private String schoolKindName;
}

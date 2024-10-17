package devping.nnplanner.domain.openapi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Table(name = "school_infos")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SchoolInfo {

    @Id
    @UuidGenerator
    private UUID schoolInfoId;

    private String schoolAreaCode;

    private String schoolAreaName;

    private String schoolCode;

    private String schoolName;

    private String schoolKindName;

    public void create(String schoolAreaCode,
                       String schoolAreaName,
                       String schoolCode,
                       String schoolName,
                       String schoolKindName) {

        this.schoolAreaCode = schoolAreaCode;
        this.schoolAreaName = schoolAreaName;
        this.schoolCode = schoolCode;
        this.schoolName = schoolName;
        this.schoolKindName = schoolKindName;
    }
}

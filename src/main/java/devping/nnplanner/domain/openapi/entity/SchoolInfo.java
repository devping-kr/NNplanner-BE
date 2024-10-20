package devping.nnplanner.domain.openapi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "school_infos")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SchoolInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long schoolInfoId;

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

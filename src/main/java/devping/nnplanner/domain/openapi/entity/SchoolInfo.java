package devping.nnplanner.domain.openapi.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

@Table(name = "school_infos")
@Builder
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

    public SchoolInfo create(String schoolAreaCode, String schoolAreaName, String schoolCode,
                             String schoolName, String schoolKindName) {

        return SchoolInfo.builder()
                         .schoolAreaCode(schoolAreaCode)
                         .schoolAreaName(schoolAreaName)
                         .schoolCode(schoolCode)
                         .schoolName(schoolName)
                         .schoolKindName(schoolKindName)
                         .build();
    }
}

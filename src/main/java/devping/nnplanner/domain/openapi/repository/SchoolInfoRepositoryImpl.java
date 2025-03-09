package devping.nnplanner.domain.openapi.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import devping.nnplanner.domain.openapi.entity.QSchoolInfo;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SchoolInfoRepositoryImpl implements SchoolInfoRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<String> findDistinctSchoolNames() {
        QSchoolInfo schoolInfo = QSchoolInfo.schoolInfo;

        return queryFactory
            .select(schoolInfo.schoolName)
            .distinct()
            .from(schoolInfo)
            .where(schoolInfo.schoolName.isNotNull())
            .orderBy(schoolInfo.schoolName.asc()) // 가나다 순 정렬 추가
            .fetch();
    }

    @Override
    public List<String> findDistinctSchoolKindNames() {
        QSchoolInfo schoolInfo = QSchoolInfo.schoolInfo;

        return queryFactory
            .select(schoolInfo.schoolKindName)
            .distinct()
            .from(schoolInfo)
            .where(schoolInfo.schoolKindName.isNotNull())
            .orderBy(schoolInfo.schoolKindName.asc()) // 가나다 순 정렬 추가
            .fetch();
    }

    @Override
    public List<String> searchSchoolNames(String keyword, int limit) {
        QSchoolInfo schoolInfo = QSchoolInfo.schoolInfo;

        return queryFactory
            .select(schoolInfo.schoolName)
            .distinct()
            .from(schoolInfo)
            .where(schoolInfo.schoolName.startsWithIgnoreCase(keyword))
            .orderBy(schoolInfo.schoolName.asc()) // 가나다순 정렬
            .limit(limit) // 최대 개수 제한
            .fetch();
    }
}
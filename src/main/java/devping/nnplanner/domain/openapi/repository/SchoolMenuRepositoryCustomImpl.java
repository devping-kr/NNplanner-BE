package devping.nnplanner.domain.openapi.repository;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import devping.nnplanner.domain.openapi.entity.QSchoolMenu;
import devping.nnplanner.domain.openapi.entity.SchoolMenu;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SchoolMenuRepositoryCustomImpl implements SchoolMenuRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<SchoolMenu> findRandomSchoolMenusBySchoolName(String schoolName, int dayCount) {

        QSchoolMenu schoolMenu = QSchoolMenu.schoolMenu;

        return queryFactory
            .selectFrom(schoolMenu)
            .where(
                schoolMenu.schoolInfo.schoolName.eq(schoolName)
                                                .and(schoolMenu.createdBy.isNull())
            )
            .orderBy(Expressions.numberTemplate(Double.class, "function('random')").asc())
            .limit(dayCount)
            .fetch();
    }

    @Override
    public List<SchoolMenu> findRandomSchoolMenusBySchoolKindName(String schoolKindName,
                                                                  int dayCount) {

        QSchoolMenu schoolMenu = QSchoolMenu.schoolMenu;

        return queryFactory
            .selectFrom(schoolMenu)
            .where(
                schoolMenu.schoolInfo.schoolKindName.eq(schoolKindName)
                                                    .and(schoolMenu.createdBy.isNull())
            )
            .orderBy(Expressions.numberTemplate(Double.class, "function('random')").asc())
            .limit(dayCount)
            .fetch();
    }
}

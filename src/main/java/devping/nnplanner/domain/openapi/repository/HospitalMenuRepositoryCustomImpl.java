package devping.nnplanner.domain.openapi.repository;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import devping.nnplanner.domain.openapi.entity.HospitalMenu;
import devping.nnplanner.domain.openapi.entity.QHospitalMenu;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class HospitalMenuRepositoryCustomImpl implements HospitalMenuRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<HospitalMenu> findRandomHospitalMenusByCategory(String hospitalMenuKind,
                                                                int dayCount) {

        QHospitalMenu hospitalMenu = QHospitalMenu.hospitalMenu;

        return queryFactory
            .selectFrom(hospitalMenu)
            .where(hospitalMenu.hospitalMenuKind.eq(hospitalMenuKind))
            .orderBy(Expressions.numberTemplate(Double.class, "function('random')").asc())
            .limit(dayCount)
            .fetch();
    }
}
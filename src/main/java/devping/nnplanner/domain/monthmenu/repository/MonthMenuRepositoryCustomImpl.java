package devping.nnplanner.domain.monthmenu.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import devping.nnplanner.domain.menucategory.entity.QMenuCategory;
import devping.nnplanner.domain.monthmenu.entity.MonthMenu;
import devping.nnplanner.domain.monthmenu.entity.QMonthMenu;
import devping.nnplanner.domain.monthmenu.entity.QMonthMenuHospital;
import devping.nnplanner.domain.monthmenu.entity.QMonthMenuSchool;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class MonthMenuRepositoryCustomImpl implements MonthMenuRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<MonthMenu> searchByMenuNameOrDateAndCategory(Long userId,
                                                             String majorCategory,
                                                             String minorCategory,
                                                             String menuName,
                                                             Integer year,
                                                             Integer month,
                                                             Pageable pageable) {

        QMonthMenu monthMenu = QMonthMenu.monthMenu;
        QMenuCategory menuCategory = QMenuCategory.menuCategory;
        QMonthMenuHospital monthMenuHospital = QMonthMenuHospital.monthMenuHospital;
        QMonthMenuSchool monthMenuSchool = QMonthMenuSchool.monthMenuSchool;

        BooleanBuilder builder = new BooleanBuilder();

        // 사용자 ID로 필터링
        builder.and(monthMenu.user.userId.eq(userId));

        // 카테고리로 필터링 (majorCategory, minorCategory)
        if (majorCategory != null && minorCategory != null) {
            builder.and(monthMenu.menuCategory.majorCategory
                .eq(majorCategory)
                .and(monthMenu.menuCategory.minorCategory.eq(minorCategory)));
        }

        // 메뉴 이름으로 필터링
        if (menuName != null) {
            builder.and(monthMenu.monthMenuName.containsIgnoreCase(menuName));
        }

        // 날짜로 필터링 (연도와 월)
        if (year != null && month != null) {
            BooleanBuilder dateBuilder = new BooleanBuilder();
            dateBuilder.or(monthMenuHospital.menuDate.year().eq(year)
                                                     .and(monthMenuHospital.menuDate.month()
                                                                                    .eq(month)))
                       .or(monthMenuSchool.menuDate.year().eq(year)
                                                   .and(
                                                       monthMenuSchool.menuDate.month().eq(month)));

            builder.and(dateBuilder);
        }

        // 페이지 데이터 조회
        List<MonthMenu> content = queryFactory
            .selectFrom(monthMenu)
            .distinct() // 중복 데이터 방지
            .leftJoin(monthMenu.menuCategory, menuCategory).fetchJoin() // 메뉴 카테고리와 페치 조인
            .leftJoin(monthMenuHospital).on(monthMenuHospital.monthMenu.eq(monthMenu)).fetchJoin()
            .leftJoin(monthMenuSchool).on(monthMenuSchool.monthMenu.eq(monthMenu)).fetchJoin()
            .where(builder)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .orderBy(monthMenu.createdAt.desc())
            .fetch();

        // 수정된 총 개수 조회 쿼리
        Long total = queryFactory
            .select(monthMenu.monthMenuId.countDistinct())
            .from(monthMenu)
            .leftJoin(monthMenu.menuCategory, menuCategory)
            .leftJoin(monthMenuHospital).on(monthMenuHospital.monthMenu.eq(monthMenu))
            .leftJoin(monthMenuSchool).on(monthMenuSchool.monthMenu.eq(monthMenu))
            .where(builder)
            .fetchOne();

        // total 값이 null인 경우 0으로 처리
        long totalElements = (total != null) ? total : 0L;

        return new PageImpl<>(content, pageable, totalElements);
    }
}
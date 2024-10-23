package devping.nnplanner.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    //공통
    NOT_FOUND(HttpStatus.NOT_FOUND, "리소스를 찾을 수 없습니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증되지 않은 접근입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근이 금지되었습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다."),

    // MONTH_MENU
    MONTH_MENU_NOT_FOUND(HttpStatus.NOT_FOUND, "식단을 찾을 수 없습니다."),

    //JWT
    INVALID_JWT(HttpStatus.BAD_REQUEST, "유효하지 않는 JWT 서명 입니다."),
    EXPIRED_JWT(HttpStatus.BAD_REQUEST, "만료된 JWT token 입니다."),
    UNSUPPORTED_JWT(HttpStatus.BAD_REQUEST, "지원되지 않는 JWT 토큰 입니다."),
    BAD_JWT(HttpStatus.BAD_REQUEST, "잘못된 JWT 토큰 입니다."),
    LOGOUT_JWT(HttpStatus.BAD_REQUEST, "로그아웃된 JWT 토큰 입니다."),

    //AUTH
    ALREADY_EMAIL(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일 입니다."),
    NOT_MATCH_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    NOT_VERIFIED_EMAIL(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 인증 정보가 없습니다."),
    NOT_EQUALS_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),

    //SURVEY
    SURVEY_NOT_FOUND(HttpStatus.NOT_FOUND, "설문을 찾을 수 없습니다."),
    INVALID_SURVEY_DEADLINE(HttpStatus.BAD_REQUEST, "설문 마감 기한이 현재 시간보다 이전입니다."),
    DUPLICATE_SURVEY(HttpStatus.CONFLICT, "동일한 설문이 이미 존재합니다."),
    QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "질문을 찾을 수 없습니다."),
    INVALID_ANSWER_TYPE(HttpStatus.BAD_REQUEST, "잘못된 응답 타입입니다.");

    private final HttpStatus status;
    private final String message;
}
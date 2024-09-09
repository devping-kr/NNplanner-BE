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

    //JWT
    INVALID_JWT(HttpStatus.BAD_REQUEST, "유효하지 않는 JWT 서명 입니다."),
    EXPIRED_JWT(HttpStatus.BAD_REQUEST, "만료된 JWT token 입니다."),
    UNSUPPORTED_JWT(HttpStatus.BAD_REQUEST, "지원되지 않는 JWT 토큰 입니다."),
    BAD_JWT(HttpStatus.BAD_REQUEST, "잘못된 JWT 토큰 입니다."),

    //AUTH
    ALREADY_EMAIL(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일 입니다."),
    NOT_MATCH_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    NOT_VERIFIED_EMAIL(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 인증 정보가 없습니다.");

    private final HttpStatus status;
    private final String message;
}
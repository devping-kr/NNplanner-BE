package devping.nnplanner.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    //공통
    NOT_FOUND(404, "리소스를 찾을 수 없습니다."),
    BAD_REQUEST(400, "잘못된 요청입니다."),
    UNAUTHORIZED(401, "인증되지 않은 접근입니다."),
    FORBIDDEN(403, "접근이 금지되었습니다."),
    INTERNAL_SERVER_ERROR(500, "서버 내부 오류입니다.");

    private final int status;
    private final String message;
}
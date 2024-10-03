package devping.nnplanner.global.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalResponse {

    public static <T> ResponseEntity<ApiResponse<T>> OK(String message, T data) {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.of(message, data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> CREATED(String message, T data) {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.of(message, data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> BAD_REQUEST(String message, T data) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.of(message, data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> INTERNAL_SERVER_ERROR(String message, T data) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.of(message, data));
    }

    public static <T> ResponseEntity<ApiResponse<T>> NO_CONTENT() {
        return ResponseEntity
            .status(HttpStatus.NO_CONTENT)
            .body(null);
    }
}
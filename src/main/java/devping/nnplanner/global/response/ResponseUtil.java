package devping.nnplanner.global.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {

    public static <T> ResponseEntity<T> OK(T body) {
        return ResponseEntity.status(HttpStatus.OK).body(body);
    }

    public static <T> ResponseEntity<T> CREATED(T body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }
}
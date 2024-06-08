package store.chikendev._2tm.dto.responce;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
// chuan hoa api
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    // thanh cong
    private int code;
    private List<String> message;
    private T result;
}

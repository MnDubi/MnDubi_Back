package festival.dev.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponseDto {
    private Long id;
    private String email;
    private String name;
    private String userCode;
    private Long groupNumberId;
}

package festival.dev.global.security.oauth.dto;

import lombok.Data;

@Data
public class NaverUserInfoResponse {
    private NaverResponse response;

    @Data
    public static class NaverResponse {
        private String id;
        private String name;
        private String email;
    }
}

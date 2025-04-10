package festival.dev.domain.user.entity;

public enum Role {
    USER,        // 인증된 사용자
    ADMIN,       // 관리자
    NOT_REGISTERED; // 회원가입이 안 된 사용자
}
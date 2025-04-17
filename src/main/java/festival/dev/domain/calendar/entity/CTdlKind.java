package festival.dev.domain.calendar.entity;

import lombok.Getter;

@Getter
public enum CTdlKind {
    GROUP("단체"), SHARE("공유"), PRIVATE("개인");
    private final String kind;

    private CTdlKind(String kind) {
        this.kind = kind;
    }
}

package festival.dev.domain.calendar.entity;

import lombok.Getter;

@Getter
public enum TdlKind {
    GROUP("단체"), SHARE("공유"), PRIVATE("개인");
    private final String kind;

    private TdlKind(String kind) {
        this.kind = kind;
    }
}

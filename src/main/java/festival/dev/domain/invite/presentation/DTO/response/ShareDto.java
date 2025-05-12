package festival.dev.domain.invite.presentation.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ShareDto {
    private String sender;
    private String receiver;
    private Long shareNumber;
}

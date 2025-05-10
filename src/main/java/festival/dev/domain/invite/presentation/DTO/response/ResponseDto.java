package festival.dev.domain.invite.presentation.DTO.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseDto {
    List<GroupDto> groupDtos;
    List<ShareDto> shareDtos;
}

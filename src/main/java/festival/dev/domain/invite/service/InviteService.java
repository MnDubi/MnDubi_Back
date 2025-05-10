package festival.dev.domain.invite.service;

import festival.dev.domain.invite.presentation.DTO.response.ResponseDto;

public interface InviteService {
    ResponseDto inviteListGet(Long userID);
}

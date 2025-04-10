package festival.dev.domain.gorupTDL.service;

import festival.dev.domain.gorupTDL.presentation.dto.request.*;
import festival.dev.domain.gorupTDL.presentation.dto.response.GInsertRes;
import festival.dev.domain.gorupTDL.presentation.dto.response.GResponse;
import festival.dev.domain.gorupTDL.presentation.dto.response.GToDoListResponse;

import java.util.List;

public interface GroupService {
    GInsertRes invite(GCreateRequest gCreateRequest, Long userid);
    void invite(GInviteReq request, Long userid);
//    GInsertRes insert(GInsertRequest gInsertReq, Long userid);
    void acceptInvite(GChoiceRequest gInviteReq, Long userid);
    void refuseInvite(GChoiceRequest gInviteReq, Long userid);
    GToDoListResponse update(GUpdateRequest gUpdateReq, Long userid);
    void delete(GDeleteRequest gDeleteReq, Long userid);
    GResponse success(GSuccessRequest request, Long userid);
    Long insert(GInsertRequest request, Long userid);
    List<?> get(Long userID);
}

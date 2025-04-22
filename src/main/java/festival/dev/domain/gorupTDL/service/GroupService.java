package festival.dev.domain.gorupTDL.service;

import festival.dev.domain.gorupTDL.presentation.dto.request.*;
import festival.dev.domain.gorupTDL.presentation.dto.response.*;

import java.util.List;

public interface GroupService {
    GInsertRes invite(GCreateRequest gCreateRequest, Long userid);
    void invite(GInviteReq request, Long userid);
    void acceptInvite(GChoiceRequest gInviteReq, Long userid);
    void refuseInvite(GChoiceRequest gInviteReq, Long userid);
    GToDoListResponse update(GUpdateRequest gUpdateReq, Long userid);
    void delete(GDeleteRequest gDeleteReq, Long userid);
    GResponse success(GSuccessRequest request, Long userid);
    Long insert(GInsertRequest request, Long userid);
    GGetRes get(Long userID);
    void finish(Long userid,Long groupNumber);
    void deleteAll(Long userID, GChoiceRequest request);
    List<GInviteGet> inviteGet(Long userid);
}

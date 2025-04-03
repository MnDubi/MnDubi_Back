package festival.dev.domain.gorupTDL.service;

import festival.dev.domain.gorupTDL.presentation.dto.request.GInsertRequest;
import festival.dev.domain.gorupTDL.presentation.dto.request.GInviteReq;
import festival.dev.domain.gorupTDL.presentation.dto.response.GInsertRes;
import festival.dev.domain.gorupTDL.presentation.dto.response.GToDoListResponse;

public interface GroupService {
    void invite(GInviteReq gInviteReq, Long userid);
    GInsertRes insert(GInsertRequest gInsertReq, Long userid);
}

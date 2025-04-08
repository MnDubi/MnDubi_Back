package festival.dev.domain.gorupTDL.service;

import festival.dev.domain.gorupTDL.entity.GroupList;
import festival.dev.domain.gorupTDL.presentation.dto.request.GInsertRequest;
import festival.dev.domain.gorupTDL.presentation.dto.request.GInviteReq;
import festival.dev.domain.gorupTDL.presentation.dto.request.GUpdateRequest;
import festival.dev.domain.gorupTDL.presentation.dto.response.GInsertRes;
import festival.dev.domain.gorupTDL.presentation.dto.response.GListDto;
import festival.dev.domain.gorupTDL.presentation.dto.response.GToDoListResponse;

public interface GroupService {
    GListDto invite(GInviteReq gInviteReq, Long userid);
    GInsertRes insert(GInsertRequest gInsertReq, Long userid);
    void acceptInvite(GInviteReq gInviteReq, Long userid);
    void refuseInvite(GInviteReq gInviteReq, Long userid);
    GToDoListResponse update(GUpdateRequest gUpdateReq, Long userid);
}

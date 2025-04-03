package festival.dev.domain.gorupTDL.service;

import festival.dev.domain.gorupTDL.presentation.dto.request.GInsertRequest;
import festival.dev.domain.gorupTDL.presentation.dto.request.GInviteReq;

public interface GroupService {
    void invite(GInviteReq gInviteReq, Long userid);
    void insert(GInsertRequest gInsertReq, Long userid);
}

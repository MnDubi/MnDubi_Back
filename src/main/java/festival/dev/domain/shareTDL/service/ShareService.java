package festival.dev.domain.shareTDL.service;

import festival.dev.domain.shareTDL.entity.Share;
import festival.dev.domain.shareTDL.entity.ShareJoin;
import festival.dev.domain.shareTDL.presentation.dto.request.ShareCreateReq;
import festival.dev.domain.shareTDL.presentation.dto.request.ShareInsertReq;
import festival.dev.domain.shareTDL.presentation.dto.request.ShareInviteReq;
import festival.dev.domain.shareTDL.presentation.dto.request.ShareModifyReq;
import festival.dev.domain.shareTDL.presentation.dto.response.ShareNumberRes;

public interface ShareService {
    ShareNumberRes createShare(ShareCreateReq request, Long userID);
    ShareNumberRes inviteShare(Long userID, ShareInviteReq request);
    ShareJoin modifyShare(Long userID, ShareModifyReq request);
    ShareJoin insertShare(Long userID, ShareInsertReq request);
}

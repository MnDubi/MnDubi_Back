package festival.dev.domain.shareTDL.service;

import festival.dev.domain.shareTDL.presentation.dto.request.ShareCreateReq;
import festival.dev.domain.shareTDL.presentation.dto.request.ShareInsertReq;
import festival.dev.domain.shareTDL.presentation.dto.request.ShareInviteReq;
import festival.dev.domain.shareTDL.presentation.dto.request.ShareModifyReq;
import festival.dev.domain.shareTDL.presentation.dto.response.ShareNumberRes;
import festival.dev.domain.shareTDL.presentation.dto.response.ShareRes;

public interface ShareService {
    ShareNumberRes createShare(ShareCreateReq request, Long userID);
    ShareNumberRes inviteShare(Long userID, ShareInviteReq request);
    ShareRes modifyShare(Long userID, ShareModifyReq request);
    ShareRes insertShare(Long userID, ShareInsertReq request);
}

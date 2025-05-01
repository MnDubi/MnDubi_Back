package festival.dev.domain.shareTDL.service;

import festival.dev.domain.shareTDL.presentation.dto.request.*;
import festival.dev.domain.shareTDL.presentation.dto.response.ShareNumberRes;
import festival.dev.domain.shareTDL.presentation.dto.response.ShareRes;

public interface ShareService {
    ShareNumberRes createShare(ShareCreateReq request, Long userID);
    ShareNumberRes inviteShare(Long userID, ShareInviteReq request);
    ShareRes modifyShare(Long userID, ShareModifyReq request);
    ShareRes insertShare(Long userID, ShareInsertReq request);
    void deleteShare(Long userID, ShareDeleteReq request);
}

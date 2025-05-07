package festival.dev.domain.shareTDL.service;

import festival.dev.domain.shareTDL.presentation.dto.request.*;
import festival.dev.domain.shareTDL.presentation.dto.response.ShareGetRes;
import festival.dev.domain.shareTDL.presentation.dto.response.ShareJoinRes;
import festival.dev.domain.shareTDL.presentation.dto.response.ShareNumberRes;
import festival.dev.domain.shareTDL.presentation.dto.response.ShareRes;

import java.util.List;

public interface ShareService {
    ShareNumberRes createShare(ShareCreateReq request, Long userID);
    ShareNumberRes inviteShare(Long userID, ShareInviteReq request);
    List<ShareGetRes> get(Long userId);
}

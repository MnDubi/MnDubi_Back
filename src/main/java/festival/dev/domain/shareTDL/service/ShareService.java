package festival.dev.domain.shareTDL.service;

import festival.dev.domain.shareTDL.presentation.dto.ShareCreateReq;

public interface ShareService {
    void createShare(ShareCreateReq request,Long userID);
}

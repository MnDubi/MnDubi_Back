package festival.dev.domain.shareTDL.service;

import festival.dev.domain.shareTDL.presentation.dto.ShareCreateReq;
import festival.dev.domain.shareTDL.presentation.dto.ShareInsertReq;

public interface ShareService {
    void createShare(ShareCreateReq request,Long userID);
    void insertShare(Long userID, ShareInsertReq request);
}

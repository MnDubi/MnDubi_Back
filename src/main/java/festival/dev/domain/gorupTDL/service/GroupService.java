package festival.dev.domain.gorupTDL.service;

import festival.dev.domain.gorupTDL.presentation.dto.request.*;
import festival.dev.domain.gorupTDL.presentation.dto.response.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface GroupService {
    GInsertRes invite(GCreateRequest gCreateRequest, Long userid);
    void invite(GInviteReq request, Long userid);
    void acceptInvite(Long userid, GChoiceReq request);
    void refuseInvite(Long userid, GChoiceReq request);
    GToDoListResponse update(GUpdateRequest gUpdateReq, Long userid);
    void delete(GDeleteRequest gDeleteReq, Long userid);
    GResponse success(GSuccessRequest request, Long userid);
    Long insert(GInsertRequest request, Long userid);
    GGetRes get(Long userID);
    void deleteAll(Long userID);
    List<GInviteGet> inviteGet(Long userid);
    List<GCreateWsRes> userList(Long userid);
    boolean isGroupMember(Long userId);

    SseEmitter sseConnect(Long groupNum);
    SseEmitter groupInviteSseConnect(String userCode);
    List<GCreateWsRes> findByUsername(Long userID,String friendUsername);
}

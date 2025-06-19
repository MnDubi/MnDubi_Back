package festival.dev.domain.shareTDL.service.impl;

import festival.dev.domain.TDL.entity.ToDoList;
import festival.dev.domain.TDL.repository.ToDoListRepository;
import festival.dev.domain.calendar.entity.CTdlKind;
import festival.dev.domain.calendar.entity.Calendar;
import festival.dev.domain.calendar.entity.Calendar_tdl_ids;
import festival.dev.domain.calendar.repository.CalendarRepository;
import festival.dev.domain.friendship.repository.FriendshipRepository;
import festival.dev.domain.shareTDL.entity.Share;
import festival.dev.domain.shareTDL.entity.ShareNumber;
import festival.dev.domain.shareTDL.presentation.dto.request.*;
import festival.dev.domain.shareTDL.presentation.dto.response.*;
import festival.dev.domain.shareTDL.repository.ShareNumberRepo;
import festival.dev.domain.shareTDL.repository.ShareRepository;
import festival.dev.domain.shareTDL.service.ShareService;
import festival.dev.domain.user.entity.User;
import festival.dev.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShareServiceImpl implements ShareService {
    private final ShareRepository shareRepository;
    private final ShareNumberRepo shareNumberRepo;
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final ToDoListRepository toDoListRepository;
    private final CalendarRepository calendarRepository;
    private final Logger log = LoggerFactory.getLogger(ShareServiceImpl.class);
    private final Map<String, CopyOnWriteArrayList<SseEmitter>> shareInviteEmitters = new ConcurrentHashMap<>();

    @Transactional
    public ShareNumberRes createShare(ShareCreateReq request, Long userID) {
        User user = getUserByID(userID);
        checkShareByUser(user);
        ShareNumber shareNumber = ShareNumber.builder()
                .number(1L).build();
        shareNumberRepo.save(shareNumber);

        Share share = Share.builder()
                .user(user)
                .shareNumber(shareNumber)
                .includeShared(request.isIncludeShared())
                .showShared(request.isShowShared())
                .accepted(true)
                .owner(true)
                .build();
        shareRepository.save(share);

        for (String userCode : request.getUserCode()) {
            if (!userCode.isBlank()) {
                checkFriendship(getUserByCode(userCode),user);
                checkShareByUser(getUserByCode(userCode));
                share = Share.builder()
                        .user(getUserByCode(userCode))
                        .shareNumber(shareNumber)
                        .includeShared(request.isIncludeShared())
                        .showShared(request.isShowShared())
                        .accepted(false)
                        .owner(false)
                        .build();
                shareRepository.save(share);
            }
        }
        return ShareNumberRes.builder()
                .shareNumber(shareNumber.getId())
                .build();
    }

    @Transactional
    public ShareNumberRes inviteShare(Long userID, ShareInviteReq request){
        User user = getUserByID(userID);
        Share share = getShareByUser(user);
        ShareNumber shareNumber = share.getShareNumber();
        String code = user.getUserCode();

        for (String userCode : request.getUserCodes()){
            User receiver = getUserByCode(userCode);
            checkFriendship(user,receiver);
            checkShareByUser(receiver);
            shareRepository.save(Share.builder()
                            .shareNumber(shareNumber)
                            .owner(false)
                            .includeShared(share.isIncludeShared())
                            .accepted(share.isAccepted())
                            .user(receiver)
                            .showShared(share.isShowShared())
                    .build());
        }

        List<SseEmitter> emitters = shareInviteEmitters.getOrDefault(code, new CopyOnWriteArrayList<>());
        ShareInviteDto shareInviteDto = ShareInviteDto.builder()
                .accept(false)
                .shareNumber(shareNumber.getId())
                .userName(user.getName())
                .build();
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("share_invite").data(shareInviteDto));
            } catch (IOException e) {
                shareInviteEmitters.get(code).remove(emitter);
            }
        }

        return ShareNumberRes.builder()
                .shareNumber(shareNumber.getId())
                .build();
    }

    public List<ShareGetRes> get(Long userId){
        User user = getUserByID(userId);
        ShareNumber shareNumber = getShareNumber(user);
        List<ShareGetRes> shareGetResList = new ArrayList<>();
        List<Share> shares = shareRepository.findByShareNumberAndAcceptedTrue(shareNumber);
        for (Share share : shares) {
            User member = share.getUser();
            List<ShareJoinRes> shareJoinResList = new ArrayList<>();
            Long part = 0L;
            List<ToDoList> tdls = toDoListRepository.findByCurrentDateAndUserIDAndSharedIsTrue(toDay(), member.getId());
            for(ToDoList tdl : tdls) {
                ShareJoinRes shareJoinRes = ShareJoinRes.builder()
                        .title(tdl.getTitle())
                        .category(tdl.getCategory().getCategoryName())
                        .shareNumber(shareNumber.getId())
                        .user_code(member.getUserCode())
                        .completed(tdl.getCompleted())
                        .build();

                shareJoinResList.add(shareJoinRes);
                if(tdl.getCompleted()){
                    part++;
                }
            }
            if (share.isShowShared()) {
                ShareGetRes response = ShareGetRes.builder()
                        .username(member.getName())
                        .every((long) tdls.size())
                        .part(part)
                        .shareJoinRes(shareJoinResList)
                        .build();
                shareGetResList.add(response);
            }
            else{
                ShareGetRes response = ShareGetRes.builder()
                        .username(member.getName())
                        .shareJoinRes(shareJoinResList)
                        .build();
                shareGetResList.add(response);
            }

        }
        return shareGetResList;
    }

    @Transactional
    @Scheduled(cron = "59 59 23 * * *")
    public void reset(){
        List<ShareNumber> shareNumbers = shareNumberRepo.findAll();
        for (ShareNumber shareNumber : shareNumbers) {
            List<Share> shares = shareRepository.findByShareNumberAndAcceptedTrue(shareNumber);
            for (Share share : shares) {
                if (share.isIncludeShared()) {
                    List<ToDoList> tdls = toDoListRepository.findByUserAndEndDate(share.getUser(), toDay());
                    int part = toDoListRepository.findByUserAndEndDateAndCompleted(share.getUser(), toDay(), true).size();
                    List<Calendar_tdl_ids> tdlIDs = tdls.stream()
                            .map(tdl -> Calendar_tdl_ids.builder()
                                    .tdlID(tdl.getId())
                                    .kind(CTdlKind.SHARE)
                                    .build())
                            .collect(Collectors.toList());
                    Calendar calendar = Calendar.builder()
                            .user(share.getUser())
                            .every(tdlIDs.size())
                            .part(part)
                            .kind("SHARE")
                            .toDoListId(tdlIDs)
                            .build();
                    calendarRepository.save(calendar);
                }
            }
        }
    }

    public List<ShareUserList> getUserList(Long userId){
        User user = getUserByID(userId);
        ShareNumber shareNumber = getShareNumber(user);
        List<ShareUserList> shareUserLists = new ArrayList<>();
        List<Share> shares = shareRepository.findByShareNumberAndAcceptedTrue(shareNumber);
        for (Share share : shares) {
            ShareUserList shareUserList = ShareUserList.builder()
                    .email(share.getUser().getEmail())
                    .name(share.getUser().getName())
                    .userCode(share.getUser().getUserCode())
                    .build();
            shareUserLists.add(shareUserList);
        }
        return shareUserLists;
    }

    public SseEmitter shareInviteSseConnect(String userCode) {
        SseEmitter emitter = new SseEmitter(300 * 1000L);

        shareInviteEmitters.putIfAbsent(userCode, new CopyOnWriteArrayList<>());
        shareInviteEmitters.get(userCode).add(emitter);

        emitter.onCompletion(() -> shareInviteEmitters.get(userCode).remove(emitter));
        emitter.onTimeout(() -> shareInviteEmitters.get(userCode).remove(emitter));
        emitter.onError(e -> shareInviteEmitters.get(userCode).remove(emitter));

        return emitter;
    }
    //---------------------

    User getUserByID(Long userID){
        return userRepository.findById(userID).orElseThrow(()-> new IllegalArgumentException("없는 유저입니다.(ID)"));
    }
    User getUserByCode(String code){
        return userRepository.findByUserCode(code).orElseThrow(()-> new IllegalArgumentException("없는 유저입니다.(Code)"));
    }
    void checkFriendship(User user1, User user2){
        if(!friendshipRepository.existsByRequesterAndAddressee(user1,user2) && !friendshipRepository.existsByRequesterAndAddressee(user2,user1)){
            throw new IllegalArgumentException("친구가 아닌 사용자입니다.");
        }
    }
    void checkShareByUser(User user){
        if(shareRepository.existsByUser(user)){
            throw new IllegalArgumentException("이미 공유 TDL에 존재하는 유저입니다. " + user.getUserCode());
        }
    }
    Share getShareByUser(User user){
        return shareRepository.findByUser(user).orElseThrow(()-> new IllegalArgumentException("공유 TDL에 참가하지 않은 사용자입니다."));
    }
    Share getShareByUserAndAccept(User user){
        return shareRepository.findByUserAndAcceptedTrue(user).orElseThrow(()-> new IllegalArgumentException("공유 TDL에 참가하지 않은 사용자입니다. accept True"));
    }
    ShareNumber getShareNumber(User user){
        return getShareByUserAndAccept(user).getShareNumber();
    }
    public String toDay(){
        LocalDateTime createAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime();
        DateTimeFormatter yearMonthDayFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return createAt.format(yearMonthDayFormatter);
    }
    Share getShareByShareNumber(ShareNumber shareNumber, User user){
        return shareRepository.findByShareNumberAndUserAndAcceptedFalse(shareNumber,user).orElseThrow(()-> new IllegalArgumentException("존재하지 않는 공유 방입니다."));
    }
}

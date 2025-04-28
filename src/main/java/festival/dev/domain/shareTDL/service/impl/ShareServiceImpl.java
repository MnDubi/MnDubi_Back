package festival.dev.domain.shareTDL.service.impl;

import festival.dev.domain.friendship.repository.FriendshipRepository;
import festival.dev.domain.shareTDL.entity.Share;
import festival.dev.domain.shareTDL.entity.ShareNumber;
import festival.dev.domain.shareTDL.presentation.dto.ShareCreateReq;
import festival.dev.domain.shareTDL.presentation.dto.ShareInsertReq;
import festival.dev.domain.shareTDL.repository.ShareJoinRepo;
import festival.dev.domain.shareTDL.repository.ShareNumberRepo;
import festival.dev.domain.shareTDL.repository.ShareRepository;
import festival.dev.domain.shareTDL.service.ShareService;
import festival.dev.domain.user.entity.User;
import festival.dev.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShareServiceImpl implements ShareService {
    private final ShareRepository shareRepository;
    private final ShareJoinRepo shareJoinRepo;
    private final ShareNumberRepo shareNumberRepo;
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;

    @Transactional
    public void createShare(ShareCreateReq request,Long userID) {
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
    }

    @Transactional
    public void insertShare(Long userID, ShareInsertReq request){
        User user = getUserByID(userID);
        Share share = getShareByUser(user);
        ShareNumber shareNumber = share.getShareNumber();
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
            throw new IllegalArgumentException("이미 공유 TDL에 존재하는 유저입니다. " + user.getName() + user.getUserCode());
        }
    }
    Share getShareByUser(User user){
        return shareRepository.findByUser(user).orElseThrow(()-> new IllegalArgumentException("공유 TDL에 참가하지 않은 사용자입니다."));
    }
}

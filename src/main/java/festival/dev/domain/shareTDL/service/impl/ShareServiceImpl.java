package festival.dev.domain.shareTDL.service.impl;

import festival.dev.domain.category.entity.Category;
import festival.dev.domain.category.repository.CategoryRepository;
import festival.dev.domain.friendship.repository.FriendshipRepository;
import festival.dev.domain.shareTDL.entity.Share;
import festival.dev.domain.shareTDL.entity.ShareJoin;
import festival.dev.domain.shareTDL.entity.ShareNumber;
import festival.dev.domain.shareTDL.presentation.dto.request.*;
import festival.dev.domain.shareTDL.presentation.dto.response.ShareNumberRes;
import festival.dev.domain.shareTDL.presentation.dto.response.ShareRes;
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
    private final CategoryRepository categoryRepository;

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
        return ShareNumberRes.builder()
                .shareNumber(shareNumber.getId())
                .build();
    }

    @Transactional
    public ShareRes modifyShare(Long userID, ShareModifyReq request){
        User user = getUserByID(userID);
        getShareByUserAndAccept(user,true);

        ShareJoin shareJoin = shareJoinRepo.findByTitleAndUser(request.getTitle(),user).orElseThrow(()-> new IllegalArgumentException("존재하지 않은 TDL입니다ㅓ."));
        checkShareJoinByTitleAndUser(request.getChange(),user);
        shareJoin = shareJoin.toBuilder()
                .title(request.getChange())
                .build();
        shareJoinRepo.save(shareJoin);
        return shareResponse(shareJoin);
    }

    @Transactional
    public ShareRes insertShare(Long userID, ShareInsertReq request){
        User user = getUserByID(userID);
        getShareByUserAndAccept(user, true);
        ShareNumber shareNumber = getShareNumber(user);
        checkShareJoinByTitleAndUser(request.getTitle(),user);
        ShareJoin shareJoin = ShareJoin.builder()
                .shareNumber(shareNumber)
                .category(getCategory(request.getCategory()))
                .title(request.getTitle())
                .completed(false)
                .user(user)
                .build();
        shareJoinRepo.save(shareJoin);
        return shareResponse(shareJoin);
    }

    @Transactional
    public void deleteShare(Long userID, ShareDeleteReq request){
        User user = getUserByID(userID);
        checkShareJoinByTitleAndUserNot(request.getTitle(),user);
        shareJoinRepo.deleteByTitleAndUser(request.getTitle(), user);
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
    Share getShareByUserAndAccept(User user, boolean accept){
        if(accept){
            return shareRepository.findByUserAndAcceptedTrue(user).orElseThrow(()-> new IllegalArgumentException("공유 TDL에 참가하지 않은 사용자입니다. accept True"));
        }
        else{
            return shareRepository.findByUserAndAcceptedFalse(user).orElseThrow(()-> new IllegalArgumentException("공유 TDL에 참가하지 않은 사용자입니다. accept False"));
        }
    }
    Category getCategory(String category){
        return categoryRepository.findByCategoryName(category);
    }
    ShareRes shareResponse(ShareJoin shareJoin){
        return ShareRes.builder()
                .accept(shareJoin.isCompleted())
                .category(shareJoin.getCategory().getCategoryName())
                .shareNumber(shareJoin.getShareNumber().getId())
                .title(shareJoin.getTitle())
                .build();
    }
    void checkShareJoinByTitleAndUser(String title,User user){
        if (shareJoinRepo.existsByTitleAndUser(title,user))
            throw new IllegalArgumentException("이미 존재하는 TDL입니다.");
    }
    void checkShareJoinByTitleAndUserNot(String title,User user){
        if (!shareJoinRepo.existsByTitleAndUser(title,user))
            throw new IllegalArgumentException("존재하지 않은 TDL입니다.");
    }
    ShareNumber getShareNumber(User user){
        return getShareByUserAndAccept(user,true).getShareNumber();
    }
}

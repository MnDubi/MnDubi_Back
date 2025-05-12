package festival.dev.domain.shareTDL.service.impl;

import festival.dev.domain.category.entity.Category;
import festival.dev.domain.category.repository.CategoryRepository;
import festival.dev.domain.friendship.repository.FriendshipRepository;
import festival.dev.domain.shareTDL.entity.Share;
import festival.dev.domain.shareTDL.entity.ShareJoin;
import festival.dev.domain.shareTDL.entity.ShareNumber;
import festival.dev.domain.shareTDL.presentation.dto.request.ShareCreateReq;
import festival.dev.domain.shareTDL.presentation.dto.request.ShareInsertReq;
import festival.dev.domain.shareTDL.presentation.dto.request.ShareInviteReq;
import festival.dev.domain.shareTDL.presentation.dto.request.ShareModifyReq;
import festival.dev.domain.shareTDL.presentation.dto.response.ShareNumberRes;
import festival.dev.domain.shareTDL.repository.ShareJoinRepo;
import festival.dev.domain.shareTDL.repository.ShareNumberRepo;
import festival.dev.domain.shareTDL.repository.ShareRepository;
import festival.dev.domain.shareTDL.service.ShareService;
import festival.dev.domain.user.entity.User;
import festival.dev.domain.user.repository.UserRepository;
import festival.dev.domain.ai.service.AIClassifierService;
import festival.dev.domain.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShareServiceImpl implements ShareService {
    private final ShareRepository shareRepository;
    private final ShareJoinRepo shareJoinRepo;
    private final ShareNumberRepo shareNumberRepo;
    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;
    private final AIClassifierService aiClassifierService;


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
    public ShareJoin modifyShare(Long userID, ShareModifyReq request){
        User user = getUserByID(userID);
        Share share = getShareByUserAndAccept(user,true);
        ShareNumber shareNumber = share.getShareNumber();
        ShareJoin shareJoin = shareJoinRepo.findByTitleAndShareNumber(request.getTitle(), shareNumber).orElseThrow(()-> new IllegalArgumentException("존재하지 않은 TDL입니다ㅓ."));
        if (shareJoinRepo.findByTitleAndShareNumber(request.getChange(), shareNumber).isPresent())
            throw new IllegalArgumentException("이미 존재하는 TDL입니다.");
        shareJoin = shareJoin.toBuilder()
                .title(request.getChange())
                .build();
        shareJoinRepo.save(shareJoin);
        return shareJoin;
    }

    @Transactional
    public ShareJoin insertShare(Long userID, ShareInsertReq request){
        User user = getUserByID(userID);
        Share share = getShareByUserAndAccept(user, true);
        ShareNumber shareNumber = share.getShareNumber();
        ShareJoin shareJoin = ShareJoin.builder()
                .shareNumber(shareNumber)
                .category(getCategory(request.getCategory()))
                .title(request.getTitle())
                .completed(false)
                .build();
        shareJoinRepo.save(shareJoin);
        return shareJoin;
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
    Category getCategory(String categoryName) {
        Map<String, List<Double>> categoryMap = categoryRepository.findAll().stream()
                .collect(Collectors.toMap(
                        Category::getName,
                        c -> categoryService.convertJsonToEmbedding(c.getEmbeddingJson())
                ));

        String classifiedCategoryName = aiClassifierService.classifyCategoryWithAI(categoryName, categoryMap);

        return categoryService.findOrCreateByName(classifiedCategoryName,
                categoryMap.containsKey(classifiedCategoryName)
                        ? categoryMap.get(classifiedCategoryName)
                        : categoryService.getEmbeddingFromText(classifiedCategoryName));
    }
}

package festival.dev.domain.shareTDL.service.impl;

import festival.dev.domain.shareTDL.entity.Share;
import festival.dev.domain.shareTDL.entity.ShareNumber;
import festival.dev.domain.shareTDL.presentation.dto.ShareCreateReq;
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

    @Transactional
    public void createShare(ShareCreateReq request,Long userID) {
        User user = getUserByID(userID);

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
    //---------------------
    User getUserByID(Long userID){
        return userRepository.findById(userID).orElseThrow(()-> new IllegalArgumentException("없는 유저입니다.(ID)"));
    }
    User getUserByCode(String code){
        return userRepository.findByUserCode(code).orElseThrow(()-> new IllegalArgumentException("없는 유저입니다.(Code)"));
    }

}

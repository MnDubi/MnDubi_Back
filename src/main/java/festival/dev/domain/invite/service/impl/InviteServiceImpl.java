package festival.dev.domain.invite.service.impl;

import festival.dev.domain.gorupTDL.entity.Group;
import festival.dev.domain.gorupTDL.entity.GroupList;
import festival.dev.domain.gorupTDL.repository.GroupListRepo;
import festival.dev.domain.gorupTDL.repository.GroupRepository;
import festival.dev.domain.invite.presentation.DTO.response.GroupDto;
import festival.dev.domain.invite.presentation.DTO.response.ResponseDto;
import festival.dev.domain.invite.presentation.DTO.response.ShareDto;
import festival.dev.domain.invite.service.InviteService;
import festival.dev.domain.shareTDL.entity.Share;
import festival.dev.domain.shareTDL.repository.ShareRepository;
import festival.dev.domain.user.entity.User;
import festival.dev.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InviteServiceImpl implements InviteService {
    private final UserRepository userRepository;
    private final ShareRepository shareRepository;
    private final GroupListRepo groupListRepo;
    private final GroupRepository groupRepository;

    public ResponseDto inviteListGet(Long userID){
        User user = getUser(userID);
        List<GroupDto> groupDtos = new ArrayList<>();
        List<ShareDto> shareDtos = new ArrayList<>();
        ResponseDto response = new ResponseDto();
        if (groupListRepo.findByUserAndAcceptTrue(user).isEmpty()){
            List<GroupList> groupLists = groupListRepo.findByUserAndAcceptFalse(user);
            for (GroupList groupList : groupLists){
                User sender = groupRepository.findUserByGroupNumber(groupList.getGroupNumber());
                GroupDto groupDto = GroupDto.builder()
                        .groupNumber(groupList.getGroupNumber().getId())
                        .receiver(user.getName())
                        .sender(sender.getName())
                        .build();
                groupDtos.add(groupDto);
            }
        }
        if (shareRepository.findByUserAndAcceptedTrue(user).isEmpty()){
            List<Share> shares = shareRepository.findByUserAndAcceptedFalse(user);
            for(Share share : shares){
                Share shareOwner = shareRepository.findByShareNumberAndOwnerIsTrue(share.getShareNumber());
                ShareDto shareDto = ShareDto.builder()
                        .shareNumber(share.getShareNumber().getId())
                        .receiver(user.getName())
                        .sender(shareOwner.getUser().getName())
                        .build();
                shareDtos.add(shareDto);
            }
        }

        response = ResponseDto.builder()
                .groupDtos(groupDtos)
                .shareDtos(shareDtos)
                .build();

        return response;
    }

    User getUser(Long id){
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않은 UserID"));
    }

}

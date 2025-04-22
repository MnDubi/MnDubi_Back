package festival.dev;

import festival.dev.domain.friendship.repository.FriendshipRepository;
import festival.dev.domain.gorupTDL.presentation.dto.request.GCreateWsReq;
import festival.dev.domain.gorupTDL.presentation.dto.response.GCreateWsRes;
import festival.dev.domain.gorupTDL.service.impl.GroupServiceImpl;
import festival.dev.domain.user.entity.User;
import festival.dev.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WebSocketTest {
    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private GroupServiceImpl groupService;

    @Mock
    private FriendshipRepository friendshipRepository;
    @Mock
    private UserRepository userRepository;

    @Test
    void testCreateWs_UserNotFound() {
        // given
        GCreateWsReq request = GCreateWsReq
                .builder()
                .email("nonexistent@example.com")
                .friend("john")
                .build();

        // when
        groupService.createWs(request);

        // then
        verify(messagingTemplate, times(1)).convertAndSend(eq("/group/create/nonexistent@example.com"), eq("사용자를 찾을 수 없음"));
    }
    @Test
    void testCreateWs_FriendsNotFound() {
        // given
        GCreateWsReq request = GCreateWsReq
                .builder()
                .email("s1@gsm.hs.kr")
                .friend("john")
                .build();

        User user = User.builder()
                .email("s1@gsm.hs.kr")
                .name("곽더미")
                .password("$2a$10$F6ZXnifUQA4FBb/64MuKROWeZgT4dcAGjXifXw5JEwWW.xbVe1ZL2")
                .provider("LOCAL")
                .role("USER")
                .userCode("3IK9OK4U")
                .createdAt(LocalDateTime.parse("2025-04-08T16:57:48.770468"))
                .updatedAt(LocalDateTime.parse("2025-04-08T16:57:48.770468"))
                .build();

        when(userRepository.findByEmail("s1@gsm.hs.kr")).thenReturn(Optional.of(user));
        when(userRepository.findByName("john")).thenReturn(Collections.emptyList());  // 친구 없음

        // when
        groupService.createWs(request);

        // then
        verify(messagingTemplate, times(1)).convertAndSend(eq("/group/create/s1@gsm.hs.kr"), eq("존재하지 않는 유저입니다."));
    }

    @Test
    void testCreateWs_Success() {
        // given
        GCreateWsReq request = GCreateWsReq.builder()
                .email("s1@gsm.hs.kr")
                .friend("김더미")
                .build();

        User user = User.builder()
                .email("s1@gsm.hs.kr")
                .name("곽더미")
                .password("$2a$10$F6ZXnifUQA4FBb/64MuKROWeZgT4dcAGjXifXw5JEwWW.xbVe1ZL2")
                .provider("LOCAL")
                .role("USER")
                .userCode("3IK9OK4U")
                .createdAt(LocalDateTime.parse("2025-04-08T16:57:48.770468"))
                .updatedAt(LocalDateTime.parse("2025-04-08T16:57:48.770468"))
                .build();

        User friend = User.builder()
                .email("s3@gsm.hs.kr")
                .name("김더미")
                .password("$2a$10$fj/jguR.VIkeV.8EWI5wTusWJ/g/j3l/9g/kp5cO8NbLi6MoOtUV2")
                .provider("LOCAL")
                .role("USER")
                .userCode("SALWSM7C")
                .createdAt(LocalDateTime.parse("2025-04-08T20:01:31.143794"))
                .updatedAt(LocalDateTime.parse("2025-04-08T20:01:31.143794"))
                .build();

        when(userRepository.findByEmail("s1@gsm.hs.kr")).thenReturn(Optional.of(user));
        when(userRepository.findByName("김더미")).thenReturn(Collections.singletonList(friend));
        when(friendshipRepository.existsByRequesterAndAddressee(user, friend)).thenReturn(true);

        // when
        groupService.createWs(request);

        // then
        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(messagingTemplate).convertAndSend(eq("/group/create/s1@gsm.hs.kr"), captor.capture());

        List<GCreateWsRes> sentResponses = captor.getValue();

        assertThat(sentResponses).hasSize(1);
        assertThat(sentResponses.get(0).getEmail()).isEqualTo("s3@gsm.hs.kr");
        assertThat(sentResponses.get(0).getName()).isEqualTo("김더미");
        assertThat(sentResponses.get(0).getUserCode()).isEqualTo("SALWSM7C");
    }

    @Test
    void testCreateWs_FriendshipNotFound() {
        // given
        GCreateWsReq request = GCreateWsReq.builder()
                .email("s1@gsm.hs.kr")
                .friend("김더미")
                .build();

        User user = User.builder()
                .email("s1@gsm.hs.kr")
                .name("곽더미")
                .password("$2a$10$F6ZXnifUQA4FBb/64MuKROWeZgT4dcAGjXifXw5JEwWW.xbVe1ZL2")
                .provider("LOCAL")
                .role("USER")
                .userCode("3IK9OK4U")
                .createdAt(LocalDateTime.parse("2025-04-08T16:57:48.770468"))
                .updatedAt(LocalDateTime.parse("2025-04-08T16:57:48.770468"))
                .build();

        User friend = User.builder()
                .email("s3@gsm.hs.kr")
                .name("김더미")
                .password("$2a$10$fj/jguR.VIkeV.8EWI5wTusWJ/g/j3l/9g/kp5cO8NbLi6MoOtUV2")
                .provider("LOCAL")
                .role("USER")
                .userCode("SALWSM7C")
                .createdAt(LocalDateTime.parse("2025-04-08T20:01:31.143794"))
                .updatedAt(LocalDateTime.parse("2025-04-08T20:01:31.143794"))
                .build();

        when(userRepository.findByEmail("s1@gsm.hs.kr")).thenReturn(Optional.of(user));
        when(userRepository.findByName("김더미")).thenReturn(Collections.singletonList(friend));
        when(friendshipRepository.existsByRequesterAndAddressee(user, friend)).thenReturn(false);  // 친구 관계 없음

        // when
        groupService.createWs(request);

        // then
        verify(messagingTemplate, times(1)).convertAndSend(eq("/group/create/s1@gsm.hs.kr"), eq("존재하지 않는 친구입니다"));
    }
}

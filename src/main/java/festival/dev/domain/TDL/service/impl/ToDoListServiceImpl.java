package festival.dev.domain.TDL.service.impl;

import festival.dev.domain.TDL.entity.ToDoList;
import festival.dev.domain.TDL.presentation.dto.request.*;
import festival.dev.domain.TDL.presentation.dto.response.ToDoListResponse;
import festival.dev.domain.TDL.repository.ToDoListRepository;
import festival.dev.domain.TDL.service.ToDoListService;
import festival.dev.domain.calendar.entity.Calendar;
import festival.dev.domain.calendar.entity.Calendar_tdl_ids;
import festival.dev.domain.calendar.entity.CTdlKind;
import festival.dev.domain.calendar.repository.CalendarRepository;
import festival.dev.domain.category.entity.Category;
import festival.dev.domain.category.service.CategoryService;
import festival.dev.domain.category.repository.CategoryRepository;
import festival.dev.domain.gorupTDL.presentation.dto.response.MemberDto;
import festival.dev.domain.shareTDL.entity.Share;
import festival.dev.domain.shareTDL.entity.ShareNumber;
import festival.dev.domain.shareTDL.presentation.dto.request.ShareChoiceRequest;
import festival.dev.domain.shareTDL.repository.ShareNumberRepo;
import festival.dev.domain.shareTDL.repository.ShareRepository;
import festival.dev.domain.user.entity.User;
import festival.dev.domain.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import festival.dev.domain.ai.service.AIClassifierService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ToDoListServiceImpl implements ToDoListService {

    private final ShareNumberRepo shareNumberRepo;
    private final ToDoListRepository toDoListRepository;
    private final CalendarRepository calendarRepository;
    private final CategoryRepository categoryRepository;
//    private final RestTemplate restTemplate;
    private final CategoryService categoryService;
    private final UserRepository userRepository;
    public final Map<Long, CopyOnWriteArrayList<SseEmitter>> shareEmitters = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private final Logger log = LoggerFactory.getLogger(ToDoListServiceImpl.class);
    private final ShareRepository shareRepository;
    private final AIClassifierService aiClassifierService;

    public void input(InsertRequest request, Long userId) {
        String title = request.getTitle();
        User user = getUser(userId);

        Map<String, List<Double>> categoryMap = categoryRepository.findAll().stream()
                .collect(Collectors.toMap(
                        Category::getName,
                        c -> categoryService.convertJsonToEmbedding(c.getEmbeddingJson())
                ));

        String categoryName = aiClassifierService.classifyCategoryWithAI(title, categoryMap);

        Category category = categoryService.findOrCreateByName(categoryName,
                categoryMap.containsKey(categoryName)
                        ? categoryMap.get(categoryName)
                        : categoryService.getEmbeddingFromText(categoryName)
        );

        inputSetting(title, user, request.getEndDate(), category);

        checkEndDate(request.getEndDate());

        ToDoList toDoList = ToDoList.builder()
                .title(request.getTitle())
                .completed(false)
                .user(user)
                .shared(true)
                .startDate(request.getEndDate())
                .endDate(request.getEndDate())
                .category(category)
                .build();

        ToDoListResponse response = toDoListResponseBuild(user, category, toDoList);
        if(shareRepository.findByUser(user).isPresent()){
            sendToShare(toDoList, response, user,"shared");
        }
        toDoListRepository.save(toDoList);
    }

    public void input(InsertUntilRequest request, Long id) {
        String title = request.getTitle();
        User user = getUser(id);

        if (request.getStartDate().compareTo(request.getEndDate()) > 0) {
            throw new IllegalArgumentException("시작하는 날짜가 끝나는 날짜보다 늦을 수 없습니다.");
        }

        checkEndDate(request.getEndDate());

        Map<String, List<Double>> categoryMap = categoryRepository.findAll().stream()
                .collect(Collectors.toMap(
                        Category::getName,
                        c -> categoryService.convertJsonToEmbedding(c.getEmbeddingJson())
                ));

        String categoryName = aiClassifierService.classifyCategoryWithAI(title, categoryMap);

        Category category = categoryService.findOrCreateByName(categoryName,
                categoryMap.containsKey(categoryName)
                        ? categoryMap.get(categoryName)
                        : categoryService.getEmbeddingFromText(categoryName)
        );

        checkExist(user, title, request.getEndDate());

        toDoListRepository.save(ToDoList.builder()
                        .title(title)
                        .completed(false)
                        .user(user)
                        .startDate(request.getStartDate())
                        .endDate(request.getEndDate())
                        .category(category)
                .build());
        inputSetting(title, user, request.getEndDate(), category);
        ToDoList toDoList = ToDoList.builder()
                .title(title)
                .completed(false)
                .shared(true)
                .user(user)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .category(category)
                .build();

        ToDoListResponse response = toDoListResponseBuild(user, category, toDoList);

        if(shareRepository.findByUser(user).isPresent()) {
            sendToShare(toDoList, response, user, "shared");
        }

        toDoListRepository.save(toDoList);
    }

    public ToDoListResponse update(UpdateRequest request, Long userID) {
        User user = getUser(userID);
        checkNotExist(user, request.getTitle(), request.getEndDate());
        checkExist(user, request.getChange(), request.getChangeDate());

        if (toDay().compareTo(request.getEndDate()) > 0)
            throw new IllegalArgumentException("이미 끝난 TDL은 변경이 불가능합니다.");

        // 제목 변경
        toDoListRepository.changeTitle(
                request.getChange(), request.getTitle(), userID,
                request.getChangeDate(), request.getEndDate()
        );

        ToDoList toDoList = toDoListRepository.findByUserAndTitleAndEndDate(
                user, request.getChange(), request.getChangeDate()
        );

        // AI 카테고리 분류 적용
        Map<String, List<Double>> categoryMap = categoryRepository.findAll().stream()
                .collect(Collectors.toMap(
                        Category::getName,
                        c -> categoryService.convertJsonToEmbedding(c.getEmbeddingJson())
                ));

        String newCategoryName = aiClassifierService.classifyCategoryWithAI(request.getChange(), categoryMap);

        Category newCategory = categoryService.findOrCreateByName(
                newCategoryName,
                categoryMap.containsKey(newCategoryName)
                        ? categoryMap.get(newCategoryName)
                        : categoryService.getEmbeddingFromText(newCategoryName)
        );

        toDoList.setCategory(newCategory); // 카테고리 업데이트
        toDoListRepository.save(toDoList); // 변경 사항 저장

        // 응답 객체 생성 및 SSE 공유
        ToDoListResponse response = toDoListResponseBuild(user, newCategory, toDoList);
        if(shareRepository.findByUser(user).isPresent()) {
            sendToShare(toDoList, response, user, "shared");
        }
        return response;
    }


    public void delete(DeleteRequest request,Long id) {
        User user = getUser(id);
        checkNotExist(user, request.getTitle(), request.getEndDate());

        ToDoList toDoList = toDoListRepository.findByUserAndTitleAndEndDate(user, request.getTitle(), request.getEndDate());
        toDoListRepository.deleteByUserAndTitleAndEndDate(user,request.getTitle(), request.getEndDate());
        ToDoListResponse response = toDoListResponseBuild(user, toDoList.getCategory(), toDoList);
        if(shareRepository.findByUser(user).isPresent()) {
            sendToShare(toDoList, response, user, "deleted");
        }
    }

    public List<ToDoListResponse> get(Long userID){
        User user = getUser(userID);
        List<ToDoList> toDoList = toDoListRepository.findByCurrentDateAndUserID(toDay(), userID);
        if(toDoList.isEmpty()){
            throw new IllegalArgumentException("오늘과 관련된 ToDoList가 없습니다.");
        }
        return toDoList.stream()
                .map(tdl -> ToDoListResponse.builder()
                        .title(tdl.getTitle())
                        .completed(tdl.getCompleted())
                        .category(tdl.getCategory().getName())  // 카테고리 이름을 포함
                        .endDate(tdl.getEndDate())
                        .startDate(tdl.getStartDate())
                        .userName(user.getName())
                        .shared(tdl.isShared())
                        .build())
                .collect(Collectors.toList());
    }

    public ToDoListResponse success(SuccessRequest request, Long userID) {
        String yearMonthDay = toDay();
        User user = getUser(userID);

        toDoListRepository.changeCompleted(request.getCompleted(), request.getTitle(), userID, yearMonthDay);
        ToDoList toDoList = toDoListRepository.findByUserAndTitleAndEndDate(user,request.getTitle(), yearMonthDay);

        ToDoListResponse response = toDoListResponseBuild(user, toDoList.getCategory(), toDoList);
        if(shareRepository.findByUser(user).isPresent()) {
            sendToShare(toDoList, response, user, "shared");
        }
        return response;
    }

    @Transactional
    public void shared(ShareRequest request, Long id){
        User user = getUser(id);
        ToDoList toDoList = toDoListRepository.findByUserAndTitleAndEndDate(user,request.getTitle(),request.getEndDate());
        ToDoList changed = toDoList.toBuilder().shared(request.getShared()).build();
        toDoListRepository.save(changed);
        ToDoListResponse response = toDoListResponseBuild(user, toDoList.getCategory(), changed);
        if(shareRepository.findByUser(user).isPresent()) {
            sendToShare(changed, response, user, "shared");
        }
    }

    @Transactional
    @Scheduled(cron = "59 59 23 * * *")
    public void finish(){
        List<User> users = userRepository.findAll();
        for(User user : users) {
            List<ToDoList> tdls = toDoListRepository.findByUserAndEndDate(user, toDay());
            int part = toDoListRepository.findByUserAndEndDateAndCompleted(user, toDay(), true).size();
            List<Calendar_tdl_ids> tdlIDs = tdls.stream()
                    .map(tdl ->  Calendar_tdl_ids.builder()
                            .tdlID(tdl.getId())
                            .kind(CTdlKind.PRIVATE)
                            .build())
                    .collect(Collectors.toList());

            Calendar calendar = Calendar.builder()
                    .user(user)
                    .every(tdlIDs.size())
                    .part(part)
                    .kind("PRIVATE")
                    .toDoListId(tdlIDs)
                    .build();
            calendarRepository.save(calendar);
        }
    }

    public SseEmitter sseConnect(Long shareNumber){
        SseEmitter emitter = new SseEmitter(300 * 1000L);
        shareEmitters.computeIfAbsent(shareNumber,  key -> new CopyOnWriteArrayList<>())
                .add(emitter);
        shareEmitters.get(shareNumber).add(emitter);

        emitter.onCompletion(() -> shareEmitters.get(shareNumber).remove(emitter));
        emitter.onTimeout(() -> shareEmitters.get(shareNumber).remove(emitter));
        emitter.onError(e -> shareEmitters.get(shareNumber).remove(emitter));

        try {
            emitter.send(SseEmitter.event().name("connected").data("SSE 연결됨 (공유 : " + shareNumber + ")"));
        } catch (IOException e) {
            shareEmitters.get(shareNumber).remove(emitter);
        }
        return emitter;
    }


    @Transactional
    public void accept(Long userId, ShareChoiceRequest request){
        User user = getUserByID(userId);
        Share share = getShareByShareNumber(shareNumberRepo.findById(request.getShareNumber()).orElseThrow(()-> new IllegalArgumentException("존재하지 않는 shareNumber입니다.")),user);

        Share change = share.toBuilder().accepted(true).build();
        shareRepository.save(change);

        MemberDto response = MemberDto.builder()
                .userCode(user.getUserCode())
                .email(user.getEmail())
                .name(user.getName())
                .build();

        List<SseEmitter> emitters = shareEmitters.get(share.getShareNumber().getId());
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("share-member")
                        .data(response));
            } catch (IOException e) {
                log.error("SSE 전송 실패: {}", e.getMessage());
                emitters.remove(emitter); // 전송 실패하면 제거
            }
        }
    }

    @Transactional
    public void refuse(Long userId,ShareChoiceRequest request){
        User user = getUserByID(userId);
        Share share = getShareByShareNumber(shareNumberRepo.findById(request.getShareNumber()).orElseThrow(()-> new IllegalArgumentException("존재하지 않는 shareNumber입니다.")),user);

        shareRepository.deleteById(share.getId());
    }

    public String toDay(){
        LocalDateTime createAt = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDateTime();
        DateTimeFormatter yearMonthDayFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return createAt.format(yearMonthDayFormatter);
    }

    public User getUser(Long id){
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("존재하지 않은 UserID"));
    }

    public void checkNotExist(User user, String title, String endDate){
        if (!toDoListRepository.existsByUserAndTitleAndEndDate(user,title, endDate)){
            throw new IllegalArgumentException("존재하지 않는 TDL입니다.");
        }
    }

    public void checkExist(User user, String title,String endDate){
        if (toDoListRepository.existsByUserAndTitleAndEndDate(user,title, endDate)){
            throw new IllegalArgumentException("이미 존재하는 TDL입니다.");
        }
    }

    public void inputSetting(String title, User user, String endDate, Category category) {
        checkExist(user, title, endDate);
        checkCategory(category);
    }

    public void checkCategory(Category category){
        if (category == null) {
            throw new IllegalArgumentException("존재하지 않은 카테고리입니다.");
        }
    }

    public void checkEndDate(String endDate){
        if (toDay().compareTo(endDate) > 0){
            throw new IllegalArgumentException("끝나는 날짜는 현재 날짜보다 빠를 수 없습니다.");
        }
    }

    @PostConstruct
    public void startPingTask() {
        scheduler.scheduleAtFixedRate(() -> {
            for (Map.Entry<Long, CopyOnWriteArrayList<SseEmitter>> entry : shareEmitters.entrySet()) {
                List<SseEmitter> emitters = entry.getValue();
                for (SseEmitter emitter : emitters) {
                    try {
                        emitter.send(SseEmitter.event()
                                .name("ping")
                                .data("keepalive"));
                    } catch (IOException e) {
                        log.info("Ping 실패로 emitter 제거: {}", e.getMessage());
                        emitter.completeWithError(e);
                        emitters.remove(emitter);
                    }
                }
            }
        }, 10, 30, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
    }

    Share getShareByUser(User user){
        return shareRepository.findByUser(user).orElseThrow(()-> new IllegalArgumentException("공유 TDL에 참가하지 않은 사용자입니다."));
    }

    Long getShareNum(User user) {
        Share share = getShareByUser(user);
        ShareNumber shareNumber = share.getShareNumber();
        return shareNumber.getId();
    }

    ToDoListResponse toDoListResponseBuild(User user, Category category, ToDoList toDoList) {
        return ToDoListResponse.builder()
                .userName(user.getName())
                .category(category.getName())
                .shared(toDoList.isShared())
                .title(toDoList.getTitle())
                .endDate(toDoList.getEndDate())
                .completed(toDoList.getCompleted())
                .startDate(toDoList.getStartDate())
                .build();
    }

    void sendToShare(ToDoList toDoList, ToDoListResponse response, User user, String name)  {
        Long shareNumber = getShareNum(user);
        List<SseEmitter> emitters = shareEmitters.get(shareNumber);
        if (toDoList.isShared()) {
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(SseEmitter.event()
                            .name(name)
                            .data(response));
                } catch (Exception e) {
                    emitters.remove(emitter); // 전송 실패하면 제거
                }
            }
        }
    }
    User getUserByID(Long userID){
        return userRepository.findById(userID).orElseThrow(()-> new IllegalArgumentException("없는 유저입니다.(ID)"));
    }
    Share getShareByShareNumber(ShareNumber shareNumber, User user){
        return shareRepository.findByShareNumberAndUserAndAcceptedFalse(shareNumber,user).orElseThrow(()-> new IllegalArgumentException("존재하지 않는 공유 방입니다."));
    }


}
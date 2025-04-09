package festival.dev.domain.gorupTDL.presentation;

import festival.dev.domain.gorupTDL.presentation.dto.request.*;
import festival.dev.domain.gorupTDL.service.GroupService;
import festival.dev.domain.user.entity.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("group/toDoList")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping("/invite")
    public ResponseEntity<?> invite(@Valid @RequestBody GInviteReq request, @AuthenticationPrincipal CustomUserDetails user) {
        try {
            groupService.invite(request,user.getUserID());
            return ResponseEntity.ok("success");
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //자기 자신이 groupjoin에 들어가게 만들기.
    @PostMapping("/insert")
    public ResponseEntity<?> insert(@Valid @RequestBody GInsertRequest request,@AuthenticationPrincipal CustomUserDetails user) {
        try {
            return ResponseEntity.ok(groupService.invite(request,user.getUserID())    );
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //수락하면 groupJoin에 추가되기
    @PutMapping("/accept")
    public ResponseEntity<?> accept(@Valid @RequestBody GChoiceRequest request,@AuthenticationPrincipal CustomUserDetails user) {
        try{
            groupService.acceptInvite(request,user.getUserID());
            return ResponseEntity.ok("success");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/refuse")
    public ResponseEntity<?> refuse(@Valid @RequestBody GChoiceRequest request,@AuthenticationPrincipal CustomUserDetails user) {
        try{
            groupService.refuseInvite(request,user.getUserID());
            return ResponseEntity.ok("success");
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //groupNumber도 받아야함. 그래서 그걸 기준으로 값을 바꿔야함.
    @PutMapping("/modify")
    public ResponseEntity<?> modify(@Valid @RequestBody GUpdateRequest request, @AuthenticationPrincipal CustomUserDetails user) {
        try{
            return ResponseEntity.ok(groupService.update(request,user.getUserID()));
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //groupNumber도 받아서 delete
    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@Valid @RequestBody GDeleteRequest request, @AuthenticationPrincipal CustomUserDetails user) {
        try{
            groupService.delete(request,user.getUserID());
            return ResponseEntity.ok("success");
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("test")
    public ResponseEntity<?> test(){
        return ResponseEntity.ok("success");
    }
}
package festival.dev.domain.gorupTDL.presentation;

import festival.dev.domain.gorupTDL.presentation.dto.request.*;
import festival.dev.domain.gorupTDL.presentation.dto.response.GInsertRes;
import festival.dev.domain.gorupTDL.presentation.dto.response.GResponse;
import festival.dev.domain.gorupTDL.service.GroupService;
import festival.dev.domain.user.entity.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("group/toDoList")
@RequiredArgsConstructor
public class GroupController {
    private static final Logger log = LoggerFactory.getLogger(GroupController.class);
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

    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody GCreateRequest request, @AuthenticationPrincipal CustomUserDetails user) {
        try {
            return ResponseEntity.ok(groupService.invite(request,user.getUserID()));
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/accept")
    public ResponseEntity<?> accept(@AuthenticationPrincipal CustomUserDetails user, @Valid @RequestBody GChoiceReq req) {
        try{
            groupService.acceptInvite(user.getUserID(),req);
            return ResponseEntity.ok("success");
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/refuse")
    public ResponseEntity<?> refuse(@AuthenticationPrincipal CustomUserDetails user, @Valid @RequestBody GChoiceReq req) {
        try{
            groupService.refuseInvite(user.getUserID(),req);
            return ResponseEntity.ok("success");
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/modify")
    public ResponseEntity<?> modify(@Valid @RequestBody GUpdateRequest request, @AuthenticationPrincipal CustomUserDetails user) {
        try{
            return ResponseEntity.ok(groupService.update(request,user.getUserID()));
        }
        catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

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

    @PutMapping("success")
    public ResponseEntity<?> success(@Valid @RequestBody GSuccessRequest request, @AuthenticationPrincipal CustomUserDetails user) {
        try{
            GResponse response = groupService.success(request,user.getUserID());
            return ResponseEntity.ok(response);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/get")
    public ResponseEntity<?> get(@AuthenticationPrincipal CustomUserDetails user) {
        try {
            return ResponseEntity.ok(groupService.get(user.getUserID()));
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/insert")
    public ResponseEntity<?> insert(@AuthenticationPrincipal CustomUserDetails user, @Valid @RequestBody GInsertRequest request) {
        try {
            return ResponseEntity.ok(GInsertRes.builder().groupNumber(groupService.insert(request,user.getUserID())).build());
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/all")
    public ResponseEntity<?> deleteAll(@AuthenticationPrincipal CustomUserDetails user){
       try {
           groupService.deleteAll(user.getUserID());
           return ResponseEntity.ok("success");
       }catch (Exception e) {
           return ResponseEntity.badRequest().body(e.getMessage());
       }
    }

    @GetMapping("/invite")
    public ResponseEntity<?> inviteGet(@AuthenticationPrincipal CustomUserDetails user){
        try{
            return ResponseEntity.ok(groupService.inviteGet(user.getUserID()));
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/userlist")
    public ResponseEntity<?> userList(@AuthenticationPrincipal CustomUserDetails user){
        try{
            return ResponseEntity.ok(groupService.userList(user.getUserID()));
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
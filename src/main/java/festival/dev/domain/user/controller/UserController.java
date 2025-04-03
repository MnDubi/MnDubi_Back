package festival.dev.domain.user.controller;

import festival.dev.domain.user.entity.User;
import festival.dev.domain.user.dto.UserDto;
import festival.dev.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.auth0.jwt.JWT;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


//    @GetMapping("/{id}")
//    public ResponseEntity<User> getUserById(@PathVariable Long id) {
//        return ResponseEntity.ok(userService.findById(id));
//    }


    @GetMapping("/profile")
    public ResponseEntity<UserDto> getProfile(@RequestHeader("Authorization") String authorization) {
        Long userId = JWT.decode(authorization.replace("Bearer ", "")).getClaim("userId").asLong();
        return ResponseEntity.ok(userService.getUserById(userId));
    }


    @GetMapping("/{email}")
    public UserDto getUser(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }


}

